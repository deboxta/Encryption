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
import java.util.List;

public class FetchPostAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String POST_HTTP = "https://m1t2.blemelin.tk/api/v1/key/";

    private boolean isServerError;
    private boolean isNotFoundError;
    private boolean isConnnectivityError;

    private final Listener onSuccess;
    private final Runnable onNotFoundError;
    private final Runnable onConnectivityError;
    private final Runnable onServerError;

    public FetchPostAsyncTask(Listener onSuccess, Runnable onNotFoundError, Runnable onConnectivityError, Runnable onServerError) {

        if (onSuccess == null) throw new IllegalArgumentException("onSuccess cannot be null");
        if (onNotFoundError == null) throw new IllegalArgumentException("onSuccess cannot be null");
        if (onConnectivityError == null) throw new IllegalArgumentException("onSuccess cannot be null");
        if (onServerError == null) throw new IllegalArgumentException("onSuccess cannot be null");

        isServerError = false;
        isNotFoundError = false;
        isConnnectivityError = false;

        this.onSuccess = onSuccess;
        this.onNotFoundError = onNotFoundError;
        this.onConnectivityError = onConnectivityError;
        this.onServerError = onServerError;
    }

    @Override
    protected String doInBackground(Void... voids) {              //TODO : CHANGE LE LIST<STRING> POUR LISTE DE POST
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(POST_HTTP).build();

        String post = null; //TODO: CHANGE LE NOM POUR QUIL SOIT PLUS PRECIS
        try(Response response = client.newCall(request).execute()) {
            if (response.code() == HttpURLConnection.HTTP_NOT_FOUND){
                isNotFoundError = true;
            }else if (!response.isSuccessful()){
                isServerError = true;
            }else{
                String responseBody = response.body().string();

                ObjectMapper mapper = new ObjectMapper();
                post = mapper.readValue(responseBody, new TypeReference<String>(){});
            }
        } catch (JsonParseException | JsonMappingException e){
            isServerError = true;
        } catch (IOException e){
            isConnnectivityError = true;
        }
//        response.close();
        return post;
    }

    public void onPostExecute(String post){
        if (isServerError)
            onServerError.run();
        else if (isConnnectivityError)
            onConnectivityError.run();
        else if (isNotFoundError)
            onNotFoundError.run();

        onSuccess.onPostFetched(post);
    }

    public interface Listener{
        void onPostFetched(String post);
    }

}
