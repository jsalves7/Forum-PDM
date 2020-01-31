package pt.uac.qa.client;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.annotation.NonNull;
import pt.uac.qa.client.http.HttpResponse;
import pt.uac.qa.model.User;

/**
 * Created by Patrício Cordeiro <patricio.cordeiro@gmail.com> on 27-12-2019.
 */
public final class UserClient extends AuthenticatedClient {
    public UserClient(@NonNull final Context context) {
        super(context);
    }

    public User getUser() throws ClientException {
        return executeRequest(new AuthenticatedRequest<User>() {
            @Override
            Response<User> doExecute() throws IOException {
                final HttpResponse response =
                        httpClient.get(url("/users/me"));

                return new Response<User>(response) {
                    @Override
                    User getData() throws JSONException {
                        return User.fromJson(new JSONObject(response.getContent()));
                    }
                };
            }
        });
    }
}
