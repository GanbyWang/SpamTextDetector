package com.example.wangyicheng.spamtextdetector;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class to deal with HTTP POST request
 */

public class HttpPost {

    // Static ints for results
    static final int POST_SUCC = 2;
    static final int POST_FAIL = 3;

    // Types of different POST request
    static final int TYPE_CHECK = 0;

    // Time thresholds
    private final int READ_TIMEOUT = 3000;
    private final int CONNECT_TIMEOUT = 3000;

    /**
     * Constructor
     * @param srcData: the byte array of data to send to backend
     * @param urlString: the URL to connect
     * @param handler: the handler function when the response comes back
     * @param type: the POST type (currently we only have one type)
     * */
    // TODO: implement the handler function when calling this constructor
    // TODO: store the text in the byte array
    HttpPost(final byte[] srcData, final String urlString, final Handler handler, final int type) {

        // Create a new thread to execute the request
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    // Generate the request and set related arguments
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(CONNECT_TIMEOUT);
                    connection.setReadTimeout(READ_TIMEOUT);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setRequestProperty("Charset", "UTF-8");
                    connection.setRequestProperty("Content-Type", "application/json");

                    DataOutputStream outputStream =
                            new DataOutputStream(connection.getOutputStream());
                    outputStream.write(srcData);
                    outputStream.flush();
                    outputStream.close();

                    // HTTP success
                    if (connection.getResponseCode() >= HttpURLConnection.HTTP_OK &&
                            connection.getResponseCode() < 300) {

                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader =
                                new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        String resultData = "";
                        while (((line = bufferedReader.readLine()) != null)) {
                            resultData += line;
                        }
                        inputStream.close();
                        connection.disconnect();

                        // Generate the success message and send it to handler
                        Message mg = Message.obtain();
                        mg.what = POST_SUCC;
                        mg.obj = resultData;
                        handler.sendMessage(mg);
                    } else {
                        // HTTP failure

                        InputStream inputStream = connection.getErrorStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        String resultData = "";
                        while (((line = bufferedReader.readLine()) != null)) {
                            resultData += line;
                        }
                        inputStream.close();

                        // Generate the error message and send it to handler
                        Message mg = Message.obtain();
                        mg.what = POST_FAIL;
                        mg.obj = resultData;
                        mg.arg1 = connection.getResponseCode();
                        connection.disconnect();
                        handler.sendMessage(mg);
                    }

                } catch (Exception e) {
                    // Generate the error message and send it to handler
                    Message mg = Message.obtain();
                    mg.what = POST_FAIL;
                    mg.obj = "Please check Internet connection";
                    mg.arg1 = 0;
                    handler.sendMessage(mg);
                }
            }
        }).start();
    }
}

