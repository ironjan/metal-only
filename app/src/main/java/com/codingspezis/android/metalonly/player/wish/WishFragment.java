package com.codingspezis.android.metalonly.player.wish;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
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
import com.codingspezis.android.metalonly.player.core.WishAndGreetConstraints;
import com.codingspezis.android.metalonly.player.siteparser.HTTPGrabber;
import com.codingspezis.android.metalonly.player.wish.WishPrefs_.WishPrefsEditor_;
import com.github.ironjan.metalonly.client_library.MetalOnlyClient;
import com.github.ironjan.metalonly.client_library.NoInternetException;
import com.github.ironjan.metalonly.client_library.WishSender;
import com.hypertrack.hyperlog.HyperLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.springframework.web.client.RestClientException;

import java.util.Locale;

@EFragment(R.layout.fragment_wish)
@OptionsMenu(R.menu.wishmenu)
public class WishFragment extends Fragment implements WishSender.Callback {
    private static final String TAG = WishFragment.class.getSimpleName();

    @ViewById(R.id.btnSend)
    Button buttonSend;
    @ViewById
    EditText editNick;
    @ViewById
    EditText editArtist;
    @ViewById
    EditText editTitle;
    @ViewById
    EditText editRegard;
    @ViewById
    Button btnSend;

    @FragmentArg(WishActivity.KEY_DEFAULT_INTERPRET)
    String interpret;
    @FragmentArg(WishActivity.KEY_DEFAULT_TITLE)
    String title;

    @ViewById(android.R.id.progress)
    ProgressBar progress;

    @StringRes
    String number_of_wishes_format;
    @StringRes
    String no_regards;
    @StringRes
    String wish_list_full;

    private WishAndGreetConstraints stats;

    @ViewById
    TextView textArtist;
    @ViewById
    TextView textTitle;
    @ViewById
    TextView textRegard;

    @ViewById(R.id.txtWishcount)
    TextView wishCount;

    @SuppressWarnings("InstanceVariableOfConcreteClass")
    @Pref
    WishPrefs_ wishPrefs;
    private boolean didNotCompleteLoadingStats = true;

    /**
     * Loads the actions that are allowed in this show. Needs to be called {@link AfterViews}
     * because we're updating the UI when getting a result.
     */
    @AfterViews
    @Background
    void loadAllowedActions() {

        FragmentActivity activity = getActivity();

        if (activity == null) return;

        if (HTTPGrabber.isOnline(activity)) {
            showLoading(true);
            try {
                Context context = getContext();
                if (context == null) {
                    return;
                }
                updateStats(MetalOnlyClient.Companion.getClient(context).getStats());
            } catch (NoInternetException | RestClientException e) {
                loadingAllowedActionsFailed();
            }
        } else {
            notifyUser(R.string.no_internet);
        }
    }

