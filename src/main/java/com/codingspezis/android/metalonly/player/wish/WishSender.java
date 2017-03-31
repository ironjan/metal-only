package com.codingspezis.android.metalonly.player.wish;

import android.text.TextUtils;

import com.codingspezis.android.metalonly.player.BuildConfig;
import com.codingspezis.android.metalonly.player.utils.UrlConstants;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Restored class since AA REST api doesn't work for wishes.
 */
public class WishSender {

    private static final String KEY_NICK = "nick";
    private static final String KEY_ARTIST = "artist";
    private static final String KEY_SONG = "song";
    private static final String KEY_GREET = "greet";
    private final Logger SENDER_LOGGER = LoggerFactory.getLogger(WishSender.class.getSimpleName());

    private final String nick;
    private final String artist;
    private final String title;
    private final String greet;

    private final Callback callback;

    public WishSender(Callback callback, String nick, String greet, String artist, String title) {
        this.nick = nick;
        this.artist = artist;
        this.title = title;
        this.greet = greet;
        this.callback = callback;
    }
    public WishSender(Callback callback, String nick, String greet) {
        this.nick = nick;
        this.artist = null;
        this.title = null;
        this.greet = greet;
        this.callback = callback;
    }

    public void send() {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(UrlConstants.METAL_ONLY_WUNSCHSCRIPT_POST_URL);
        List<NameValuePair> pairs = new ArrayList<>(4);

        if (!TextUtils.isEmpty(nick)) {
            pairs.add(new BasicNameValuePair(KEY_NICK, nick));
        }
        if (!TextUtils.isEmpty(artist)) {
            pairs.add(new BasicNameValuePair(KEY_ARTIST, artist));
        }
        if (!TextUtils.isEmpty(title)) {
            pairs.add(new BasicNameValuePair(KEY_SONG, title));
        }
        if (!TextUtils.isEmpty(greet)) {
            pairs.add(new BasicNameValuePair(KEY_GREET, greet));
        }

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs,
                    "UTF-8");
            entity.setContentEncoding(HTTP.UTF_8);
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().toString().equals("HTTP/1.1 200 OK")) {
                // TODO handle error cases by parsing HTML
                callback.onSuccess();
            } else {
                callback.onFail();
            }
        } catch (IOException e) {
            callback.onException(e);
        }

        if (BuildConfig.DEBUG) SENDER_LOGGER.debug("run() done");
    }


    public interface Callback {
        void onSuccess();
        void onFail();
        void onException(Exception e);
    }
}