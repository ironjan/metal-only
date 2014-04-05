package com.codingspezis.android.metalonly.player.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.codingspezis.android.metalonly.player.BuildConfig;
import com.codingspezis.android.metalonly.player.MainActivity;
import com.codingspezis.android.metalonly.player.R;
import com.codingspezis.android.metalonly.player.wish.AllowedActions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_wish)
@OptionsMenu(R.menu.help)
public class WishFragment extends SherlockFragment {
    private static final Logger LOGGER = LoggerFactory.getLogger(WishFragment.class.getSimpleName());

    // URL to wish script on metal-only.de
    public static final String URL_WISHES = "http://metalonly.de/?action=wunschscript";

    // intent keys
    public static final String KEY_WISHES_ALLOWED = "MO_WISHES_ALLOWED";
    public static final String KEY_REGARDS_ALLOWED = "MO_REGARDS_ALLOWED";
    public static final String KEY_NUMBER_OF_WISHES = "MO_NUMBER_OF_WISHES";
    public static final String KEY_DEFAULT_INTERPRET = "MO_DEFAULT_INTERPRET";
    public static final String KEY_DEFAULT_TITLE = "MO_DEFAULT_TITLE";

    // shared preferences keys
    public static final String KEY_SP_NICK = "moa_nickname";

    boolean wish, regard;

    @ViewById(R.id.btnSend)
    Button buttonSend;

    @ViewById
    EditText editNick,
            editArtist,
            editTitle,
            editRegard;
    @ViewById
    TextView textArtist,
            textTitle,
            textRegard;

    private String numberOfWishes;
    @ViewById(R.id.txtWishcount)
    TextView wishCount;

    @AfterViews
    public void init() {
        if(BuildConfig.DEBUG) LOGGER.debug("init()");

            SharedPreferences settings = getActivity().getSharedPreferences(getString(R.string.app_name),
                Context.MODE_MULTI_PROCESS);

        // input fields
        editNick.setText(settings.getString(KEY_SP_NICK, ""));


        // get parameters
        final Bundle bundle = getArguments();



        if (bundle != null) {
            wish = bundle.getBoolean(KEY_WISHES_ALLOWED, false);
            regard = bundle.getBoolean(KEY_REGARDS_ALLOWED, false);
            numberOfWishes = bundle.getString(KEY_NUMBER_OF_WISHES);
            editArtist.setText(bundle.getString(KEY_DEFAULT_INTERPRET));
            editTitle.setText(bundle.getString(KEY_DEFAULT_TITLE));
        }

        wishCount.setText(numberOfWishes);
        if (!wish) {
            editArtist.setText(R.string.no_wishes_short);
            editArtist.setEnabled(false);
            setInvisible(editArtist);
            setInvisible(textArtist);
            editTitle.setText(R.string.no_wishes_short);
            editTitle.setEnabled(false);
            setInvisible(editTitle);
            setInvisible(textTitle);

            wishCount.setText(wishCount.getText() + "\n" + getString(R.string.no_wishes_short));

        }
        if (!regard) {
            editRegard.setText(R.string.no_regards);
            editRegard.setEnabled(false);
            setInvisible(editRegard);
            setInvisible(textRegard);

            wishCount.setText(wishCount.getText() + "\n" + getString(R.string.no_regards));
        }

        if(BuildConfig.DEBUG) LOGGER.debug("init() done");

    }

    @Override
    public void onPause() {
        if(BuildConfig.DEBUG) LOGGER.debug("onPause()");

        SharedPreferences settings = getActivity().getSharedPreferences(getString(R.string.app_name),
                Context.MODE_MULTI_PROCESS);

        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_SP_NICK, editNick.getText().toString());
        editor.commit();
        super.onPause();

