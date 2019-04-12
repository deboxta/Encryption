package ca.csf.mobile1.tp2.activity.Crypt;

//BEN_CORRECTION : Découpage en packages bien, mais nommage incorrect (lower.dot.case, pas mixed.Dot.case).

import android.os.AsyncTask;
import ca.csf.mobile1.tp2.modele.Decrypt;

//BEN_REVIEW : Je trouve étrange que votre taĉhe Async produise un "StringBuilder" au lieu d'une String.

public class DecryptTask extends AsyncTask<String, Void, StringBuilder>
{
    //BEN_CORRECTION : Erreur de conception orientée objet. Vous auriez du avoir une référence vers un objet de type
    //                 "KeyFromServer" au lieu de le décomposer ainsi.
    private String inputCharacters;
    private String outputCharacters;

    private final Listener listener;

    //BEN_CORRECTION : Doc imcomplète pour cette fonction.

    /**
     *
     * @param listener ce qui veut être averti du résultat
     * @param inputCharacters Le string qui sert à déterminer quelle est la position d'un charactère
     * @param outputCharacters Le string qui sert à remplacer des charactère selon leur position
     */
    public DecryptTask(Listener listener, String inputCharacters, String outputCharacters)
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
        return Decrypt.INSTANCE.decrypt(userString[0], inputCharacters, outputCharacters);
    }

    @Override
    protected void onPreExecute() {
        listener.onDecrypting();
    }

    @Override
    protected void onPostExecute(StringBuilder decrypted) {
        listener.onDecrypted(decrypted);
    }

    /**
     * L'interface du listener
     */
    public interface Listener{
        void onDecrypted(StringBuilder decrypted);
        void onDecrypting();
    }
}
