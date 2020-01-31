package pt.uac.qa.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pt.uac.qa.client.AnswerClient;
import pt.uac.qa.client.ClientException;
import pt.uac.qa.model.Answer;

public class AnswerService extends IntentService {
    private static final String TAG = "AnswersService";

    private static final String ACTION_ADD_ANSWER = "pt.uac.qa.services.action.ADD_ANSWER";
    private static final String ACTION_FETCH_ANSWER = "pt.uac.qa.services.action.FETCH_ANSWER";
    private static final String ACTION_FETCH_MY_ANSWERS = "pt.uac.qa.services.action.FETCH_MY_ANSWERS";
    private static final String ACTION_DELETE_ANSWERS = "pt.uac.qa.services.action.DELETE_ANSWERS";
    private static final String ACTION_UPDATE_ANSWER = "pt.uac.qa.services.action.UPDATE_ANSWER";
    private static final String ACTION_POSITIVE_VOTE_ANSWER = "pt.uac.qa.services.action.POSITIVE_VOTE_ANSWER";
    private static final String ACTION_NEGATIVE_VOTE_ANSWER = "pt.uac.qa.services.action.NEGATIVE_VOTE_ANSWER";
    private static final String ACTION_MARK_AS_CORRECT_ANSWER = "pt.uac.qa.services.action.MARK_AS_CORRECT_ANSWER";


    // TODO: Rename parameters
    private static final String EXTRA_PARAM_QUESTION_ID = "pt.uac.qa.services.extra.PARAM_QUESTION_ID";
    private static final String EXTRA_PARAM_ANSWER_BODY = "pt.uac.qa.services.extra.PARAM_ANSWER_BODY";
    private static final String EXTRA_PARAM_ANSWER_ID = "pt.uac.qa.services.PARAM_ANSWER_ID";
    private static final String EXTRA_PARAM_ANSWERS_IDS = "pt.uac.qa.services.extra.PARAM_ANSWERS_IDS";

    public static final String INTENT_FILTER = "pt.uac.qa.services.ANSWER_SERVICE";
    public static final String RESULT_ERROR = "pt.uac.qa.services.RESULT_ERROR";
    public static final String RESULT_ANSWERS = "pt.uac.qa.services.RESULT_ANSWERS";
    public static final String RESULT_ANSWER = "pt.uac.qa.services.RESULT_ANSWER";


    public AnswerService() {
        super("AnswerService");
    }

    public static void addAnswer(Context context, String questionId, String answerBody) {
        Intent intent = new Intent(context, AnswerService.class);
        intent.setAction(ACTION_ADD_ANSWER);
        intent.putExtra(EXTRA_PARAM_QUESTION_ID, questionId);
        intent.putExtra(EXTRA_PARAM_ANSWER_BODY, answerBody);
        context.startService(intent);
    }

    public static void fetchAnswer(Context context, String answerId) {
        Intent intent = new Intent(context, AnswerService.class);
        intent.setAction(ACTION_FETCH_ANSWER);
        intent.putExtra(EXTRA_PARAM_ANSWER_ID, answerId);
        context.startService(intent);
    }

    public static void fetchMyAnswers(Context context) {
        Intent intent = new Intent(context, AnswerService.class);
        intent.setAction(ACTION_FETCH_MY_ANSWERS);
        context.startService(intent);
    }

    public static void deleteAnswers(Context context, String... answerIds) {
        Intent intent = new Intent(context, AnswerService.class);
        intent.setAction(ACTION_DELETE_ANSWERS);
        intent.putExtra(EXTRA_PARAM_ANSWERS_IDS, answerIds);
        context.startService(intent);
    }

    public static void updateAnswer(Context context, String answerId, String answerBody) {
        Intent intent = new Intent(context, AnswerService.class);
        intent.setAction(ACTION_UPDATE_ANSWER);
        intent.putExtra(EXTRA_PARAM_ANSWER_ID, answerId);
        intent.putExtra(EXTRA_PARAM_ANSWER_BODY, answerBody);
        context.startService(intent);
    }

    public static void votePositive(Context context, String answerId) {
        Intent intent = new Intent(context, AnswerService.class);
        intent.setAction(ACTION_POSITIVE_VOTE_ANSWER);
        intent.putExtra(EXTRA_PARAM_ANSWER_ID, answerId);
        context.startService(intent);
    }

    public static void voteNegative(Context context, String answerId) {
        Intent intent = new Intent(context, AnswerService.class);
        intent.setAction(ACTION_NEGATIVE_VOTE_ANSWER);
        intent.putExtra(EXTRA_PARAM_ANSWER_ID, answerId);
        context.startService(intent);
    }