        if(BuildConfig.DEBUG) LOGGER.debug("onPause() done");
    }

    private void setInvisible(View v) {
        if(BuildConfig.DEBUG) LOGGER.debug("setInvisible({})",v);

        if (v != null)
            v.setVisibility(View.GONE);

        if(BuildConfig.DEBUG) LOGGER.debug("setInvisible({}) done",v);
    }


    /**
     * checks edit text objects for valid data
     *
     * @return true if input is valid - false otherwise
     */
    private boolean haveValidData() {
        if(BuildConfig.DEBUG) LOGGER.debug("haveValidData()");

        boolean haveNick = !TextUtils.isEmpty(editNick.getText());
        boolean haveWish = !TextUtils.isEmpty(editArtist.getText())
                && !TextUtils.isEmpty(editTitle.getText()) && editArtist.isEnabled()
                && editTitle.isEnabled();
        boolean haveRegard = !TextUtils.isEmpty(editRegard.getText())
                && editRegard.isEnabled();

        final boolean isValidData = haveNick && (haveWish || haveRegard);

        if(BuildConfig.DEBUG) LOGGER.debug("haveValidData() -> {}",isValidData);
        return isValidData;
    }

    @Click(R.id.btnSend)
    void sendButtonClicked() {
        if(BuildConfig.DEBUG) LOGGER.debug("sendButtonClicked()");

        if (!haveValidData()) {
            if(BuildConfig.DEBUG) LOGGER.debug("sendButtonClicked: invalid data");
            notifyUser(R.string.invalid_input);
        } else {
            if(BuildConfig.DEBUG) LOGGER.debug("sendButtonClicked: valid data, sending");
            (new GetSender()).start();
        }


        if(BuildConfig.DEBUG) LOGGER.debug("sendButtonClicked() done");
    }

    @UiThread
    void notifyUser(String s) {
        Toast.makeText(getActivity(),
                s, Toast.LENGTH_SHORT)
                .show();
    }

    @UiThread
    void notifyUser(int id) {
        Toast.makeText(getActivity(),
                id, Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * shows info
     */
    @OptionsItem(R.id.mnu_help)
    void showInfo() {
        if(BuildConfig.DEBUG) LOGGER.debug("showInfo()");

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(R.string.notification);
        final View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_text, null);
        TextView tv = (TextView) v.findViewById(R.id.text);
        tv.setText(R.string.wish_explanation);
        alert.setView(v);
        alert.setPositiveButton(R.string.ok, null);
        alert.show();


        if(BuildConfig.DEBUG) LOGGER.debug("showInfo() done");
    }

    public static WishFragment newInstance(Bundle bundle) {
        if(BuildConfig.DEBUG) LOGGER.debug("newInstance({})", bundle);

        WishFragment wishFragment = new WishFragment_();
        wishFragment.setArguments(bundle);

        if(BuildConfig.DEBUG) LOGGER.debug("newInstance({}) done", bundle);
        return wishFragment;
    }

    /**
     * GetSender
     */
    private class GetSender extends Thread {

        private final Logger SENDER_LOGGER = LoggerFactory.getLogger(GetSender.class.getSimpleName());
        
        // TODO move this to own class
        @Override
        public void run() {
            if(BuildConfig.DEBUG) SENDER_LOGGER.debug("run()");

            // TODO refactor this method
            // generate url
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(
                    "http://www.metal-only.de/?action=wunschscript&do=save");
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            // add post values
            
            if (!TextUtils.isEmpty(editNick.getText())) {
                pairs.add(new BasicNameValuePair("nick", editNick.getText()
                        .toString()));
            }
            if (!TextUtils.isEmpty(editArtist.getText())
                    && editArtist.isEnabled()) {
                pairs.add(new BasicNameValuePair("artist", editArtist.getText()
                        .toString()));
            }
            if (!TextUtils.isEmpty(editTitle.getText())
                    && editTitle.isEnabled()) {
                pairs.add(new BasicNameValuePair("song", editTitle.getText()
                        .toString()));
            }
            if (!TextUtils.isEmpty(editRegard.getText())) {
                pairs.add(new BasicNameValuePair("greet", editRegard.getText()
                        .toString()));
            }

            try {
                // generate entity
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs,
                        "UTF-8");
                entity.setContentEncoding(HTTP.UTF_8);
                // send post
                post.setEntity(entity);
                HttpResponse response = client.execute(post);
                if (!response.getStatusLine().toString()
                        .equals("HTTP/1.1 200 OK")) {
                    Exception e = new Exception(
                            getString(R.string.sending_error));
                    throw e;
                }
                notifyUser(R.string.sent);
                getActivity().finish();

            } catch (final Exception e) {
                notifyUser(e.toString());
            }

            if(BuildConfig.DEBUG) SENDER_LOGGER.debug("run() done");
        }
    }

}
