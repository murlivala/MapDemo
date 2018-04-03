package mock.mapdemo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import mock.mapdemo.BuildConfig;

public class InternetUtil {
    private static final String TAG = InternetUtil.class.getSimpleName();

    public static boolean isInternetOn(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public static String sendHttpRequest(String path, String jsonString) throws Exception {
        URL url = new URL(path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if(null==jsonString || "".equals(jsonString)){
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
        }else{
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            DataOutputStream output = new DataOutputStream(connection.getOutputStream());
            output.writeBytes(jsonString);
            output.flush();
            output.close();
        }
        return processInputStream(connection.getInputStream());
    }

    private static String processInputStream(InputStream inputStream) throws Exception {
        InputStreamReader input = new InputStreamReader(inputStream);

        BufferedReader in = new BufferedReader(input);
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String responseStr = response.toString();

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Result = " + responseStr);
        }

        return responseStr;
    }
}
