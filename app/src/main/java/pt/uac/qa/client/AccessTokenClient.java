package pt.uac.qa.client;

import android.content.Context;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;

import pt.uac.qa.QAApp;
import pt.uac.qa.client.http.HTTP;
import pt.uac.qa.client.http.HttpClient;
import pt.uac.qa.client.http.HttpResponse;
import pt.uac.qa.model.AccessToken;

/**
 * Created by Patrício Cordeiro <patricio.cordeiro@gmail.com> on 27-12-2019.
 */
public class AccessTokenClient extends AbstractClient {
    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_CLIENT_SECRET = "client_secret";

    private final HttpClient httpClient;
    private final String clientId;
    private final String clientSecret;

    public AccessTokenClient(final Context context) {
        super(context);
        final QAApp app = (QAApp) context.getApplicationContext();
        this.httpClient = new HttpClient();
        this.clientId = app.getMetadata(KEY_CLIENT_ID);
        this.clientSecret = app.getMetadata(KEY_CLIENT_SECRET);
        setupClientAuthentication();
    }

    public AccessToken getAccessToken(final String username, final String password) throws ClientException {
        return executeRequest(new Request<AccessToken>() {
            @Override
            public Response<AccessToken> execute() throws IOException {
                final String passwordGrantType = String.format(
                        "grant_type=password&scopes=forum.usage+user.management&username=%s&password=%s",
                        URLEncoder.encode(username, "UTF-8"),
                        URLEncoder.encode(password, "UTF-8"));

                final HttpResponse response = httpClient.post(
                        url("/auth/access-token"), passwordGrantType, HTTP.MediaType.FORM);

                return new AccessTokenResponse(response);
            }
        });
    }

    public AccessToken refreshToken(final String refreshToken) throws ClientException {
        return executeRequest(new Request<AccessToken>() {
            @Override
            public Response<AccessToken> execute() throws IOException {
                final String refreshTokenGrantType = String.format(
                        "grant_type=refresh_token&refresh_token=%s",
                        URLEncoder.encode(refreshToken, "UTF-8"));

                final HttpResponse response = httpClient.post(
                        url("/auth/access-token"), refreshTokenGrantType, HTTP.MediaType.FORM);

                return new AccessTokenResponse(response);
            }
        });
    }

    private void setupClientAuthentication() {
        final String basicAuthString =
                String.format("%s:%s", clientId, clientSecret);
        httpClient.addHeader(HTTP.Header.AUTHORIZATION, "Basic " +
                Base64.encodeToString(
                        basicAuthString.getBytes(), Base64.NO_WRAP));
    }

    static final class AccessTokenResponse extends Response<AccessToken> {
        AccessTokenResponse(HttpResponse httpResponse) {
            super(httpResponse);
        }

        @Override
        AccessToken getData() throws JSONException {
            return AccessToken.fromJson(new JSONObject(httpResponse.getContent()));
        }
    }
}
