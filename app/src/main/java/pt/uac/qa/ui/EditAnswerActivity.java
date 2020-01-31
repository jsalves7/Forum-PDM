package pt.uac.qa.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import pt.uac.qa.R;
import pt.uac.qa.client.AnswerClient;
import pt.uac.qa.model.Answer;
import pt.uac.qa.services.AnswerService;

public class EditAnswerActivity extends AppCompatActivity {
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(AnswerService.RESULT_ERROR)) {
                Exception error = (Exception) intent.getSerializableExtra(AnswerService.RESULT_ERROR);
                Toast.makeText(EditAnswerActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            } else if (intent.hasExtra(AnswerService.RESULT_ANSWER)) {
                Answer answer = (Answer) intent.getSerializableExtra(AnswerService.RESULT_ANSWER);
                answerBody.setText(answer.getBody());
            } else {
                EditAnswerActivity.this.setResult(Activity.RESULT_OK);
                finish();
            }
        }
    };

    private EditText answerBody;
    private String answerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_answer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        registerReceiver(receiver, new IntentFilter(AnswerService.INTENT_FILTER));

        answerBody = findViewById(R.id.answerBody);
        loadAnswer();

        if (answerId != null){
            this.setTitle("Actualize Resposta");
        } else {
            this.setTitle("Adicione Resposta");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.answer_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_accept) {
            submitAnswer();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void submitAnswer() {
        if (TextUtils.isEmpty(answerBody.getText())) {
            answerBody.setError("Escreva a sua resposta");
            return;
        }

        if (answerId != null) {
            final String body = answerBody.getText().toString();
            AnswerService.updateAnswer(EditAnswerActivity.this, answerId, body);

        } else {
            final String body = answerBody.getText().toString();
            Intent intent = getIntent();
            String questionId = intent.getStringExtra("question_id");
            AnswerService.addAnswer(EditAnswerActivity.this, questionId, body);
        }
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void loadAnswer() {
        Intent intent = getIntent();

        if (intent.hasExtra("answer_id")) {
            answerId = intent.getStringExtra("answer_id");
            AnswerService.fetchAnswer(this, answerId);
        }
    }
}
