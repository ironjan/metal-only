package com.codingspezis.android.metalonly.player.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.codingspezis.android.metalonly.player.BuildConfig
import com.codingspezis.android.metalonly.player.R
import com.codingspezis.android.metalonly.player.WishActivity
import com.codingspezis.android.metalonly.player.siteparser.HTTPGrabber
import com.codingspezis.android.metalonly.player.wish.WishPrefs_
import com.github.ironjan.metalonly.client_library.MetalOnlyClientV2
import com.github.ironjan.metalonly.client_library.NoInternetException
import com.github.ironjan.metalonly.client_library.WishSender
import com.github.ironjan.metalonly.client_library.model.StatsV2
import com.hypertrack.hyperlog.HyperLog
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.Background
import org.androidannotations.annotations.Click
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.FragmentArg
import org.androidannotations.annotations.OptionsItem
import org.androidannotations.annotations.OptionsMenu
import org.androidannotations.annotations.UiThread
import org.androidannotations.annotations.ViewById
import org.androidannotations.annotations.res.StringRes
import org.androidannotations.annotations.sharedpreferences.Pref
import org.springframework.web.client.RestClientException
import java.util.Locale

@EFragment(R.layout.fragment_wish)
@OptionsMenu(R.menu.wishmenu)
open class WishFragment : Fragment(), WishSender.Callback {

    @JvmField
    @ViewById(R.id.btnSend)
    internal var buttonSend: Button? = null
    @JvmField
    @ViewById
    internal var editNick: EditText? = null
    @JvmField
    @ViewById
    internal var editArtist: EditText? = null
    @JvmField
    @ViewById
    internal var editTitle: EditText? = null
    @JvmField
    @ViewById
    internal var editRegard: EditText? = null
    @JvmField
    @ViewById
    internal var btnSend: Button? = null

    @JvmField
    @FragmentArg(WishActivity.KEY_DEFAULT_INTERPRET)
    internal var interpret: String? = null
    @JvmField
    @FragmentArg(WishActivity.KEY_DEFAULT_TITLE)
    internal var title: String? = null

    @JvmField
    @ViewById(android.R.id.progress)
    internal var progress: ProgressBar? = null

    @JvmField
    @StringRes
    internal var number_of_wishes_format: String? = null
    @JvmField
    @StringRes
    internal var no_regards: String? = null
    @JvmField
    @StringRes
    internal var wish_list_full: String? = null

    private var stats: StatsV2? = null

    @JvmField
    @ViewById
    internal var textArtist: TextView? = null
    @JvmField
    @ViewById
    internal var textTitle: TextView? = null
    @JvmField
    @ViewById
    internal var textRegard: TextView? = null

    @JvmField
    @ViewById(R.id.txtWishcount)
    internal var wishCount: TextView? = null

    @JvmField
    @Pref
    internal var wishPrefs: WishPrefs_? = null
    private var didNotCompleteLoadingStats = true

    /**
     * Loads the actions that are allowed in this show. Needs to be called [AfterViews]
     * because we're updating the UI when getting a result.
     */
    @AfterViews
    @Background
    internal open fun loadAllowedActions() {

        val activity = activity ?: return

        if (HTTPGrabber.isOnline(activity)) {
            showLoading(true)
            try {
                val context = context ?: return

                val newStats = MetalOnlyClientV2.getClient(context).getStats()
                if (newStats.isRight()) {
                    updateStats(newStats.get())
                }
            } catch (e: NoInternetException) {
                loadingAllowedActionsFailed()
            } catch (e: RestClientException) {
                loadingAllowedActionsFailed()
            }
        } else {
            notifyUser(R.string.no_internet)
        }
    }

    @UiThread
    internal open fun loadingAllowedActionsFailed() {
        val activity = activity
        if (activity != null) {
            val toast = Toast.makeText(activity, R.string.stats_failed_to_load, Toast.LENGTH_LONG)
            toast?.show()
        }
    }

