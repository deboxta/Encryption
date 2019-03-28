package ca.csf.mobile1.tp2.activity;

import android.os.AsyncTask;
import ca.csf.mobile1.tp2.modele.Encrypt;

public class EncryptTask extends AsyncTask<String, Void, StringBuilder>
{
    public static final StringBuilder NULL_RETURN = new StringBuilder().append("");

    private String inputCharacters;
    private String outputCharacters;

    private final Listener listener;

    /**
     *
     * @param listener ce qui veut être averti du résultat
     * @param inputCharacters Le string qui sert à déterminer quelle est la position d'un charactère
     * @param outputCharacters Le string qui sert à remplacer des charactère selon leur position
     */
    public EncryptTask(Listener listener, String inputCharacters, String outputCharacters)
    {
        if (listener == null) throw new IllegalArgumentException("listener cannot be null");
        if (inputCharacters == null) throw new IllegalArgumentException("inputCharacters cannot be null");
        if (outputCharacters == null) throw new IllegalArgumentException("outputCharacters cannot be null");

        this.listener = listener;

        this.inputCharacters = inputCharacters;
        this.outputCharacters = outputCharacters;
    }

    @Override
    protected StringBuilder doInBackground(String... userString)
    {
        if (inputCharacters != null && outputCharacters != null){
            return Encrypt.INSTANCE.encrypt(userString[0], inputCharacters, outputCharacters);
        }
        return NULL_RETURN;
    }

    @Override
    protected void onPreExecute() {
        listener.onEncrypting();
    }

    @Override
    protected void onPostExecute(StringBuilder encrypted) {
        listener.onEncrypted(encrypted);
    }

    /**
     * L'interface du listener
     */
    public interface Listener{
        void onEncrypted(StringBuilder encrypted);
        void onEncrypting();
    }
}