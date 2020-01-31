package pt.uac.qa.client;

import android.content.Context;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.annotation.NonNull;
import pt.uac.qa.QAApp;

/**
 * Created by Patrício Cordeiro <patricio.cordeiro@gmail.com> on 26-12-2019.
 */
class AbstractClient {
    private static final String KEY_BASE_URL = "base_url";

    private final String baseUrl;
    final Context context;

    AbstractClient(@NonNull final Context context) {
        final QAApp app = (QAApp) context.getApplicationContext();
        this.baseUrl = app.getMetadata(KEY_BASE_URL);
        this.context = context;
    }

    <T> T executeRequest(final Request<T> request) throws ClientException {
        try {
            final Response<T> response = request.execute();
            response.validate();
            return response.getData();
        } catch (JSONException | IOException e) {
            throw new ClientException(e);
        }
    }

    URL url(final String partialUrl) throws MalformedURLException {
        final String partial = partialUrl.startsWith("/")
                ? partialUrl.substring(1)
                : partialUrl;

        final String fullUrl = baseUrl.endsWith("/")
                ? baseUrl + partial
                : baseUrl + "/" + partial;

        return new URL(fullUrl);
    }
}