    @UiThread
    internal open fun updateStats(stats: StatsV2?) {
        this.stats = stats
        if (stats == null) {
            return
        }
        val sb = StringBuilder(0)

        if (number_of_wishes_format != null) {
            sb.append(String.format(Locale.GERMAN, number_of_wishes_format!!, stats.maxNoOfWishes))
        }

        sb.append(' ') // Append space to keep distance with next text

        if (stats.canWish) {
            editArtist?.setText("")
            editArtist?.isEnabled = true
            editTitle?.setText("")
            editTitle?.isEnabled = true
        } else {
            editArtist?.setText(R.string.no_wishes_short)
            editArtist?.isEnabled = false
            editTitle?.setText(R.string.no_wishes_short)
            editTitle?.isEnabled = false
        }

        if (stats.canGreet) {
            editRegard?.setText("")
            editRegard?.isEnabled = true
        } else {
            editRegard?.setText(R.string.no_regards)
            editRegard?.isEnabled = false

            sb.append('\n')
            sb.append(no_regards)
        }

        if (stats.maxNoOfWishesReached) {
            sb.append(wish_list_full)
            editArtist?.setText(R.string.wish_list_full_short)
            editArtist?.isEnabled = false
            editArtist?.isEnabled = false
            editTitle?.setText(R.string.wish_list_full_short)
            editTitle?.isEnabled = false
            editTitle?.isEnabled = false
        }

        if (wishCount != null) {
            wishCount?.text = sb.toString()
        }

        showLoading(false)
        didNotCompleteLoadingStats = false
    }

    @UiThread
    internal open fun showLoading(isLoading: Boolean) {
        if (btnSend == null || progress == null) {
            return
        }

        if (isLoading) {
            btnSend?.isEnabled = false
            progress?.visibility = View.VISIBLE
        } else {
            btnSend?.isEnabled = true
            progress?.visibility = View.INVISIBLE
        }
    }

    @AfterViews
    internal fun loadUiValues() {
        val wasGivenFragmentArgs = interpret != null && title != null
        if (wasGivenFragmentArgs) {
            if (editArtist != null) {
                editArtist?.setText(interpret)
            }
            if (editTitle != null) {
                editTitle?.setText(title)
            }
        } else {
            if (wishPrefs == null) {
                return
            }

            editTitle?.setText(wishPrefs?.title()?.get() ?: "")
            editRegard?.setText(wishPrefs?.greeting()?.get() ?: "")
            editArtist?.setText(wishPrefs?.artist()?.get() ?: "")
        }

        editNick?.setText(wishPrefs?.nick()?.get() ?: "")
    }

    override fun onPause() {
        saveInputInPrefs()
        super.onPause()
    }

    private fun saveInputInPrefs() {
        if (wishPrefs == null) {
            return
        }

        val editor = wishPrefs?.edit()

        editor?.nick()?.put(editNick?.text.toString())
            editor?.artist()?.put(editArtist?.text.toString())
            editor?.title()?.put(editTitle?.text.toString())
            editor?.greeting()?.put(editRegard?.text.toString())
        editor?.apply()
    }

    /**
     * checks edit text objects for valid data
     *
     * @return true if input is valid - false otherwise
     */
    private fun haveValidData(): Boolean {
        if (BuildConfig.DEBUG) {
            HyperLog.d(TAG, "haveValidData()")
        }

        val haveNick = editNick != null && !TextUtils.isEmpty(editNick?.text)

        val artistEnabledAndHasText = editArtist?.isEnabled ?: false && !TextUtils.isEmpty(editArtist?.text)
        val titleEnabledAndHasText = editTitle?.isEnabled ?: false && !TextUtils.isEmpty(editTitle?.text)
        val hasWish = artistEnabledAndHasText && titleEnabledAndHasText
        val hasRegard = editRegard?.isEnabled ?: false && !TextUtils.isEmpty(editRegard?.text)

        val isValidData = haveNick && (hasWish || hasRegard)

        if (BuildConfig.DEBUG) {
            HyperLog.d(TAG, String.format("haveValidData() -> %s", isValidData))
        }
        return isValidData
    }

