package pt.uac.qa.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Patrício Cordeiro <patricio.cordeiro@gmail.com> on 27-12-2019.
 */
public class AccessToken {
    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private long createdAt;
    private long expiresIn;

    public AccessToken() {
        setCreatedAt(System.currentTimeMillis());
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public boolean isExpired() {
        return createdAt + expiresIn < System.currentTimeMillis();
    }

    public static AccessToken fromJson(final JSONObject jsonObject) throws JSONException {
        final AccessToken accessToken = new AccessToken();

        accessToken.setTokenType(jsonObject.getString("token_type"));
        accessToken.setExpiresIn(jsonObject.getInt("expires_in") * 1000);
        accessToken.setAccessToken(jsonObject.getString("access_token"));
        accessToken.setRefreshToken(jsonObject.getString("refresh_token"));

        return accessToken;
    }

    public JSONObject toJson() throws JSONException {
        final JSONObject jsonObject = new JSONObject();

        jsonObject.put("token_type", tokenType);
        jsonObject.put("access_token", accessToken);
        jsonObject.put("refresh_token", refreshToken);
        jsonObject.put("expires_in", expiresIn);
        jsonObject.put("created_at", createdAt);

        return jsonObject;
    }
}
