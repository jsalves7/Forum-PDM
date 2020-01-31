package pt.uac.qa.client.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Patrício Cordeiro <patricio.cordeiro@gmail.com> on 27-12-2019.
 */
public class HttpClient {
    private final Map<String, Set<String>> headers = new HashMap<>();

    @SuppressWarnings("ConstantConditions")
    public void addHeader(final String name, final String value) {
        if (!headers.containsKey(name)) {
            headers.put(name, new LinkedHashSet<String>());
        }

        headers.get(name).add(value);
    }

    public HttpResponse get(final URL url) throws IOException {
        return get(url, HTTP.MediaType.APP_JSON);
    }

    public HttpResponse get(final URL url, final String mediaType) throws IOException {
        return executeRequest(HTTP.Method.GET, url, null, mediaType);
    }

    public HttpResponse post(final URL url) throws IOException {
        return post(url, null);
    }

    public HttpResponse post(final URL url, final String body) throws IOException {
        return post(url, body, HTTP.MediaType.APP_JSON);
    }

    public HttpResponse post(final URL url, final String body, final String mediaType) throws IOException {
        return executeRequest(HTTP.Method.POST, url, body, mediaType);
    }

    public HttpResponse put(final URL url) throws IOException {
        return put(url, null);
    }

    public HttpResponse put(final URL url, final String body) throws IOException {
        return put(url, body, HTTP.MediaType.APP_JSON);
    }

    public HttpResponse put(final URL url, final String body, final String mediaType) throws IOException {
        return executeRequest(HTTP.Method.PUT, url, body, mediaType);
    }

    public HttpResponse patch(final URL url) throws IOException {
        addHeader("X-HTTP-Method-Override", "PATCH");
        return post(url);
    }

    public HttpResponse patch(final URL url, final String body) throws IOException {
        addHeader("X-HTTP-Method-Override", "PATCH");
        return post(url, body);
    }

    public HttpResponse delete(final URL url) throws IOException {
        return executeRequest(HTTP.Method.DELETE, url, null, null);
    }

    private HttpResponse executeRequest(final String httpMethod, final URL url, final String body, final String mediaType) throws IOException {
        final HttpURLConnection connection = connect(url);

        try {
            connection.setRequestMethod(httpMethod);
            writeHeaders(connection);
            writeContent(connection, body, mediaType);
            return getResponse(connection);
        } finally {
            disconnect(connection);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void writeHeaders(final HttpURLConnection connection) {
        for (final String name : headers.keySet()) {
            for (final String value : headers.get(name)) {
                connection.addRequestProperty(name, value);
            }
        }
    }

    private void writeContent(final HttpURLConnection connection, final String content, final String mediaType) throws IOException {
        if (content == null)
            return;

        final byte[] data = content.getBytes(StandardCharsets.UTF_8);

        connection.setDoOutput(true);
        connection.addRequestProperty(HTTP.Header.CONTENT_TYPE, mediaType);

        try (final OutputStream out = connection.getOutputStream()) {
            out.write(data, 0, data.length);
        }
    }

    private HttpResponse getResponse(final HttpURLConnection connection) throws IOException {
        final int statusCode = connection.getResponseCode();
        final String content = statusCode >= HTTP.Status.OK && statusCode < HTTP.Status.BAD_REQUEST
                ? consumeStream(connection.getInputStream())
                : consumeStream(connection.getErrorStream());

        return new HttpResponse(
                connection.getHeaderFields(),
                statusCode,
                connection.getResponseMessage(),
                content,
                StandardCharsets.UTF_8,
                content.length()
        );
    }

    private String consumeStream(final InputStream stream) throws IOException {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            final StringBuilder buffer = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            return buffer.toString();
        }
    }

    private HttpURLConnection connect(final URL url) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.addRequestProperty(HTTP.Header.HOST, url.getHost());
        return connection;
    }

    private void disconnect(final HttpURLConnection connection) {
        if (connection != null) {
            connection.disconnect();
        }
    }
}
