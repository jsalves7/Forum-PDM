package pt.uac.qa.client.http;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by Patrício Cordeiro <patricio.cordeiro@gmail.com> on 27-12-2019.
 */
public class HttpResponse {
    private final Map<String, List<String>> headers;
    private final int statusCode;
    private final String statusMessage;
    private final String content;
    private final Charset encoding;
    private final int contentLength;

    HttpResponse(Map<String, List<String>> headers,
                 int statusCode,
                 String statusMessage,
                 String content,
                 Charset encoding,
                 int contentLength) {
        this.headers = headers;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.content = content;
        this.encoding = encoding;
        this.contentLength = contentLength;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getContent() {
        return content;
    }

    public Charset getEncoding() {
        return encoding;
    }

    public int getContentLength() {
        return contentLength;
    }
}
