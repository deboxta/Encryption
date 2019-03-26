package ca.csf.mobile1.tp2.activity;

import android.os.AsyncTask;

public class Decrypt extends AsyncTask<String, Void, StringBuilder>
{
    private String userString;
    private String inputCharacters;
    private String outputCharacters;

    @Override
    protected StringBuilder doInBackground(String... strings)
    {
        return decrypt(userString, inputCharacters, outputCharacters);
    }

    /**
     *
     * @param userString Le string que l'utilisateur a entré
     * @param inputCharacters Le string qui sert à déterminer quelle est la position d'un charactère
     * @param outputCharacters Le string qui sert à remplacer des charactère selon leur position
     * @return Retourne un string décrypté
     */
    public Decrypt(String userString, String inputCharacters, String outputCharacters) {
        this.userString = userString;
        this.inputCharacters = inputCharacters;
        this.outputCharacters = outputCharacters;
    }

    private StringBuilder decrypt(String userString , String inputCharacters, String outputCharacters)
    {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < userString.length(); i++)
        {
            Character currentCharacter = userString.charAt(i);

            stringBuilder.append(inputCharacters.substring(outputCharacters.indexOf(currentCharacter), outputCharacters.indexOf(currentCharacter) + 1));
        }

        return stringBuilder;
    }
}
