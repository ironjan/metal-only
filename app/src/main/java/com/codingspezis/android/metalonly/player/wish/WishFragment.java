package com.codingspezis.android.metalonly.player.wish;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codingspezis.android.metalonly.player.BuildConfig;
import com.codingspezis.android.metalonly.player.R;
import com.codingspezis.android.metalonly.player.WishActivity;
import com.codingspezis.android.metalonly.player.siteparser.HTTPGrabber;
import com.codingspezis.android.metalonly.player.utils.jsonapi.MetalOnlyAPIWrapper;
import com.codingspezis.android.metalonly.player.utils.jsonapi.NoInternetException;
import com.codingspezis.android.metalonly.player.utils.jsonapi.Stats;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

@EFragment(R.layout.fragment_wish)
@OptionsMenu(R.menu.wishmenu)
public class WishFragment extends Fragment implements WishSender.Callback {
    private static final Logger LOGGER = LoggerFactory.getLogger(WishFragment.class.getSimpleName());

    @ViewById(R.id.btnSend)
    Button buttonSend;
    @ViewById
    EditText editNick,
            editArtist,
            editTitle,
            editRegard;
    @ViewById
    Button btnSend;

    @FragmentArg(WishActivity.KEY_DEFAULT_INTERPRET)
    String interpret;
    @FragmentArg(WishActivity.KEY_DEFAULT_TITLE)
    String title;

    @ViewById(android.R.id.progress)
    ProgressBar progress;

    @StringRes
    String number_of_wishes_format,
            no_regards,
            app_name,
            no_wishes_short;

    private Stats stats = Stats.getDefault();

    @ViewById
    TextView textArtist,
            textTitle,
            textRegard;

    @ViewById(R.id.txtWishcount)
    TextView wishCount;

    @Bean
    MetalOnlyAPIWrapper apiWrapper;

    @Pref
    WishPrefs_ wishPrefs;
    private boolean didNotCompleteLoadingStats = true;

    public WishFragment() {
    }

    /**
     * Loads the actions that are allowed in this show. Needs to be called {@link AfterViews}
     * because we're updating the UI when getting a result.
     */
    @AfterViews
    @Background
    void loadAllowedActions() {
        if (HTTPGrabber.isOnline(getActivity())) {
            showLoading(true);
            updateStats(apiWrapper.getStats());
        } else {
            notifyUser(R.string.no_internet);
        }
    }

    @UiThread
    void updateStats(Stats stats) {
        this.stats = stats;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.GERMAN, number_of_wishes_format, stats.getWishLimit()));

        // FIXME Disable in layout, enable after load
        // FIXME distinguish: wish list full, no wishes possbile!
        if (!stats.isCanWish()) {
            editArtist.setText(no_wishes_short);
            editArtist.setEnabled(false);
            editTitle.setText(no_wishes_short);
            editTitle.setEnabled(false);
        }

        if (!stats.isCanGreet()) {
            editRegard.setText(no_regards);
            editRegard.setEnabled(false);

            sb.append("\n").append(no_regards);
        }

        wishCount.setText(sb.toString());
        showLoading(false);
        didNotCompleteLoadingStats = false;
    }

    @UiThread
    void showLoading(boolean isLoading) {
        if (isLoading) {
            btnSend.setEnabled(false);
            progress.setVisibility(View.VISIBLE);
        } else {
            btnSend.setEnabled(true);
            progress.setVisibility(View.INVISIBLE);
        }
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
    void loadUiValues() {
        boolean wasGivenFragmentArgs = (interpret != null) && (title != null);
        if(wasGivenFragmentArgs) {
            editArtist.setText(interpret);
            editTitle.setText(title);
        } else {
            editTitle.setText(wishPrefs.title().get());
            editRegard.setText(wishPrefs.greeting().get());
            editArtist.setText(wishPrefs.artist().get());
        }

        editNick.setText(wishPrefs.nick().get());
    }

    @Override
    public void onPause() {
        saveInputInPrefs();
        super.onPause();
    }

    private void saveInputInPrefs() {
        wishPrefs.edit()
                .nick().put(editNick.getText().toString())
                .artist().put(editArtist.getText().toString())
                .title().put(editTitle.getText().toString())
                .greeting().put(editRegard.getText().toString()).
                apply();
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
    @OptionsItem(R.id.ic_menu_send)
    void sendButtonClicked() {
        if (BuildConfig.DEBUG) LOGGER.debug("sendButtonClicked()");

        if(didNotCompleteLoadingStats){
            return;
        }
        if (haveValidData()) {
            showLoading(true);
            sendWishGreet();
        } else {
            notifyUser(R.string.invalid_input);
        }


        if (BuildConfig.DEBUG) LOGGER.debug("sendButtonClicked() done");
    }

    @Background
    void sendWishGreet() {
        try {
            final String nick = editNick.getText().toString();
            final String artist = editArtist.getText().toString();
            final String title = editTitle.getText().toString();
            final String greet = editRegard.getText().toString();

            if (stats.isCanWish() && !TextUtils.isEmpty(artist) && !TextUtils.isEmpty(title)) {
                new WishSender(this, nick, greet, artist, title).send();
            } else {
                new WishSender(this, nick, greet, null, null).send();
            }
        } catch (NoInternetException e) {
            notifyUser(R.string.no_internet);
        }
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

    @Override
    public void onSuccess() {
        showLoading(false);
        notifyUser(R.string.sent);
        clearSongAndWish();
        getActivity().finish();
    }

    @Override
    public void onFail() {
        showLoading(false);
        notifyUser(R.string.sending_error);
        enableSendButton();
    }

    @Override
    public void onException(Exception e) {
        showLoading(false);
        notifyUser(e.getMessage());
        enableSendButton();
    }

    @UiThread
    void enableSendButton() {
        buttonSend.setEnabled(true);
        buttonSend.setText(R.string.wish_send);
    }

    @Click(R.id.btnClear)
    void btnClearClicked(){
        clearSongAndWish();
    }

    @UiThread
    void clearSongAndWish() {
        editArtist.setText("");
        editTitle.setText("");
        editRegard.setText("");
        saveInputInPrefs();
    }
}
