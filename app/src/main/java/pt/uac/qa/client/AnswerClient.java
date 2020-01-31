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
import pt.uac.qa.model.Answer;

/**
 * Created by Patrício Cordeiro <patricio.cordeiro@gmail.com> on 27-12-2019.
 */
public class AnswerClient extends AuthenticatedClient {
    public AnswerClient(@NonNull final Context context) {
        super(context);
    }

    public List<Answer> getMyAnswers() throws ClientException {
        return executeRequest(new AuthenticatedRequest<List<Answer>>() {
            @Override
            Response<List<Answer>> doExecute() throws IOException {
                final HttpResponse response =
                        httpClient.get(url("/my/answers"));

                return new Response<List<Answer>>(response) {
                    @Override
                    List<Answer> getData() throws JSONException {
                        final JSONObject json = new JSONObject(response.getContent());
                        final List<Answer> answers = new ArrayList<>();

                        if (!json.isNull("data")) {
                            final JSONArray array = json.getJSONArray("data");

                            for (int i = 0; i < array.length(); i++) {
                                final Answer answer = Answer.fromJson(array.getJSONObject(i));
                                answers.add(answer);
                            }
                        }

                        return answers;
                    }
                };
            }
        });
    }

    public Answer addAnswer(final String questionId, final String answer) throws ClientException {
        return executeRequest(new AuthenticatedRequest<Answer>() {
            @Override
            Response<Answer> doExecute() throws IOException, JSONException {
                final HttpResponse response = httpClient.post(
                        url(String.format("/questions/%s/answers", questionId)), createJsonPayload(answer).toString());
                return new AnswerResponse(response);
            }
        });
    }

    public Answer getAnswer(final String answerId) throws ClientException {
        return executeRequest(new AuthenticatedRequest<Answer>() {
            @Override
            Response<Answer> doExecute() throws IOException {
                final HttpResponse response = httpClient.get(
                        url(String.format("/answers/%s", answerId)));
                return new AnswerResponse(response);
            }
        });
    }

    public void deleteAnswer(final String answerId) throws ClientException {
        executeRequest(new AuthenticatedRequest<Void>() {
            @Override
            Response<Void> doExecute() throws IOException {
                final HttpResponse response = httpClient.delete(url("/answers/" + answerId));

                return new Response<Void>(response) {
                    @Override
                    Void getData() {
                        return null;
                    }
                };
            }
        });
    }

    public Answer updateAnswer(final String answerId, final String answer) throws ClientException {
        return executeRequest(new AuthenticatedRequest<Answer>() {
            @Override
            Response<Answer> doExecute() throws IOException, JSONException {
                final HttpResponse response = httpClient.patch(
                        url(String.format("/answers/%s", answerId)), createJsonPayload(answer).toString());

                return new AnswerResponse(response);
            }
        });
    }

    public Answer markAnswerCorrect(final String answerId) throws ClientException {
        return executeRequest(new AuthenticatedRequest<Answer>() {
            @Override
            Response<Answer> doExecute() throws IOException {
                final HttpResponse response = httpClient.patch(
                        url(String.format("/answers/%s/mark-as-correct", answerId)));

                return new AnswerResponse(response);
            }
        });
    }

    public Answer voteAnswerUp(final String answerId) throws ClientException {
        return executeRequest(new AuthenticatedRequest<Answer>() {
            @Override
            Response<Answer> doExecute() throws IOException {
                final HttpResponse response = httpClient.put(
                        url(String.format("/answers/%s/vote-up", answerId)));

                return new AnswerResponse(response);
            }
        });
    }

    public Answer voteAnswerDown(final String answerId) throws ClientException {
        return executeRequest(new AuthenticatedRequest<Answer>() {
            @Override
            Response<Answer> doExecute() throws IOException {
                final HttpResponse response = httpClient.put(
                        url(String.format("/answers/%s/vote-down", answerId)));

                return new AnswerResponse(response);
            }
        });
    }

    private JSONObject createJsonPayload(final String answerBody) throws JSONException {
        final JSONObject payload = new JSONObject();
        payload.put("body", answerBody);
        return payload;
    }

    private static final class AnswerResponse extends Response<Answer> {
        AnswerResponse(final HttpResponse httpResponse) {
            super(httpResponse);
        }

        @Override
        Answer getData() throws JSONException {
            return Answer.fromJson(new JSONObject(httpResponse.getContent()));
        }
    }
}
