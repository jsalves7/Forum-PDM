package pt.uac.qa;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import pt.uac.qa.model.AccessToken;
import pt.uac.qa.model.User;

/**
 * Created by Patrício Cordeiro <patricio.cordeiro@gmail.com> on 27-12-2019.
 */
public class QAApp extends Application {
    private static final String TAG = "QAApp";

    private static final String PREF_NAME_QAAPP = "qaapp";
    private static final String PREF_KEY_ACCESS_TOKEN = "pt.uac.qa.prefs.ACCESS_TOKEN";
    private static final String PREF_KEY_USER = "pt.uac.qa.prefs.USER";

    private AccessToken accessToken;
    private User user;

    @Override
    public void onCreate() {
        super.onCreate();
        loadAccessToken();
        loadUser();
    }

    public void logout() {
        accessToken = null;
        user = null;
        deleteAccessToken();
        deleteUser();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        saveUser();
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(final AccessToken accessToken) {
        this.accessToken = accessToken;
        saveAccessToken();
    }

    public String getMetadata(final String key) {
        try {
            final ApplicationInfo ai = getPackageManager()
                    .getApplicationInfo(
                            getPackageName(), PackageManager.GET_META_DATA);
            final Bundle bundle = ai.metaData;
            return bundle.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }

        throw new IllegalStateException("Missing 'base_url' metadata");
    }

    private void loadAccessToken() {
        final SharedPreferences sp =
                getSharedPreferences(PREF_NAME_QAAPP, MODE_PRIVATE);
        final String accessTokenJson =
                sp.getString(PREF_KEY_ACCESS_TOKEN, null);

        if (accessTokenJson != null) {
            try {
                accessToken = AccessToken.fromJson(new JSONObject(accessTokenJson));
            } catch (JSONException ex) {
                Log.e(TAG, "Error loading access token", ex);
            }
        }
    }

    private void saveAccessToken() {
        try {
            final SharedPreferences sp = getSharedPreferences(PREF_NAME_QAAPP, MODE_PRIVATE);
            final SharedPreferences.Editor editor = sp.edit();

            editor.putString(PREF_KEY_ACCESS_TOKEN,
                    accessToken.toJson().toString());
            editor.apply();
        } catch (Exception ex) {
            Log.e(TAG, "Error saving the access token", ex);
        }
    }

    private void deleteAccessToken() {
        final SharedPreferences sp = getSharedPreferences(PREF_NAME_QAAPP, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        editor.remove(PREF_KEY_ACCESS_TOKEN);
        editor.apply();
    }

    private void saveUser() {
        try {
            final SharedPreferences sp = getSharedPreferences(PREF_NAME_QAAPP, MODE_PRIVATE);
            final SharedPreferences.Editor editor = sp.edit();

            editor.putString(PREF_KEY_USER, user.toJson().toString());
            editor.apply();
        } catch (Exception ex) {
            Log.e(TAG, "Error saving the user", ex);
        }
    }

    private void loadUser() {
        final SharedPreferences sp = getSharedPreferences(PREF_NAME_QAAPP, MODE_PRIVATE);
        final String userJson = sp.getString(PREF_KEY_USER, null);

        if (userJson != null) {
            try {
                user = User.fromJson(new JSONObject(userJson));
            } catch (JSONException ex) {
                Log.e(TAG, "Error loading user", ex);
            }
        }
    }

    private void deleteUser() {
        final SharedPreferences sp = getSharedPreferences(PREF_NAME_QAAPP, MODE_PRIVATE);
        final SharedPreferences.Editor edit = sp.edit();
        edit.remove(PREF_KEY_USER);
        edit.apply();
    }
}
