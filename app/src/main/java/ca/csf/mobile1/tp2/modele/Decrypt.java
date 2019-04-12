package ca.csf.mobile1.tp2.modele;

//BEN_CORRECTION : Doc manquante pour l'enum.
//BEN_REVIEW : Je comprends pas vraiment pourquoi vous n'avez pas fait qu'une seule classe qui est capable de faire les deux.
//BEN_CORRECTION : Duplication de code.
public enum  Decrypt
{
    INSTANCE;

    //BEN_CORRECTION : Doc imcomplète pour cette fonction.

    /**
     *
     * @param userString Le string que l'utilisateur a entré
     * @param inputCharacters Le string qui sert à déterminer quelle est la position d'un charactère
     * @param outputCharacters Le string qui sert à remplacer des charactère selon leur position
     * @return Retourne un string décrypté
     */
    public StringBuilder decrypt(String userString, String inputCharacters, String outputCharacters)
    {
        if (validate(userString))
        {
            throw new IllegalArgumentException("Wrong user input");
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < userString.length(); i++)
        {
            //BEN_CORRECTION : Warning ignoré.
            Character currentCharacter = userString.charAt(i);

            stringBuilder.append(inputCharacters.substring(outputCharacters.indexOf(currentCharacter), outputCharacters.indexOf(currentCharacter) + 1));
        }

        return stringBuilder;
    }

    private boolean validate(String userString)
    {
        if (userString == null || userString.equals(""))
        {
            return true;
        }

        for (int i = 0; i < userString.length(); i++)
        {
            if(!Character.isLetter(userString.charAt(i)) && '.' != userString.charAt(i) && ' ' != userString.charAt(i))
            {
                return true;
            }
        }
        return false;
    }
}