    @UiThread
    void loadingAllowedActionsFailed() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            Toast toast = Toast.makeText(activity, R.string.stats_failed_to_load, Toast.LENGTH_LONG);
            if (toast != null) {
                toast.show();
            }
        }
    }

    @UiThread
    void updateStats(WishAndGreetConstraints stats) {
        this.stats = stats;
        if (stats == null) {
            return;
        }
        StringBuilder sb = new StringBuilder(0);

        if (number_of_wishes_format != null) {
            sb.append(String.format(Locale.GERMAN, number_of_wishes_format, stats.getWishLimit()));
        }

        sb.append(' ');// Append space to keep distance with next text

        if (stats.getCanWish()) {
            if (editArtist != null) {
                editArtist.setText("");
            }
            if (editArtist != null) {
                editArtist.setEnabled(true);
            }
            if (editTitle != null) {
                editTitle.setText("");
            }
            if (editTitle != null) {
                editTitle.setEnabled(true);
            }
        } else {
            if (editArtist != null) {
                editArtist.setText(R.string.no_wishes_short);
            }
            if (editArtist != null) {
                editArtist.setEnabled(false);
            }
            if (editTitle != null) {
                editTitle.setText(R.string.no_wishes_short);
            }
            if (editTitle != null) {
                editTitle.setEnabled(false);
            }
        }

        if (stats.getCanGreet()) {
            if (editRegard != null) {
                editRegard.setText("");
            }
            if (editRegard != null) {
                editRegard.setEnabled(true);
            }
        } else {
            if (editRegard != null) {
                editRegard.setText(R.string.no_regards);
            }
            if (editRegard != null) {
                editRegard.setEnabled(false);
            }

            sb.append('\n');
            sb.append(no_regards);
        }

        if (stats.getWishLimitReached()) {
            sb.append(wish_list_full);
            if (editArtist != null) {
                editArtist.setText(R.string.wish_list_full_short);
            }
            if (editArtist != null) {
                editArtist.setEnabled(false);
            }
            if (editArtist != null) {
                editArtist.setEnabled(false);
            }
            if (editTitle != null) {
                editTitle.setText(R.string.wish_list_full_short);
            }
            if (editTitle != null) {
                editTitle.setEnabled(false);
            }
            if (editTitle != null) {
                editTitle.setEnabled(false);
            }
        }


        if (wishCount != null) {
            wishCount.setText(sb.toString());
        }

        showLoading(false);
        didNotCompleteLoadingStats = false;
    }

    @UiThread
    void showLoading(boolean isLoading) {
        if ((btnSend == null) || (progress == null)) {
            return;
        }

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
        WishFragment wishFragment = new WishFragment_();
        wishFragment.setArguments(bundle);

        return wishFragment;
    }

    @AfterViews
    void loadUiValues() {
        boolean wasGivenFragmentArgs = (interpret != null) && (title != null);
        if (wasGivenFragmentArgs) {
            if (editArtist != null) {
                editArtist.setText(interpret);
            }
            if (editTitle != null) {
                editTitle.setText(title);
            }
        } else {
            if (wishPrefs == null) {
                return;
            }

            if (editTitle != null) {
                //noinspection ConstantConditions
                editTitle.setText(wishPrefs.title().get());
            }

            if (editRegard != null) {
                //noinspection ConstantConditions
                editRegard.setText(wishPrefs.greeting().get());
            }

            if (editArtist != null) {
                //noinspection ConstantConditions
                editArtist.setText(wishPrefs.artist().get());
            }
        }

        if (editNick != null) {
            //noinspection ConstantConditions
            editNick.setText(wishPrefs.nick().get());
        }
    }

    @Override
    public void onPause() {
        saveInputInPrefs();
        super.onPause();
    }

    private void saveInputInPrefs() {
        if (wishPrefs == null) {
            return;
        }

        WishPrefsEditor_ editor = wishPrefs.edit();

        if (editNick != null && editNick.getText() != null) {
            //noinspection ConstantConditions
            editor.nick().put(editNick.getText().toString());
        }

        if (editArtist != null && editArtist.getText() != null) {
            //noinspection ConstantConditions
            editor.artist().put(editArtist.getText().toString());
        }

        if (editTitle != null && editTitle.getText() != null) {
            //noinspection ConstantConditions
            editor.title().put(editTitle.getText().toString());
        }

        if (editRegard != null && editRegard.getText() != null) {
            //noinspection ConstantConditions
            editor.greeting().put(editRegard.getText().toString());
        }

        editor.apply();
    }

    /**
     * checks edit text objects for valid data
     *
     * @return true if input is valid - false otherwise
     */
    private boolean haveValidData() {
        if (BuildConfig.DEBUG) {
            HyperLog.d(TAG, "haveValidData()");
        }

        boolean haveNick = editNick != null && !TextUtils.isEmpty(editNick.getText());

        boolean artistEnabledAndHasText =
                editArtist != null &&
                        !TextUtils.isEmpty(editArtist.getText()) &&
                        editArtist.isEnabled();
        boolean titleEnabledAndHasText =
                editTitle != null
                        && !TextUtils.isEmpty(editTitle.getText())
                        && editTitle.isEnabled();
        boolean haveWish = artistEnabledAndHasText
                && titleEnabledAndHasText;
        boolean haveRegard = editRegard != null
                && !TextUtils.isEmpty(editRegard.getText())
                && editRegard.isEnabled();

        boolean isValidData = haveNick && (haveWish || haveRegard);

        if (BuildConfig.DEBUG) {
            HyperLog.d(TAG, String.format("haveValidData() -> %s", isValidData));
        }
        return isValidData;
    }

    @Click(R.id.btnSend)
    @OptionsItem(R.id.ic_menu_send)
    void sendButtonClicked() {
        if (BuildConfig.DEBUG) {
            HyperLog.d(TAG, "sendButtonClicked()");
        }

        if (didNotCompleteLoadingStats) {
            return;
        }
        if (haveValidData()) {
            showLoading(true);
            sendWishGreet();
        } else {
            notifyUser(R.string.invalid_input);
        }


        if (BuildConfig.DEBUG) {
            HyperLog.d(TAG, "sendButtonClicked() done");
        }
    }

    @Background
    void sendWishGreet() {
        HyperLog.d(TAG, "sendWishGreet()");
        try {
            if (editNick == null || editArtist == null || editTitle == null || editRegard == null) {
                return;
            }

            Editable editNickText = editNick.getText();
            Editable editArtistText = editArtist.getText();
            Editable editTitleText = editTitle.getText();
            Editable editRegardText = editRegard.getText();


            String nick = (editNickText != null) ? editNickText.toString() : "";
            String artist = (editArtistText != null) ? editArtistText.toString() : "";
            String title = (editTitleText != null) ? editTitleText.toString() : "";
            String greet = (editRegardText != null) ? editRegardText.toString() : "";

            boolean canWish = (stats != null) && stats.getCanWish();
            boolean hasWish = !TextUtils.isEmpty(artist) && !TextUtils.isEmpty(title);

            HyperLog.d(TAG, "sendWishGreet() - Prepared sending");
            if (canWish && hasWish) {
                new WishSender(this, nick, greet, artist, title).send();
                HyperLog.d(TAG, "sendWishGreet() - Sent wish with greeting");
            } else {
                new WishSender(this, nick, greet, null, null).send();
                HyperLog.d(TAG, "sendWishGreet() - Sent greeting, dropped wish");
            }
        } catch (NoInternetException e) {
            HyperLog.d(TAG, "sendWishGreet() - No internet notification");
            notifyUser(R.string.no_internet);
        } catch (Exception e) {
            HyperLog.d(TAG, "sendWishGreet() - Unknown exception: ", e);
            notifyUser(R.string.unexpectedError);
        }
    }

    @UiThread
    void notifyUser(String s) {
        Toast toast = Toast.makeText(getActivity(),
                s, Toast.LENGTH_SHORT);
        if (toast != null) {
            toast.show();
        }
    }

    @UiThread
    void notifyUser(int id) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            Toast toast = Toast.makeText(activity, id, Toast.LENGTH_SHORT);
            if (toast != null) {
                toast.show();
            }
        }
    }

    /**
     * shows info
     */
    @OptionsItem(R.id.mnu_help)
    void showInfo() {

        FragmentActivity activity = getActivity();
        if (activity != null) {
            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setTitle(R.string.notification);
            View v = activity.getLayoutInflater().inflate(R.layout.dialog_text, null);

            if (v != null) {
                TextView tv = (TextView) v.findViewById(R.id.text);
                if (tv != null) {
                    tv.setText(R.string.wish_explanation);
                }
                alert.setView(v);
                alert.setPositiveButton(R.string.ok, null);
                alert.show();
            }

        }
    }

    @Override
    public void onSuccess() {
        showLoading(false);
        notifyUser(R.string.sent);
        clearSongAndWish();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    @Override
    public void onFail() {
        showLoading(false);
        notifyUser(R.string.sending_error);
        enableSendButton();
    }

    @Override
    public void onException(@NonNull Exception e) {
        showLoading(false);
        notifyUser(e.getMessage());
        enableSendButton();
    }

    @UiThread
    void enableSendButton() {
        if (buttonSend != null) {
            buttonSend.setEnabled(true);
            buttonSend.setText(R.string.wish_send);
        }
    }

    @Click(R.id.btnClear)
    void btnClearClicked() {
        clearSongAndWish();
    }

    @UiThread
    void clearSongAndWish() {
        if (editArtist != null) {
            editArtist.setText("");
        }
        if (editTitle != null) {
            editTitle.setText("");
        }
        if (editRegard != null) {
            editRegard.setText("");
        }
        saveInputInPrefs();
    }
}
