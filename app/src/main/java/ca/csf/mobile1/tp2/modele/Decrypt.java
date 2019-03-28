package ca.csf.mobile1.tp2.modele;

public enum  Decrypt
{
    INSTANCE;

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