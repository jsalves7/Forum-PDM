package pt.uac.qa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import pt.uac.qa.model.AccessToken;
import pt.uac.qa.services.AccessTokenService;

import static pt.uac.qa.services.AccessTokenService.RESULT_ERROR;

public class SplashActivity extends AppCompatActivity {
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (intent.hasExtra(RESULT_ERROR)) {
                final Exception error = (Exception) intent.getSerializableExtra(RESULT_ERROR);
                Toast.makeText(SplashActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                launchLoginActivity();
            } else {
                launchMainActivity();
            }
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerReceiver(receiver,
                new IntentFilter(AccessTokenService.INTENT_FILTER));
        setContentView(R.layout.activity_splash);
        hideSystemUI();
        checkAccessToken();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void checkAccessToken() {
        final QAApp app = (QAApp) getApplication();
        final AccessToken accessToken = app.getAccessToken();

        if (accessToken == null) {
            launchLoginActivity();
        } else if (accessToken.isExpired()) {
            refreshAccessToken(accessToken);
        } else {
            launchMainActivity();
        }
    }

    private void launchLoginActivity() {
        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchMainActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void refreshAccessToken(final AccessToken accessToken) {
        AccessTokenService.startRefreshToken(
                this, accessToken.getRefreshToken());
    }
}