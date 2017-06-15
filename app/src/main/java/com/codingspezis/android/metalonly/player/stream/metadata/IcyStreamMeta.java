package com.codingspezis.android.metalonly.player.stream.metadata;

import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by r on 17.09.14.
 * <p/>
 * CREDIT TO:
 * http://stackoverflow.com/questions/8970548/how-to-get-metadata-of-a-streaming-online-radio
 */
public class IcyStreamMeta {

    protected URL streamUrl;
    private Map<String, String> metadata;
    private boolean isError;
    private Map<String, String> data;

    /**
     * Arbitrary, high number to limit the StringBuffer's size that is used to extract metadata.
     * Hopefully this value is also small enough to prevent the OOM errors from <a href="https://github.com/ironjan/metal-only/issues/68>#68</a>.
     */
    private static final int MAX_STRING_BUFFER_SIZE = 4096;
    private static final int INITIAL_STRING_BUFFER_SIZE = 128;

    public IcyStreamMeta() {
        isError = false;
    }

    public static Map<String, String> parseMetadata(String metaString) {
        Map<String, String> metadata = new HashMap<>();
        String[] metaParts = metaString.split(";");
//        ÔStreamTitle='Maat - The Divine Slaughtering Of Mankin';
        Pattern p = Pattern.compile("(\\w+)='(.*)'$");
        Matcher m;
        for (String metaPart : metaParts) {
            m = p.matcher(metaPart);
            if (m.find()) {
                metadata.put((String) m.group(1), (String) m.group(2));
            }
        }

        return metadata;
    }

    /**
     * Get artist using stream's title
     *
     * @return String
     * @throws IOException
     */
    public String getArtist() throws IOException {
        data = getMetadata();

        if (!data.containsKey("StreamTitle"))
            return "";

        String streamTitle = data.get("StreamTitle");
        String title = streamTitle.substring(0, streamTitle.indexOf("-"));
        return title.trim();
    }

    /**
     * Get streamTitle
     *
     * @return String
     * @throws IOException
     */
    public String getStreamTitle() throws IOException {
        data = getMetadata();

        if (!data.containsKey("StreamTitle"))
            return "";

        return data.get("StreamTitle");
    }

    /**
     * Get title using stream's title
     *
     * @return String
     * @throws IOException
     */
    public String getTitle() throws IOException {
        data = getMetadata();

        if (!data.containsKey("StreamTitle"))
            return "";

        String streamTitle = data.get("StreamTitle");
        String artist = streamTitle.substring(streamTitle.indexOf("-") + 1);
        return artist.trim();
    }

    public Map<String, String> getMetadata() throws IOException {
        if (metadata == null) {
            refreshMeta();
        }

        return metadata;
    }

    synchronized public void refreshMeta() throws IOException {
        retreiveMetadata();
    }

    synchronized private void retreiveMetadata() throws IOException {
        URLConnection con;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            con = new IcyURLConnection(streamUrl);
        else
            con = streamUrl.openConnection();
        con.setRequestProperty("Icy-MetaData", "1");
        con.setRequestProperty("Connection", "close");
        con.setRequestProperty("Accept", null);
        con.connect();
        int metaDataOffset = 0;
        Map<String, List<String>> headers = con.getHeaderFields();
        InputStream stream = con.getInputStream();

        if (headers.containsKey("icy-metaint")) {
            // Headers are sent via HTTP
            metaDataOffset = Integer.parseInt(headers.get("icy-metaint").get(0));
        } else {
            // Headers are sent within a stream
            StringBuilder strHeaders = new StringBuilder(INITIAL_STRING_BUFFER_SIZE);
            int charInt;
            while ((charInt = stream.read()) != -1) {
                if(strHeaders.length() >= MAX_STRING_BUFFER_SIZE){
                    strHeaders.delete(0, MAX_STRING_BUFFER_SIZE/2);
                }

                //noinspection NumericCastThatLosesPrecision loop condition makes sure that charInt is in [0, 255]
                strHeaders.append(((char) charInt));
                if (strHeaders.length() > 5 && (strHeaders.substring((strHeaders.length() - 4), strHeaders.length()).equals("\r\n\r\n"))) {
                    // getEndDate of headers
                    break;
                }
            }

            // Match headers to get metadata offset within a stream
            Pattern p = Pattern.compile("\\r\\n(icy-metaint):\\s*(.*)\\r\\n");
            Matcher m = p.matcher(strHeaders.toString());
            if (m.find()) {
                metaDataOffset = Integer.parseInt(m.group(2));
            }
        }

        // In case no data was sent
        if (metaDataOffset == 0) {
            isError = true;
            return;
        }

        // Read metadata
        int b;
        int count = 0;
        int metaDataLength = 4080; // 4080 is the max length
        boolean inData;
        StringBuilder metaData = new StringBuilder();
        // Stream position should be either at the beginning or right after headers
        while ((b = stream.read()) != -1) {
            count++;

            // Length of the metadata
            if (count == metaDataOffset + 1) {
                metaDataLength = b * 16;
            }

            inData = metaDataOffset < count  + 1 && count < (metaDataOffset + metaDataLength);
            if (inData) {
                if (b != 0) {
                    metaData.append((char) b);
                }
            }
            if (count > (metaDataOffset + metaDataLength)) {
                break;
            }
        }

        // Set the data
        metadata = IcyStreamMeta.parseMetadata(metaData.toString());

        // Close
        stream.close();

    }

    public boolean isError() {
        return isError;
    }

    public URL getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(URL streamUrl) {
        this.metadata = null;
        this.streamUrl = streamUrl;
        this.isError = false;
    }
}