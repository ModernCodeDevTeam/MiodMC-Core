package pl.dcrft.Utils;

import com.google.gson.JsonObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class URLUtil {
    public static JsonObject queryJson(String urlString) throws Exception{

        HttpURLConnection conn;
        StringBuilder jsonResults = new StringBuilder();
        URL url = new URL(urlString);
        conn = (HttpURLConnection) url.openConnection();
        InputStreamReader in = new InputStreamReader(conn.getInputStream());
        // Load the results into a StringBuilder
        int read;
        char[] buff = new char[1024];
        while ((read = in.read(buff)) != -1) {
            jsonResults.append(buff, 0, read);
        }
        if (conn != null) {
            conn.disconnect();
        }

        return new JsonObject().getAsJsonObject(jsonResults.toString());
    }

    public static boolean isValidURL(String url) throws MalformedURLException, URISyntaxException {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException e) {
            return false;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
