package ca.csf.mobile1.tp2.activity.Server;

import android.os.AsyncTask;
import ca.csf.mobile1.tp2.activity.JSON.KeyFromServer;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.HttpURLConnection;

public class FetchKeyAsyncTask extends AsyncTask<String, Void, KeyFromServer> {

    private static final String POST_HTTP = "https://m1t2.blemelin.tk/api/v1/key/";

    private boolean isServerError;
    private boolean isNotFoundError;
    private boolean isConnectivityError;

    private OkHttpClient okHttpClient;
    private ObjectMapper objectMapper;

    private final Listener onSuccess;
    private final Runnable onNotFoundError;
    private final Runnable onConnectivityError;
    private final Runnable onServerError;

    public FetchKeyAsyncTask(Listener onSuccess, Runnable onNotFoundError, Runnable onConnectivityError, Runnable onServerError, OkHttpClient okHttpClient, ObjectMapper objectMapper) {

        if (onSuccess == null) throw new IllegalArgumentException("onSuccess cannot be null");
        if (onNotFoundError == null) throw new IllegalArgumentException("onNotFoundError cannot be null");
        if (onConnectivityError == null) throw new IllegalArgumentException("onConnectivityError cannot be null");
        if (onServerError == null) throw new IllegalArgumentException("onServerError cannot be null");

        isServerError = false;
        isNotFoundError = false;
        isConnectivityError = false;

        this.okHttpClient = okHttpClient;
        this.objectMapper =objectMapper;

        this.onSuccess = onSuccess;
        this.onNotFoundError = onNotFoundError;
        this.onConnectivityError = onConnectivityError;
        this.onServerError = onServerError;
    }

    /**
     * Utilise la clé fournie en paramêtre pour aller chercher ses informations sur le server suite à la connection
     * @param key La clé qui permet d'aller chercher les informations d'Encryptage
     * @return L'objet de la clé JSON
     */
    @Override
    protected KeyFromServer doInBackground(String... key) {

        Request request = new Request.Builder().url(POST_HTTP+key[0]).build();

        KeyFromServer postKeyInfo = null;
        try(Response response = okHttpClient.newCall(request).execute()) {
            if (response.code() == HttpURLConnection.HTTP_NOT_FOUND){
                isNotFoundError = true;
            }else if (!response.isSuccessful()){
                isServerError = true;
            }else{
                try{
                    if (response.body() != null){
                        String responseBody = response.body().string();
                        postKeyInfo = objectMapper.readValue(responseBody, KeyFromServer.class);
                    }
                }catch(NullPointerException e){
                    e.printStackTrace();
                }
            }
        } catch (JsonParseException | JsonMappingException e){
            e.printStackTrace();
            isServerError = true;
        } catch (IOException e){
            e.printStackTrace();
            isConnectivityError = true;
        }
        return postKeyInfo;
    }

    @Override
    protected void onPreExecute() { onSuccess.onPostFetching(); }

    @Override
    protected void onPostExecute(KeyFromServer postKeyInfo) {
        if (isServerError)
            onServerError.run();
        else if (isConnectivityError)
            onConnectivityError.run();
        else if (isNotFoundError)
            onNotFoundError.run();
        else
            onSuccess.onPostFetched(postKeyInfo);
    }

    public interface Listener{
        void onPostFetched(KeyFromServer postKeyInfo);
        void onPostFetching();
    }
}
