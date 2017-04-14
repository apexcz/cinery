package com.example.chineduoty.cinery.utilities;

import android.net.Uri;
import android.util.Log;

import com.example.chineduoty.cinery.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by chineduoty on 4/13/17.
 */

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();
    private final static String API_KEY_VALUE = "YOUR API KEY";

    public static URL buildUrl(String mode){
        Uri builtUri = Uri.parse(Constants.BASE_URL).buildUpon()
                .appendPath(mode)
                .appendQueryParameter(Constants.API_KEY,API_KEY_VALUE)
                .build();

        URL url = null;
        try{
            url = new URL(builtUri.toString());
        }
        catch (MalformedURLException ex){
            ex.printStackTrace();
        }

        Log.v(TAG,"Built Uri "+url);

        return url;
    }

    public static String makeHttpCall(URL url) throws IOException{
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try{
            InputStream in = connection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput){
                return scanner.next();
            }
            else {
                return null;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
        finally {
            connection.disconnect();
        }
    }
}
