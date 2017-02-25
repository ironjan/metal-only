package com.codingspezis.android.metalonly.player.fragments;

import android.app.*;
import android.content.*;
import android.os.*;
import android.text.*;
import android.view.*;
import android.widget.*;

import com.codingspezis.android.metalonly.player.*;
import com.codingspezis.android.metalonly.player.utils.UrlConstants;

import org.androidannotations.annotations.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.message.*;
import org.apache.http.protocol.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;

@EFragment(R.layout.fragment_wish)
@OptionsMenu(R.menu.help)
public class WishFragment extends SherlockFragment {
    private static final Logger LOGGER = LoggerFactory.getLogger(WishFragment.class.getSimpleName());
    // intent keys
    private static final String KEY_WISHES_ALLOWED = WishActivity.KEY_WISHES_ALLOWED;
    private static final String KEY_REGARDS_ALLOWED = WishActivity.KEY_REGARDS_ALLOWED;
    private static final String KEY_NUMBER_OF_WISHES = WishActivity.KEY_NUMBER_OF_WISHES;
    private static final String KEY_DEFAULT_INTERPRET = WishActivity.KEY_DEFAULT_INTERPRET;
    private static final String KEY_DEFAULT_TITLE = WishActivity.KEY_DEFAULT_TITLE;
    // shared preferences keys
    private static final String KEY_SP_NICK = "moa_nickname";
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
    @ViewById(R.id.txtWishcount)
    TextView wishCount;
    private boolean wish, regard;
    private String numberOfWishes;

    public WishFragment() {
    }

    private static void setInvisible(View v) {
        if (BuildConfig.DEBUG) LOGGER.debug("setInvisible({})", v);

        if (null != v) {
            v.setVisibility(View.GONE);
        }

        if (BuildConfig.DEBUG) LOGGER.debug("setInvisible({}) done", v);
    }

    @SuppressWarnings({"LocalVariableOfConcreteClass", "MethodReturnOfConcreteClass"})
    public static WishFragment newInstance(Bundle bundle) {
        if (BuildConfig.DEBUG) LOGGER.debug("newInstance({})", bundle);

        WishFragment wishFragment = new WishFragment_();
        wishFragment.setArguments(bundle);

        if (BuildConfig.DEBUG) LOGGER.debug("newInstance({}) done", bundle);
        return wishFragment;
    }

    @AfterViews
    void init() {
        if (BuildConfig.DEBUG) LOGGER.debug("init()");

        final String app_name = getString(R.string.app_name);
        final SharedPreferences settings = getActivity().getSharedPreferences(app_name,
                Context.MODE_MULTI_PROCESS);

        // input fields
        final String nickName = settings.getString(KEY_SP_NICK, "");
        editNick.setText(nickName);


        // get parameters
        Bundle bundle = getArguments();


        if (null != bundle) {
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

        if (BuildConfig.DEBUG) LOGGER.debug("init() done");

    }

    @Override
    public void onPause() {
        if (BuildConfig.DEBUG) LOGGER.debug("onPause()");

        final String app_name = getString(R.string.app_name);
        SharedPreferences settings = getActivity().getSharedPreferences(app_name,
                Context.MODE_MULTI_PROCESS);

        SharedPreferences.Editor editor = settings.edit();
        final String nickName = editNick.getText().toString();
        editor.putString(KEY_SP_NICK, nickName);
        editor.commit();
        super.onPause();

        if (BuildConfig.DEBUG) LOGGER.debug("onPause() done");
    }

    /**
     * checks edit text objects for valid data
     *
     * @return true if input is valid - false otherwise
     */
    private boolean haveValidData() {
        if (BuildConfig.DEBUG) LOGGER.debug("haveValidData()");

        boolean haveNick = !TextUtils.isEmpty(editNick.getText());
        final boolean artistEnabledAndHasText = !TextUtils.isEmpty(editArtist.getText()) && editArtist.isEnabled();
        final boolean titleEnabledAndHasText = !TextUtils.isEmpty(editTitle.getText())
                && editTitle.isEnabled();
        boolean haveWish = artistEnabledAndHasText
                && titleEnabledAndHasText;
        boolean haveRegard = !TextUtils.isEmpty(editRegard.getText())
                && editRegard.isEnabled();

        boolean isValidData = haveNick && (haveWish || haveRegard);

        if (BuildConfig.DEBUG) LOGGER.debug("haveValidData() -> {}", isValidData);
        return isValidData;
    }

    @Click(R.id.btnSend)
    void sendButtonClicked() {
        if (BuildConfig.DEBUG) LOGGER.debug("sendButtonClicked()");

        if (haveValidData()) {
            if (BuildConfig.DEBUG) LOGGER.debug("sendButtonClicked: valid data, sending");
            (new GetSender()).start();

        } else {
            if (BuildConfig.DEBUG) LOGGER.debug("sendButtonClicked: invalid data");
            notifyUser(R.string.invalid_input);
        }


        if (BuildConfig.DEBUG) LOGGER.debug("sendButtonClicked() done");
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
        if (BuildConfig.DEBUG) LOGGER.debug("showInfo()");

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(R.string.notification);
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_text, null);
        TextView tv = (TextView) v.findViewById(R.id.text);
        tv.setText(R.string.wish_explanation);
        alert.setView(v);
        alert.setPositiveButton(R.string.ok, null);
        alert.show();


        if (BuildConfig.DEBUG) LOGGER.debug("showInfo() done");
    }

    /**
     * GetSender
     */
    private class GetSender extends Thread {

        private final Logger SENDER_LOGGER = LoggerFactory.getLogger(GetSender.class.getSimpleName());

        // TODO move this to own class
        @SuppressWarnings("RefusedBequest")
        @Override
        public void run() {
            if (BuildConfig.DEBUG) SENDER_LOGGER.debug("run()");

            // TODO refactor this method
            // generate url
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(UrlConstants.METAL_ONLY_WUNSCHSCRIPT_POST_URL);
            List<NameValuePair> pairs = new ArrayList<NameValuePair>(4);
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
                if (response.getStatusLine().toString().equals("HTTP/1.1 200 OK")) {
                    notifyUser(R.string.sent);
                    getActivity().finish();
                } else {
                    notifyUser(R.string.sending_error);
                }
            } catch (ClientProtocolException e) {
                notifyUser(e.getMessage());
            } catch (UnsupportedEncodingException e) {
                notifyUser(e.getMessage());
            } catch (IOException e) {
                notifyUser(e.getMessage());
            }

            if (BuildConfig.DEBUG) SENDER_LOGGER.debug("run() done");
        }
    }

}
