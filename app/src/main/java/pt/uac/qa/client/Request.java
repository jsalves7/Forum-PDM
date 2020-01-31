package pt.uac.qa.client;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by Patrício Cordeiro <patricio.cordeiro@gmail.com> on 27-12-2019.
 */
interface Request<T> {
    Response<T> execute() throws IOException, JSONException;
}
