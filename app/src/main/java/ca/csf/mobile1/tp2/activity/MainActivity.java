package ca.csf.mobile1.tp2.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.*;
import ca.csf.mobile1.tp2.R;
import ca.csf.mobile1.util.view.CharactersFilter;
import ca.csf.mobile1.util.view.KeyPickerDialog;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static org.koin.java.standalone.KoinJavaComponent.get;

public class MainActivity extends AppCompatActivity implements FetchKeyAsyncTask.Listener{

    private static final int KEY_LENGTH = 5;
    private static final int MAX_KEY_VALUE = (int) Math.pow(10, KEY_LENGTH) - 1;
    public static final String INIT_KEY_VALUE = "%05d";

    private OkHttpClient okHttpClient;
    private ObjectMapper objectMapper;
    private Intent intent;

    private Integer key;
    private Random rand;
    private String inputCharacters;
    private String outputCharacters;
    private boolean keySelected;

    private View rootView;
    private EditText inputEditText;
    private TextView outputTextView;
    private Button encryptButton;
    private Button decryptButton;
    private ImageButton copyButton;
    private FloatingActionButton selectKeyButton;
    private TextView currentKeyTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createView();
        createDependencies();
        setVariables();

        rand = new Random();
        key = rand.nextInt(MAX_KEY_VALUE);
        keySelected = false;
        inputCharacters = null;
        outputCharacters = null;

        keyFetchingActivation(key);

        intent = getIntent();
        if("text/plain".equals(intent.getType())) {
            inputEditText.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
            openKeyPickerDialog();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("CURRENT_INPUT", inputEditText.getText().toString());
        outState.putString("CURRENT_OUTPUT",outputTextView.getText().toString());
        outState.putString("CURRENT_KEY_TEXT", currentKeyTextView.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        inputEditText.setText(savedInstanceState.getString("CURRENT_INPUT"));
        outputTextView.setText(savedInstanceState.getString("CURRENT_OUTPUT"));
        currentKeyTextView.setText(savedInstanceState.getString("CURRENT_KEY_TEXT"));

        openKeyPickerDialog();
    }

    /**
     * Permet de set toutes les variables qui en ont besoin
     */
    private void setVariables(){
        selectKeyButton.setOnClickListener(this::onSelectKeyButtonPressed);
        copyButton.setOnClickListener(this::onCopyButtonPressed);
        encryptButton.setOnClickListener(this::onEncryptButtonPressed);
        decryptButton.setOnClickListener(this::onDecryptButtonPressed);
        encryptButton.setEnabled(false);
        decryptButton.setEnabled(false);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Création des variables de connection server qui sont passées à la tâche async
     */
    private void createDependencies() {
        okHttpClient = get(OkHttpClient.class);
        objectMapper = get(ObjectMapper.class);
    }

    /**
     * Crée la view de notre application en associant les variables
     */
    private void createView() {
        setContentView(R.layout.activity_main);

        rootView = findViewById(R.id.rootView);
        progressBar = findViewById(R.id.progressBar);
        inputEditText = findViewById(R.id.inputEditText);
        inputEditText.setFilters(new InputFilter[]{new CharactersFilter()});
        outputTextView = findViewById(R.id.outputTextView);
        encryptButton = findViewById(R.id.encryptButton);
        decryptButton = findViewById(R.id.decryptButton);
        selectKeyButton = findViewById(R.id.selectKeyButton);
        copyButton = findViewById(R.id.copyButton);
        currentKeyTextView = findViewById(R.id.currentKeyTextView);
    }


    private void onSelectKeyButtonPressed(View view){
        openKeyPickerDialog();
    }

    private void onCopyButtonPressed(View view) {
        putTextInClipboard(outputTextView.getText().toString());
        Snackbar.make(rootView,R.string.text_copied_output, BaseTransientBottomBar.LENGTH_LONG).show();
    }

    private void onEncryptButtonPressed(View view) {
        outputTextView.setText(encrypt(inputEditText.getText().toString(), inputCharacters, outputCharacters));
    }

    private void onDecryptButtonPressed(View view) {
        outputTextView.setText(decrypt(inputEditText.getText().toString(), inputCharacters, outputCharacters));
    }

    /**
     * Permet
     */
    private void openKeyPickerDialog() {
        //TODO : Compléter la création et l'ouverture du "KeyPickerDialog" dans cette fonction.
        KeyPickerDialog.make(this, KEY_LENGTH)
                .setKey(key)
                .setConfirmAction(this::theConfirmFunctionToCall)
                .setCancelAction(this::theCancelFunctionToCall)
                .show();
    }

    private void theConfirmFunctionToCall(Integer key) {
        keyFetchingActivation(key);
        keySelected = true;
    }

    private void keyFetchingActivation(Integer key){
        FetchKeyAsyncTask taskGetKey = new FetchKeyAsyncTask(this, this::onNotFoundError, this::onConnectivityError, this::onServerError, okHttpClient, objectMapper);

        taskGetKey.execute(key.toString());

        putKeyOnCurrentKeyTextView(key);

        this.key = key;

        decryptButton.setEnabled(true);
        encryptButton.setEnabled(true);
    }

    private void theCancelFunctionToCall() {
        if (intent.getType() != null && keySelected == false)
            finish();
    }

    private void putKeyOnCurrentKeyTextView(Integer key){
        currentKeyTextView.setText(R.string.text_current_key);
        currentKeyTextView.setText(currentKeyTextView.getText().toString().replace(INIT_KEY_VALUE,key.toString()));
    }

    @SuppressWarnings("ConstantConditions")
    private void putTextInClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText(getResources().getString(R.string.clipboard_encrypted_text), text));
    }