    @Click(R.id.btnSend)
    @OptionsItem(R.id.ic_menu_send)
    internal fun sendButtonClicked() {
        if (BuildConfig.DEBUG) {
            HyperLog.d(TAG, "sendButtonClicked()")
        }

        if (didNotCompleteLoadingStats) {
            return
        }
        if (haveValidData()) {
            showLoading(true)
            sendWishGreet()
        } else {
            notifyUser(R.string.invalid_input)
        }

        if (BuildConfig.DEBUG) {
            HyperLog.d(TAG, "sendButtonClicked() done")
        }
    }

    @Background
    internal open fun sendWishGreet() {
        HyperLog.d(TAG, "sendWishGreet()")
        try {
            if (editNick == null || editArtist == null || editTitle == null || editRegard == null) {
                return
            }

            val nick = editNick?.text.toString()
            val artist = editArtist?.text.toString() ?: ""
            val title = editTitle?.text.toString()
            val greet = editRegard?.text.toString()

            val canWish = stats?.canWish ?: false
            val hasWish = !TextUtils.isEmpty(artist) && !TextUtils.isEmpty(title)

            HyperLog.d(TAG, "sendWishGreet() - Prepared sending")
            if (canWish && hasWish) {
                WishSender(this, nick, greet, artist, title).send()
                HyperLog.d(TAG, "sendWishGreet() - Sent wish with greeting")
            } else {
                WishSender(this, nick, greet, null, null).send()
                HyperLog.d(TAG, "sendWishGreet() - Sent greeting, dropped wish")
            }
        } catch (e: NoInternetException) {
            HyperLog.d(TAG, "sendWishGreet() - No internet notification")
            notifyUser(R.string.no_internet)
        } catch (e: Exception) {
            HyperLog.d(TAG, "sendWishGreet() - Unknown exception: ", e)
            notifyUser(R.string.unexpectedError)
        }
    }

    @UiThread
    internal open fun notifyUser(s: String) {
        val toast = Toast.makeText(activity,
                s, Toast.LENGTH_SHORT)
        toast?.show()
    }

    @UiThread
    internal open fun notifyUser(id: Int) {
        val activity = activity
        if (activity != null) {
            val toast = Toast.makeText(activity, id, Toast.LENGTH_SHORT)
            toast?.show()
        }
    }

    /**
     * shows info
     */
    @OptionsItem(R.id.mnu_help)
    internal fun showInfo() {

        val activity = activity
        if (activity != null) {
            val alert = AlertDialog.Builder(activity)
            alert.setTitle(R.string.notification)
            val v = activity.layoutInflater.inflate(R.layout.dialog_text, null)

            if (v != null) {
                val tv = v.findViewById<View>(R.id.text) as TextView
                tv?.setText(R.string.wish_explanation)
                alert.setView(v)
                alert.setPositiveButton(R.string.ok, null)
                alert.show()
            }
        }
    }

    override fun onSuccess() {
        showLoading(false)
        notifyUser(R.string.sent)
        clearSongAndWish()
        val activity = activity
        activity?.finish()
    }

    override fun onFail() {
        showLoading(false)
        notifyUser(R.string.sending_error)
        enableSendButton()
    }

    override fun onException(e: Exception) {
        showLoading(false)
        notifyUser(e.message ?: "Unknown error.")
        enableSendButton()
    }

    @UiThread
    internal open fun enableSendButton() {
        buttonSend?.isEnabled = true
        buttonSend?.setText(R.string.wish_send)
    }

    @Click(R.id.btnClear)
    internal fun btnClearClicked() {
        clearSongAndWish()
    }

    @UiThread
    internal open fun clearSongAndWish() {
        editArtist?.setText("")
        editTitle?.setText("")
        editRegard?.setText("")
        saveInputInPrefs()
    }

    companion object {
        private val TAG = WishFragment::class.java.simpleName

        fun newInstance(bundle: Bundle): WishFragment {
            val wishFragment = WishFragment_()
            wishFragment.arguments = bundle

            return wishFragment
        }
    }
}
