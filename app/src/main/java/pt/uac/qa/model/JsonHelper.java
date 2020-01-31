package pt.uac.qa.model;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;

/**
 * Created by Patrício Cordeiro <patricio.cordeiro@gmail.com> on 27-12-2019.
 */
final class JsonHelper {
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private final JSONObject json;

    JsonHelper(@NonNull final JSONObject json) {
        this.json = Objects.requireNonNull(json);
    }

    User getUser(final String name) throws JSONException {
        return User.fromJson(json.getJSONObject(name));
    }

    JSONObject getJSONObject(final String name) throws JSONException {
        return json.getJSONObject(name);
    }

    Date getDate(final String name) throws JSONException {
        final JSONObject obj = json.getJSONObject(name);
        return parseDate(obj.optString("date"));
    }

    String getString(final String name) throws JSONException {
        if (json.has(name))
            return json.getString(name);

        return null;
    }

    int getInt(final String name) throws JSONException {
        if (json.has(name)) {
            return json.getInt(name);
        }

        return 0;
    }

    boolean getBoolean(final String name) throws JSONException {
        if (json.has(name)) {
            return json.getBoolean(name);
        }

        return false;
    }

    List<String> getTagList(final String name) throws JSONException {
        final List<String> list = new ArrayList<>();

        if (!json.isNull(name)) {
            final JSONArray array = json.getJSONArray(name);

            for (int i = 0; i < array.length(); i++) {
                final String test = array.getString(i);
                String tag;

                if (test.startsWith("{")) {
                    final JSONObject o = array.getJSONObject(i);
                    tag = o.getString("description");
                } else {
                    tag = array.getString(i);
                }

                if (!TextUtils.isEmpty(tag)) {
                    list.add(array.getString(i));
                }
            }
        }

        return list;
    }

    private Date parseDate(final String input) {
        try {
            return input != null
                    ? dateFormat.parse(input)
                    : null;
        } catch (ParseException e) {
            return null;
        }
    }
}
