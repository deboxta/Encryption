package ca.csf.mobile1.tp2.activity;

import android.os.AsyncTask;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.PrivilegedAction;
import java.util.List;

public class FetchPostAsyncTask extends AsyncTask<String, Void, List<Task>> {

    private static final String POST_HTTP = "http://192.168.1.15:8080/api/v1/key/"; //https://m1t2.blemelin.tk/api/v1/key/

    private boolean isServerError;
    private boolean isNotFoundError;
    private boolean isConnnectivityError;

    private OkHttpClient okHttpClient;
    private ObjectMapper objectMapper;

    private final Listener onSuccess;
    private final Runnable onNotFoundError;
    private final Runnable onConnectivityError;
    private final Runnable onServerError;

    public FetchPostAsyncTask(Listener onSuccess, Runnable onNotFoundError, Runnable onConnectivityError, Runnable onServerError, OkHttpClient okHttpClient, ObjectMapper objectMapper) {

        if (onSuccess == null) throw new IllegalArgumentException("onSuccess cannot be null");
        if (onNotFoundError == null) throw new IllegalArgumentException("onNotFoundError cannot be null");
        if (onConnectivityError == null) throw new IllegalArgumentException("onConnectivityError cannot be null");
        if (onServerError == null) throw new IllegalArgumentException("onServerError cannot be null");

        isServerError = false;
        isNotFoundError = false;
        isConnnectivityError = false;

        this.okHttpClient = okHttpClient;
        this.objectMapper =objectMapper;

        this.onSuccess = onSuccess;
        this.onNotFoundError = onNotFoundError;
        this.onConnectivityError = onConnectivityError;
        this.onServerError = onServerError;
    }

    @Override
    protected List<Task> doInBackground(String... key) {              //TODO : CHANGE LE LIST<STRING> POUR LISTE DE POST
        if(android.os.Debug.isDebuggerConnected())
            android.os.Debug.waitForDebugger();

        Request request = new Request.Builder().url(POST_HTTP+key[0]).build();

        List<Task> post = null; //TODO: CHANGE LE NOM POUR QUIL SOIT PLUS PRECIS
        try(Response response = okHttpClient.newCall(request).execute()) {
            if (response.code() == HttpURLConnection.HTTP_NOT_FOUND){
                isNotFoundError = true;
            }else if (!response.isSuccessful()){
                isServerError = true;
            }else{
                String responseBody = response.body().string();

                post = objectMapper.readValue(responseBody, new TypeReference<Task>(){});
            }
        } catch (JsonParseException | JsonMappingException e){
            e.printStackTrace();
            isServerError = true;
        } catch (IOException e){
            e.printStackTrace();
            isConnnectivityError = true;
        }
        return post;
    }

    @Override
    protected void onPostExecute(List<Task> post) {
        if (isServerError)
            onServerError.run();
        else if (isConnnectivityError)
            onConnectivityError.run();
        else if (isNotFoundError)
            onNotFoundError.run();

        onSuccess.onPostFetched(post);
    }

    public interface Listener{
        void onPostFetched(List<Task> post);
    }
}
