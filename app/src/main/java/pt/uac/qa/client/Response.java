package pt.uac.qa.client;

import org.json.JSONException;

import pt.uac.qa.client.http.HTTP;
import pt.uac.qa.client.http.HttpResponse;

/**
 * Created by Patrício Cordeiro <patricio.cordeiro@gmail.com> on 27-12-2019.
 */
abstract class Response<T> {
    final HttpResponse httpResponse;

    Response(final HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    void validate() throws ClientException {
        final int statusCode = httpResponse.getStatusCode();

        if (httpResponse.getStatusCode() >= HTTP.Status.BAD_REQUEST) {
            if (statusCode == HTTP.Status.UNAUTHORIZED) {
                throw new SecurityException(httpResponse.getContent());
            }

            throw new ClientException(httpResponse.getContent());
        }

    }

    abstract T getData() throws JSONException;
}
