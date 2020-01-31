package pt.uac.qa.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import androidx.annotation.NonNull;

/**
 * Created by Patrício Cordeiro <patricio.cordeiro@gmail.com> on 27-12-2019.
 */
public class User implements Serializable {
    private String userId;
    private String name;
    private String email;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public JSONObject toJson() throws JSONException {
        final JSONObject json = new JSONObject();

        json.put("userId", userId);
        json.put("name", name);
        json.put("email", email);

        return json;
    }

    public static User fromJson(@NonNull final JSONObject json) throws JSONException {
        final User user = new User();

        user.setUserId(json.getString("userId"));
        user.setName(json.getString("name"));
        user.setEmail(json.getString("email"));

        return user;
    }
}
