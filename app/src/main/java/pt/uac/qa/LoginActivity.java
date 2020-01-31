package pt.uac.qa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import pt.uac.qa.services.AccessTokenService;

import static pt.uac.qa.services.AccessTokenService.RESULT_ERROR;

public class LoginActivity extends AppCompatActivity {
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (intent.hasExtra(RESULT_ERROR)) {
                final Exception error = (Exception) intent.getSerializableExtra(RESULT_ERROR);

                if (error instanceof SecurityException) {
                    showLoginFailed("Login / Password inválidos");
                } else {
                    showLoginFailed("Ocorreu um erro, tente mais tarde");
                }
            } else {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }
    };

    private ProgressBar loadingProgressBar;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        registerReceiver(receiver, new IntentFilter(AccessTokenService.INTENT_FILTER));
        setupLoginForm();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void setupLoginForm() {
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login();
                    return true;
                }

                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                login();
            }
        });
    }

    private void login() {
        if (validateForm()) {
            loginButton.setEnabled(false);
            loadingProgressBar.setVisibility(View.VISIBLE);
            AccessTokenService.startCreateToken(
                    this,
                    usernameEditText.getText().toString(),
                    passwordEditText.getText().toString());
        }
    }

    private boolean validateForm() {
        boolean result = true;

        if (TextUtils.isEmpty(usernameEditText.getText())) {
            usernameEditText.setError("Introduza o seu email");
            result = false;
        }

        if (TextUtils.isEmpty(passwordEditText.getText())) {
            passwordEditText.setError("Introduza a sua password");
            result = false;
        }

        return result;
    }

    private void showLoginFailed(final String message) {
        loadingProgressBar.setVisibility(View.GONE);
        loginButton.setEnabled(true);
        Toast.makeText(
                getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
