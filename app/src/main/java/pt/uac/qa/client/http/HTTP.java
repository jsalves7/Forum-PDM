package pt.uac.qa.client.http;

/**
 * Created by Patrício Cordeiro <patricio.cordeiro@gmail.com> on 27-12-2019.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class HTTP {
    private HTTP() {}

    public static final class Method {
        public static final String GET = "GET";
        public static final String POST = "POST";
        public static final String PUT = "PUT";
        public static final String DELETE = "DELETE";
        public static final String PATCH = "PATCH";

        private Method() {}
    }

    public static final class Status {
        public static final int OK = 200;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int INTERNAL_SERVER_ERROR = 500;
        private Status() {}
    }

    public static final class Header {
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String AUTHORIZATION = "Authorization";
        public static final String HOST = "Host";

        private Header() {}
    }

    public static final class MediaType {
        public static final String PAIN_TEXT = "text/plain";
        public static final String HTML_TEXT = "text/html";
        public static final String APP_JSON = "application/json";
        public static final String FORM = "application/x-www-form-urlencoded";

        private MediaType() {}
    }
}
