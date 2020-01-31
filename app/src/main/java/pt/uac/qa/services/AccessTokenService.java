package pt.uac.qa.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import pt.uac.qa.QAApp;
import pt.uac.qa.client.AccessTokenClient;
import pt.uac.qa.client.ClientException;
import pt.uac.qa.client.UserClient;
import pt.uac.qa.model.AccessToken;
import pt.uac.qa.model.User;

public class AccessTokenService extends IntentService {
    private static final String TAG = AccessTokenService.class.getSimpleName();

    private static final String ACTION_CREATE_TOKEN = "pt.uac.qa.services.action.CREATE_TOKEN";
    private static final String ACTION_REFRESH_TOKEN = "pt.uac.qa.services.action.REFRESH_TOKEN";

    private static final String PARAM_USERNAME = "pt.uac.qa.services.param.USERNAME";
    private static final String PARAM_PASSWORD = "pt.uac.qa.services.param.PASSWORD";
    private static final String PARAM_REFRESH_TOKEN = "pt.uac.qa.services.param.REFRESH_TOKEN";

    public static final String INTENT_FILTER = "pt.uac.qa.services.ACCESS_TOKEN_SERVICE";
    public static final String RESULT_ERROR = "pt.uac.qa.services.RESULT_ERROR";

    public AccessTokenService() {
        super("AccessTokenService");
    }

    public static void startCreateToken(
            final Context context, final String username, final String password) {
        final Intent intent = new Intent(context, AccessTokenService.class);

        intent.setAction(ACTION_CREATE_TOKEN);
        intent.putExtra(PARAM_USERNAME, username);
        intent.putExtra(PARAM_PASSWORD, password);

        context.startService(intent);
    }

    public static void startRefreshToken(
            final Context context, final String refreshToken) {
        final Intent intent = new Intent(context, AccessTokenService.class);

        intent.setAction(ACTION_REFRESH_TOKEN);
        intent.putExtra(PARAM_REFRESH_TOKEN, refreshToken);

        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_CREATE_TOKEN.equals(action)) {
                final String username = intent.getStringExtra(PARAM_USERNAME);
                final String password = intent.getStringExtra(PARAM_PASSWORD);
                createAccessToken(username, password);
            } else if (ACTION_REFRESH_TOKEN.equals(action)) {
                final String refreshToken = intent.getStringExtra(PARAM_REFRESH_TOKEN);
                refreshAccessToken(refreshToken);
            }
        }
    }

    private void createAccessToken(final String username, final String password) {
        try {
            final QAApp app = (QAApp) getApplication();
            final AccessToken token = login(username, password);
            app.setAccessToken(token);
            app.setUser(getUser(token));
            sendSuccessBroadcast();
        } catch (Exception e) {
            Log.e(TAG, null, e);
            sendErrorBroadcast(e);
        }
    }

    private void refreshAccessToken(final String refreshToken) {
        try {
            final QAApp app = (QAApp) getApplication();
            final AccessTokenClient accessTokenClient = new AccessTokenClient(this);
            final AccessToken accessToken = accessTokenClient.refreshToken(refreshToken);

            app.setAccessToken(accessToken);
            app.setUser(getUser(accessToken));
            sendSuccessBroadcast();
        } catch (Exception e) {
            Log.e(TAG, null, e);
            sendErrorBroadcast(e);
        }
    }

    private AccessToken login(final String username, final String password) throws ClientException {
        final AccessTokenClient accessTokenClient =
                new AccessTokenClient(this);
        return accessTokenClient.getAccessToken(username, password);
    }

    private User getUser(final AccessToken token) throws ClientException {
        final UserClient userClient = new UserClient(this);
        return userClient.getUser();
    }

    private void sendSuccessBroadcast() {
        final Intent intent = new Intent(INTENT_FILTER);
        sendBroadcast(intent);
    }

    private void sendErrorBroadcast(final Exception e) {
        final Intent intent = new Intent(INTENT_FILTER);
        intent.putExtra(RESULT_ERROR, e);
        sendBroadcast(intent);
    }
}
