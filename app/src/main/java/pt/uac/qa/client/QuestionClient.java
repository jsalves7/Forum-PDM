package pt.uac.qa.client;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import pt.uac.qa.client.http.HttpResponse;
import pt.uac.qa.model.Question;

/**
 * Created by Patrício Cordeiro <patricio.cordeiro@gmail.com> on 27-12-2019.
 */
public final class QuestionClient extends AuthenticatedClient {
    public QuestionClient(@NonNull final Context context) {
        super(context);
    }

    public Question addQuestion(final String title, final String body, final List<String> tags) throws ClientException {
        return executeRequest(new AuthenticatedRequest<Question>() {
            @Override
            Response<Question> doExecute() throws IOException, JSONException {
                final HttpResponse response = httpClient.post(
                        url("/questions"), createJsonPayload(title, body, tags).toString());
                return new QuestionResponse(response);
            }
        });
    }

    public void deleteQuestion(final String questionId) throws ClientException {
        executeRequest(new AuthenticatedRequest<Void>() {
            @Override
            Response<Void> doExecute() throws IOException {
                final HttpResponse response =
                        httpClient.delete(url("/questions/" + questionId));

                return new Response<Void>(response) {
                    @Override
                    Void getData() {
                        return null;
                    }
                };
            }
        });
    }

    public Question updateQuestion(final String questionId, final String title, final String body) throws ClientException {
        return executeRequest(new AuthenticatedRequest<Question>() {
            @Override
            Response<Question> doExecute() throws IOException, JSONException {
                final HttpResponse response = httpClient.patch(
                        url("/questions/" + questionId), createJsonPayload(title, body).toString());
                return new QuestionResponse(response);
            }
        });
    }

    public List<Question> getMyQuestions() throws ClientException {
        return fetchQuestions("/my/questions");
    }

    public List<Question> getQuestions() throws ClientException {
        return fetchQuestions("/questions");
    }

    public Question getQuestion(final String questionId) throws ClientException {
        return executeRequest(new AuthenticatedRequest<Question>() {
            @Override
            Response<Question> doExecute() throws IOException {
                final HttpResponse response = httpClient.get(url("/questions/" + questionId));
                return new QuestionResponse(response);
            }
        });
    }

    private List<Question> fetchQuestions(final String partialUrl) throws ClientException {
        return executeRequest(new AuthenticatedRequest<List<Question>>() {
            @Override
            Response<List<Question>> doExecute() throws IOException {
                final HttpResponse response = httpClient.get(url(partialUrl));

                return new Response<List<Question>>(response) {
                    @Override
                    List<Question> getData() throws JSONException {
                        final JSONObject json = new JSONObject(httpResponse.getContent());
                        final List<Question> questions = new ArrayList<>();

                        if (!json.isNull("data")) {
                            final JSONArray array = json.getJSONArray("data");

                            for (int i = 0; i < array.length(); i++) {
                                final Question question = Question.fromJson(array.getJSONObject(i));
                                questions.add(question);
                            }
                        }

                        return questions;
                    }
                };
            }
        });
    }

    private JSONObject createJsonPayload(final String title, final String body) throws JSONException {
        return createJsonPayload(title, body, null);
    }

    private JSONObject createJsonPayload(final String title, final String body, final List<String> tags) throws JSONException {
        final JSONObject payload = new JSONObject();

        payload.put("title", title);
        payload.put("body", body);

        if (tags != null && !tags.isEmpty()) {
            final JSONArray array = new JSONArray();

            for (final String tag : tags) {
                array.put(tag);
            }

            payload.put("tags", array);
        }

        return payload;
    }

    private static final class QuestionResponse extends Response<Question> {
        QuestionResponse(final HttpResponse httpResponse) {
            super(httpResponse);
        }

        @Override
        Question getData() throws JSONException {
            return Question.fromJson(new JSONObject(httpResponse.getContent()));
        }
    }
}
