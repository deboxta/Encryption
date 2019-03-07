package ca.csf.mobile1.tp2.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import ca.csf.mobile1.tp2.R;
import ca.csf.mobile1.util.view.CharactersFilter;
import ca.csf.mobile1.util.view.KeyPickerDialog;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;

import static org.koin.java.standalone.KoinJavaComponent.get;

public class MainActivity extends AppCompatActivity {

    private static final int KEY_LENGTH = 5;
    private static final int MAX_KEY_VALUE = (int) Math.pow(10, KEY_LENGTH) - 1;

    private OkHttpClient okHttpClient;
    private ObjectMapper objectMapper;

    private View rootView;
    private EditText inputEditText;
    private TextView outputTextView;
    private Button encryptButton;
    private Button decryptButton;
    private TextView currentKeyTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createDeView();
        createDeDependencies();
    }

    private void createDependencies() {
        okHttpClient = get(OkHttpClient.class);
        objectMapper = get(ObjectMapper.class);
    }

    private void createView() {
        setContentView(R.layout.activity_main);

        rootView = findViewById(R.id.rootView);
        progressBar = findViewById(R.id.progressBar);
        inputEditText = findViewById(R.id.inputEditText);
        inputEditText.setFilters(new InputFilter[]{new CharactersFilter()});
        outputTextView = findViewById(R.id.outputTextView);
        encryptButton = findViewById(R.id.encryptButton);
        decryptButton = findViewById(R.id.decryptButton);
        currentKeyTextView = findViewById(R.id.currentKeyTextView);
    }

    private void openKeyPickerDialog() {
        //TODO : Compléter la création et l'ouverture du "KeyPickerDialog" dans cette fonction.
        KeyPickerDialog.make(this, KEY_LENGTH)
                //.setKey(1337)
                //.setConfirmAction(this::theConfirmFunctionToCall)
                //.setCancelAction(this::theCancelFunctionToCall)
                .show();
    }

    @SuppressWarnings("ConstantConditions")
    private void putTextInClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText(getResources().getString(R.string.clipboard_encrypted_text), text));
    }

}
