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
import ca.csf.mobile1.tp2.activity.Crypt.DecryptTask;
import ca.csf.mobile1.tp2.activity.Crypt.EncryptTask;
import ca.csf.mobile1.tp2.activity.JSON.KeyFromServer;
import ca.csf.mobile1.tp2.activity.Server.FetchKeyAsyncTask;
import ca.csf.mobile1.util.view.CharactersFilter;
import ca.csf.mobile1.util.view.KeyPickerDialog;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;

import java.util.Random;

import static org.koin.java.standalone.KoinJavaComponent.get;

public class MainActivity extends AppCompatActivity implements FetchKeyAsyncTask.Listener, EncryptTask.Listener, DecryptTask.Listener{

    private static final int KEY_LENGTH = 5;
    private static final int MAX_KEY_VALUE = (int) Math.pow(10, KEY_LENGTH) - 1;
    public static final String INIT_KEY_VALUE = "%05d";
    public static final String STRING_NULL = "";

    private OkHttpClient okHttpClient;
    private ObjectMapper objectMapper;
    private Intent intent;

    private Integer key;
    private Random rand;
    private String inputCharacters;
    private String outputCharacters;

    private boolean keySelected;
    private boolean keyPickerDialogueIsOpen;

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

        //Afin de différencier s'il s'agit d'un début d'application suite à un changement d'orientation ou non
        if (savedInstanceState != null){
            restoreVariables(savedInstanceState);
        } else {
            normalStartUpSettings();
        }

