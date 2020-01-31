package pt.uac.qa;

/**
 * Created by Patrício Cordeiro <patricio.cordeiro@gmail.com> on 17-01-2020.
 */
public final class StringUtils {
    private StringUtils() {}

    public static String join(final String joint, final Iterable<String> strings) {
        final StringBuilder sb = new StringBuilder();

        for (final String string : strings) {
            if (sb.length() > 0)
                sb.append(joint);

            sb.append(string);
        }

        return sb.toString();
    }
}