    private void onNotFoundError(){
        Snackbar.make(rootView,R.string.error_not_found, Snackbar.LENGTH_LONG).show();
    }

    private void onConnectivityError(){ Snackbar.make(rootView,R.string.text_connectivity_error, Snackbar.LENGTH_INDEFINITE).setAction(R.string.text_activate_wifi, this::activateWifi).show(); }

    private void onServerError(){
        Snackbar.make(rootView,R.string.text_server_error, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Permet d'accèder aux paramêtres wifi
     * @param view La view principale
     */
    private void activateWifi(View view){
        progressBar.setVisibility(View.INVISIBLE);
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    /**
     * Opérations à effectuer une fois la clé obtenue
     * @param postKeyInfo La clé JSON en format object qui contient les informations de la clé
     */
    @Override
    public void onPostFetched(KeyFromServer postKeyInfo) {
        progressBar.setVisibility(View.INVISIBLE);
        inputCharacters = postKeyInfo.getInputCharacters();
        outputCharacters = postKeyInfo.getOutputCharacters();
        key = postKeyInfo.getId();
    }

    /**
     * Operations a effectuer lors de l'obtention de la clé
     */
    @Override
    public void onPostFetching() { progressBar.setVisibility(View.VISIBLE); }


    /**
     *
     * @param userString Le string que l'utilisateur a entré
     * @param inputCharacters Le string qui sert à déterminer quelle est la position d'un charactère
     * @param outputCharacters Le string qui sert à remplacer des charactère selon leur position
     * @return Retourne un string encrypté
     */
    private StringBuilder encrypt(String userString , String inputCharacters, String outputCharacters)
    {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < userString.length(); i++)
        {
            Character currentCharacter = userString.charAt(i);

            stringBuilder.append(outputCharacters.substring(inputCharacters.indexOf(currentCharacter), inputCharacters.indexOf(currentCharacter) + 1));
        }

        return stringBuilder;
    }

    /**
     *
     * @param userString Le string que l'utilisateur a entré
     * @param inputCharacters Le string qui sert à déterminer quelle est la position d'un charactère
     * @param outputCharacters Le string qui sert à remplacer des charactère selon leur position
     * @return Retourne un string décrypté
     */
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
