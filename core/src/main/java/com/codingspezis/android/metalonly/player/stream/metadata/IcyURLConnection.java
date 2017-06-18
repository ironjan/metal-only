/*
** CREDIT TO
** http://aacdecoder-android.googlecode.com/
**
** #######################################################################
**
** AACDecoder - Freeware Advanced Audio (AAC) Decoder for Android
** Copyright (C) 2014 Spolecne s.r.o., http://www.spoledge.com
**  
** This file is a part of AACDecoder.
**
** AACDecoder is free software; you can redistribute it and/or modify
** it under the terms of the GNU Lesser General Public License as published
** by the Free Software Foundation; either version 3 of the License,
** or (at your option) any later version.
** 
** This program is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
** GNU Lesser General Public License for more details.
** 
** You should have received a copy of the GNU Lesser General Public License
** along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package com.codingspezis.android.metalonly.player.stream.metadata;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This is a URLConnection allowing to accept http-like ICY (shoutcast) responses.
 * Starting from Android 4.4 Kitkat the standard HttpURLConnection is not able
 * to process ICY protocol, so this class is a workaround for fixing this situation.
 * <p/>
 * The instance of this class must be created either directly, or by using
 * the IcyURLStreamHandler and setting a new global URLStreamHandlerFactory into the JVM:
 * <pre>
 *  try {
 *      java.net.URL.setURLStreamHandlerFactory( new java.net.URLStreamHandlerFactory(){
 *          public java.net.URLStreamHandler createURLStreamHandler( String protocol ) {
 *              if ("icy".equals( protocol )) return new com.spoledge.aacdecoder.IcyURLStreamHandler();
 *              return null;
 *          }
 *      });
 *  }
 *  catch (Throwable t) {
 *      Log.w( LOG, "Cannot set the ICY URLStreamHandler - maybe already set ? - " + t );
 *  }
 * </pre>
 * <p/>
 * When the URLStreamHandlerFactory is installed, you can create the URLConnection indirectly
 * using the URL class:
 * <p/>
 * <pre>
 *  java.net.URL url = new java.net.URL( "icy://159.253.145.178:8100" );
 *  java.net.URLConnection = url.openConnection(); // should be instance of IcyURLConnection
 * </pre>
 */
class IcyURLConnection extends HttpURLConnection {

    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private HashMap<String, List<String>> requestProps;
    private final HashMap<String, List<String>> headers = new HashMap<>();
    private String responseLine;

    /**
     * Creates new instance for the given URL.
     */
    public IcyURLConnection(URL url) {
        super(url);
    }

    /**
     * Opens a communications link to the resource referenced by this URL,
     * if such a connection has not already been established.
     */
    @Override
    public synchronized void connect() throws IOException {
        // according to specification:
        if (connected) return;

        socket = createSocket();

        int port = url.getPort() != -1 ? url.getPort() : url.getDefaultPort();
        socket.connect(
                new InetSocketAddress(url.getHost(), port),
                getConnectTimeout());

        Map<String, List<String>> requestProps = getRequestProperties();

        connected = true;

        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();

        writeLine("GET " + ("".equals(url.getPath()) ? "/" : url.getPath()) + " HTTP/1.1");
        writeLine("Host: " + url.getHost());

        if (requestProps != null) {
            for (Map.Entry<String, List<String>> entry : requestProps.entrySet()) {
                for (String val : entry.getValue()) {
                    writeLine(entry.getKey() + ": " + val);
                }
            }
        }

        writeLine("");

        responseLine = readResponseLine();

        String line;
        do {
            line = readLine();
            parseHeaderLine(line);
        } while ((line != null) && (!line.isEmpty()));
    }


    @Override
    public InputStream getInputStream() {
        return inputStream;
    }


    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }


    @Override
    public String getHeaderField(String name) {
        List<String> list = headers.get(name);

        if ((list == null) || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }


    @Override
    public String getHeaderField(int n) {
        return (n == 0) ? responseLine : null;
    }


    @Override
    public Map<String, List<String>> getHeaderFields() {
        return headers;
    }


    @Override
    public synchronized void setRequestProperty(String key, String value) {
        if (requestProps == null) {
            requestProps = new HashMap<>();
        }

        List<String> list = new ArrayList<>();
        list.add(value);
        requestProps.put(key, list);
    }

    @Override
    public synchronized void addRequestProperty(String key, String value) {
        if (requestProps == null) {
            requestProps = new HashMap<>();
        }

        List<String> list = requestProps.get(key);
        if (list == null) {
            list = new ArrayList<>();
        }

        list.add(value);
        requestProps.put(key, list);
    }


    @Override
    public Map<String, List<String>> getRequestProperties() {
        return requestProps;
    }

    @Override
    public synchronized void disconnect() {
        if (!connected) return;

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
            }
            socket = null;
        }

        inputStream = null;
        outputStream = null;
        headers.clear();
        responseLine = null;
    }


    @Override
    public boolean usingProxy() {
        return false;
    }

    /**
     * Creates a new unconnected Socket instance.
     * Subclasses may use this method to override the default socket implementation.
     */
    private Socket createSocket() {
        return new Socket();
    }

    /**
     * Reads one response header line and adds it to the headers map.
     */
    private void parseHeaderLine(String line) throws IOException {
        int len = 2;
        int n = line.indexOf(": ");

        if (n == -1) {
            len = 1;
            n = line.indexOf(':');
            if (n == -1) return;
        }

        String key = line.substring(0, n);
        String val = line.substring(n + len);

        addHeaderValue(key, val);
    }

    private void addHeaderValue(String key, String val) {
        List<String> list = headers.get(key);

        if (list == null) {
            list = new ArrayList<>(1);
        }

        list.add(val);

        headers.put(key, list);
    }


    /**
     * Reads the first response line.
     */
    private String readResponseLine() throws IOException {
        String line = readLine();

        if (line != null) {
            int n = line.indexOf(' ');

            if (n != -1) {
                line = "HTTP/1.0" + line.substring(n);
            }
        }

        return line;
    }


    /**
     * Reads one response line.
     *
     * @return the line without any new-line character.
     */
    private String readLine() throws IOException {
        StringBuilder sb = new StringBuilder();

        int c;
        while ((c = inputStream.read()) != -1) {
            if (c == '\r') continue;
            if (c == '\n') break;
            sb.append((char) c);
        }

        return sb.toString();
    }


    /**
     * Writes one request line.
     */
    private void writeLine(String line) throws IOException {
        line += '\r';
        line += '\n';
        outputStream.write(line.getBytes("UTF-8"));
    }

}