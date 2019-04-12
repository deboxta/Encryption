package ca.csf.mobile1.tp2.activity.JSON;

//BEN_CORRECTION : Classe de la couche modèle non documentée.
//BEN_CORRECTION : Nommage. Le "FromServer" ne sert à rien.

public class KeyFromServer {
    private int id;
    private String inputCharacters;
    private String  outputCharacters;

    public String getInputCharacters() {
        return inputCharacters;
    }

    public void setInputCharacters(String inputCharacters) {
        this.inputCharacters = inputCharacters;
    }

    public String getOutputCharacters() {
        return outputCharacters;
    }

    public void setOutputCharacters(String outputCharacters) {
        this.outputCharacters = outputCharacters;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}