        intent = getIntent();
        if("text/plain".equals(intent.getType())&& !keySelected) {
            inputEditText.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
            openKeyPickerDialog();
        }
    }

    /**
     * Sauveguarde toutes les données utiles lors du changement d'orientation pour plus tard
     * @param outState variable de sauveguarde qui contient les informations de sauveguarde
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("CURRENT_INPUT", inputEditText.getText().toString());
        outState.putString("CURRENT_OUTPUT",outputTextView.getText().toString());
        outState.putString("CURRENT_KEY_TEXT", currentKeyTextView.getText().toString());
        outState.putInt("CURRENT_KEY", key);
        outState.putBoolean("CURRENT_KEY_PICKER", keyPickerDialogueIsOpen);
        outState.putBoolean("CURRENT_BUTTONS_STATE", encryptButton.isEnabled());
        outState.putBoolean("CURRENT_KEY_SELECTED", keySelected);
        outState.putString("CURRENT_INPUT_CHARACTERS", inputCharacters);
        outState.putString("CURRENT_OUTPUT_CHARACTERS", outputCharacters);
    }

    /**
     * Restaure les données importantes lors du changement de d'orientation
     * @param savedInstanceState Contient les sauveguardes de toutes les données
     */
    private void restoreVariables(Bundle savedInstanceState){
        inputEditText.setText(savedInstanceState.getString("CURRENT_INPUT"));
        outputTextView.setText(savedInstanceState.getString("CURRENT_OUTPUT"));
        currentKeyTextView.setText(savedInstanceState.getString("CURRENT_KEY_TEXT"));
        encryptButton.setEnabled(savedInstanceState.getBoolean("CURRENT_BUTTONS_STATE"));
        decryptButton.setEnabled(savedInstanceState.getBoolean("CURRENT_BUTTONS_STATE"));
        keySelected = savedInstanceState.getBoolean("CURRENT_KEY_SELECTED");
        inputCharacters = savedInstanceState.getString("CURRENT_INPUT_CHARACTERS");
        outputCharacters = savedInstanceState.getString("CURRENT_OUTPUT_CHARACTERS");
        key = savedInstanceState.getInt("CURRENT_KEY");
        keyPickerDialogueIsOpen = savedInstanceState.getBoolean("CURRENT_KEY_PICKER");
        if (keyPickerDialogueIsOpen){
            openKeyPickerDialog();
        }
    }

    /**
     * Initialise les variables lors de démarrage de l'application normal
     */
    private void normalStartUpSettings() {
        rand = new Random();
        key = rand.nextInt(MAX_KEY_VALUE);
        keySelected = false;
        keyPickerDialogueIsOpen = false;
        keyFetchingActivation(key);
        inputCharacters = null;
        outputCharacters = null;
    }

    /**
     * Permet de set toutes les variables qui en ont besoin
     */
    private void setVariables(){
        selectKeyButton.setOnClickListener(this::onSelectKeyButtonPressed);
        copyButton.setOnClickListener(this::onCopyButtonPressed);
        encryptButton.setOnClickListener(this::onEncryptButtonPressed);
        decryptButton.setOnClickListener(this::onDecryptButtonPressed);
        deactivateButtons();
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

    /**
     * Fait appel aux classes d'encryption pour effectuer le calcul seulement s'il y a du texte
     * @param view la vue
     */
    private void onEncryptButtonPressed(View view) {
        try{
            EncryptTask encrypt = new EncryptTask(this, inputCharacters, outputCharacters);
            if (!inputEditText.getText().toString().equals(STRING_NULL)){
                outputTextView.setText(encrypt.execute(inputEditText.getText().toString()).toString());
            } else {
                Snackbar.make(rootView, R.string.text_no_text_input, Snackbar.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Fait appel aux classes de décryption pour effectuer le calcul seulement s'il y a du texte
     * @param view La vue actuelle
     */
    private void onDecryptButtonPressed(View view) {
        try{
            DecryptTask decrypt = new DecryptTask(this, inputCharacters, outputCharacters);

            if (!inputEditText.getText().toString().equals(STRING_NULL)){
                outputTextView.setText(decrypt.execute(inputEditText.getText().toString()).toString());
            } else {
                Snackbar.make(rootView, R.string.text_no_text_input, Snackbar.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void openKeyPickerDialog() {
        KeyPickerDialog.make(this, KEY_LENGTH)
                .setKey(key)
                .setConfirmAction(this::theConfirmFunctionToCall)
                .setCancelAction(this::theCancelFunctionToCall)
                .show();
        keyPickerDialogueIsOpen = true;
    }

    /**
     * Appelée par la boite de dialogue de clé lorsque la clé est choisie
     * @param key La clé entrée en paramêtre
     */
    private void theConfirmFunctionToCall(Integer key) {
        keyFetchingActivation(key);
        keySelected = true;
    }

    /**
     * Appelée par la boite de dialogue de clé lorsque le choix est annulé
     */
    private void theCancelFunctionToCall() {
        keyPickerDialogueIsOpen = false;
        //Utile pour le cas ou l'Application aurait été ouverte par partage
        if (intent.getType() != null && !keySelected)
            finish();
    }

    /**
     * Fait appel à la classe de connection au serveur afin d'Aller chercher les informations de la clé
     * @param key La clé qui à préalablement été choisie au hazard ou manuellement
     */
    private void keyFetchingActivation(Integer key){
        FetchKeyAsyncTask taskGetKey = new FetchKeyAsyncTask(this, this::onNotFoundError, this::onConnectivityError, this::onServerError, okHttpClient, objectMapper);

        taskGetKey.execute(key.toString());

        this.key = key;

        putKeyOnCurrentKeyTextView(key);

        keyPickerDialogueIsOpen = false;

        activateButtons();
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

    private void deactivateButtons(){
        decryptButton.setEnabled(false);
        encryptButton.setEnabled(false);
    }

    private void activateButtons(){
        decryptButton.setEnabled(true);
        encryptButton.setEnabled(true);
    }

    private void onNotFoundError(){
        Snackbar.make(rootView,R.string.error_not_found, Snackbar.LENGTH_LONG).show();
    }

    private void onConnectivityError(){
        Snackbar.make(rootView,R.string.text_connectivity_error, Snackbar.LENGTH_INDEFINITE).setAction(R.string.text_activate_wifi, this::activateWifi).show();
    }

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

    @Override
    public void onEncrypted(StringBuilder encrypted)
    {
        progressBar.setVisibility(View.INVISIBLE);
        outputTextView.setText(encrypted);
    }

    @Override
    public void onEncrypting() { progressBar.setVisibility(View.VISIBLE); }

    @Override
    public void onDecrypted(StringBuilder decrypted)
    {
        progressBar.setVisibility(View.INVISIBLE);
        outputTextView.setText(decrypted);
    }

    @Override
    public void onDecrypting() { progressBar.setVisibility(View.VISIBLE); }
}
