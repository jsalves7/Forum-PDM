package pt.uac.qa.client;

import android.content.Context;

import org.json.JSONException;

import java.io.IOException;

import androidx.annotation.NonNull;
import pt.uac.qa.QAApp;
import pt.uac.qa.client.http.HTTP;
import pt.uac.qa.client.http.HttpClient;
import pt.uac.qa.model.AccessToken;

/**
 * Created by Patrício Cordeiro <patricio.cordeiro@gmail.com> on 27-12-2019.
 */
class AuthenticatedClient extends AbstractClient {
    final HttpClient httpClient;

    AuthenticatedClient(@NonNull final Context context) {
        super(context);
        httpClient = new HttpClient();
        setupBearerAuthentication();
    }

    @Override
    <T> T executeRequest(final Request<T> request) throws ClientException {
        try {
            return super.executeRequest(request);
        } catch (SecurityException e) {
            if (request instanceof AuthenticatedRequest) {
                final AuthenticatedRequest req = (AuthenticatedRequest) request;

                if (req.getCounter() < 2) {
                    refreshToken();
                    return executeRequest(request);
                }
            }

            throw e;
        }
    }

    private void refreshToken() throws ClientException {
        final QAApp app = (QAApp) context.getApplicationContext();
        final AccessTokenClient accessTokenClient = new AccessTokenClient(app);
        final AccessToken token = accessTokenClient
                .refreshToken(app.getAccessToken().getRefreshToken());

        app.setAccessToken(accessTokenClient.refreshToken(token.getRefreshToken()));
        setupBearerAuthentication();
    }

    private void setupBearerAuthentication() {
        final QAApp app = (QAApp) context.getApplicationContext();
        final AccessToken accessToken = app.getAccessToken();
        httpClient.addHeader(HTTP.Header.AUTHORIZATION,
                String.format("%s %s",
                        accessToken.getTokenType(), accessToken.getAccessToken()));
    }

    static abstract class AuthenticatedRequest<T> implements Request<T> {
        private int counter;

        @Override
        public final Response<T> execute() throws IOException, JSONException {
            ++counter;
            return doExecute();
        }

        int getCounter() {
            return counter;
        }

        abstract Response<T> doExecute() throws IOException, JSONException;
    }
}