    public static void markAsCorrect(Context context, String answerId){
        Intent intent = new Intent(context, AnswerService.class);
        intent.setAction(ACTION_MARK_AS_CORRECT_ANSWER);
        intent.putExtra(EXTRA_PARAM_ANSWER_ID, answerId);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_ADD_ANSWER.equals(action)) {
                final String questionId = intent.getStringExtra(EXTRA_PARAM_QUESTION_ID);
                final String answerBody = intent.getStringExtra(EXTRA_PARAM_ANSWER_BODY);
                addAnswer(questionId, answerBody);

            } else if (ACTION_FETCH_MY_ANSWERS.equals(action)) {
                getMyAnswers();

            } else if (ACTION_FETCH_ANSWER.equals(action)) {
                final String answerId = intent.getStringExtra(EXTRA_PARAM_ANSWER_ID);
                getAnswer(answerId);

            } else if (ACTION_UPDATE_ANSWER.equals(action)) {
                final String answerId = intent.getStringExtra(EXTRA_PARAM_ANSWER_ID);
                final String answerBody = intent.getStringExtra(EXTRA_PARAM_ANSWER_BODY);
                updateAnswer(answerId, answerBody);

            } else if (ACTION_DELETE_ANSWERS.equals((action))){
                String[] answersIds = intent.getStringArrayExtra(EXTRA_PARAM_ANSWERS_IDS);
                deleteAnswers(answersIds);

            }  else if (ACTION_NEGATIVE_VOTE_ANSWER.equals((action))){
                final String answerId = intent.getStringExtra(EXTRA_PARAM_ANSWER_ID);
                voteNegative(answerId);

            }  else if (ACTION_POSITIVE_VOTE_ANSWER.equals((action))){
                final String answerId = intent.getStringExtra(EXTRA_PARAM_ANSWER_ID);
                votePositive(answerId);

            }  else if (ACTION_MARK_AS_CORRECT_ANSWER.equals((action))){
                final String answerId = intent.getStringExtra(EXTRA_PARAM_ANSWER_ID);
                markAsCorrect(answerId);
            }
        }
    }


    private void addAnswer (String answerId, String answerBody){
        try {
            AnswerClient client = new AnswerClient(this);
            client.addAnswer(answerId, answerBody);
            sendBroadcast(new Intent(INTENT_FILTER));
        } catch (ClientException e) {
            sendErrorBroadcast(e);
        }
    }

    private void deleteAnswers(String[] answersIds) {
        try {
            AnswerClient client = new AnswerClient(this);
            for (String answerId : answersIds) {
                client.deleteAnswer(answerId);
            }
            sendBroadcast(new Intent((INTENT_FILTER)));
        } catch (ClientException e) {
            Log.e(TAG, null, e);
            sendErrorBroadcast(e);
        }
    }

    private void updateAnswer(String answerId, String body) {
        try {
            AnswerClient client = new AnswerClient(this);
            client.updateAnswer(answerId, body);
            sendBroadcast(new Intent(INTENT_FILTER));
        } catch (ClientException e) {
            Log.e(TAG, null, e);
            sendErrorBroadcast(e);
        }
    }


    private void getAnswer(String answerId) {
        try {
            AnswerClient client= new AnswerClient(this);
            Answer answer = client.getAnswer(answerId);
            Intent intent = new Intent(INTENT_FILTER);
            intent.putExtra(RESULT_ANSWER, answer);
            sendBroadcast(intent);
        } catch (ClientException e) {
            Log.e(TAG, null, e);
            sendErrorBroadcast(e);
        }
    }

    private void getMyAnswers() {
        try {
            final AnswerClient client = new AnswerClient(this);
            sendAnswersBroadcast(client.getMyAnswers());
        } catch (ClientException e) {
            Log.e(TAG, null, e);
            sendErrorBroadcast(e);
        }
    }

    private void markAsCorrect(String answerId) {
        try {
            AnswerClient client = new AnswerClient(this);
            client.markAnswerCorrect(answerId);
            sendBroadcast(new Intent(INTENT_FILTER));
        } catch (ClientException e) {
            Log.e(TAG, null, e);
            sendErrorBroadcast(e);
        }
    }

    private void votePositive(String answerId) {
        try {
            AnswerClient client= new AnswerClient(this);
            client.voteAnswerUp(answerId);
            Intent intent = new Intent(INTENT_FILTER);
            sendBroadcast(intent);
        } catch (ClientException e) {
            Log.e(TAG, null, e);
            sendErrorBroadcast(e);
        }
    }


    private void voteNegative(String answerId) {
        try {
            AnswerClient client= new AnswerClient(this);
            client.voteAnswerDown(answerId);
            Intent intent = new Intent(INTENT_FILTER);
            sendBroadcast(intent);
        } catch (ClientException e) {
            Log.e(TAG, null, e);
            sendErrorBroadcast(e);
        }
    }

    private void sendErrorBroadcast ( final Exception e){
        final Intent intent = new Intent(INTENT_FILTER);
        intent.putExtra(RESULT_ERROR, e);
        sendBroadcast(intent);
    }

    public void sendAnswersBroadcast(List<Answer> answers) {
        final Intent intent = new Intent(INTENT_FILTER);
        intent.putExtra(RESULT_ANSWERS, (ArrayList<Answer>) answers);
        sendBroadcast(intent);
    }
}