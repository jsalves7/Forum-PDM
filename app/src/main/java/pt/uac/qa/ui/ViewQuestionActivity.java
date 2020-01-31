package pt.uac.qa.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import pt.uac.qa.QAApp;
import pt.uac.qa.R;
import pt.uac.qa.StringUtils;
import pt.uac.qa.model.Answer;
import pt.uac.qa.model.Question;
import pt.uac.qa.services.QuestionService;

public class ViewQuestionActivity extends AppCompatActivity {
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(QuestionService.RESULT_ERROR)) {
                Exception error = (Exception) intent.getSerializableExtra(QuestionService.RESULT_ERROR);
                Toast.makeText(ViewQuestionActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            } else if (intent.hasExtra(QuestionService.RESULT_QUESTION)) {
                Question question = (Question) intent.getSerializableExtra(QuestionService.RESULT_QUESTION);

                titleView.setText(question.getTitle());
                bodyView.setText(question.getBody());

                if (question.getTags() != null && !question.getTags().isEmpty()) {
                    tagsView.setText(StringUtils.join(", ", question.getTags()));
                } else {
                    tagsView.setVisibility(View.GONE);
                }

                authorView.setText(String.format(
                        "por %s %s",
                        question.getUser().getName(),
                        DateUtils.getRelativeTimeSpanString(
                                question.getDatePublished().getTime(),
                                System.currentTimeMillis(),
                                DateUtils.SECOND_IN_MILLIS)));

                if (question.getAnswers() != null && !question.getAnswers().isEmpty()) {
                    adapter.loadItems(question.getAnswers());
                }

                getSupportActionBar().setTitle(question.getTitle());
            }
        }
    };

    private TextView titleView;
    private TextView bodyView;
    private TextView tagsView;
    private TextView authorView;
    private LinearLayout container;
    private AnswerAdapter adapter;
    private boolean collapsed = false;
    private String questionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(receiver, new IntentFilter(QuestionService.INTENT_FILTER));
        setContentView(R.layout.activity_view_question);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupViews();
        loadQuestion();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            loadQuestion();
        }
    }

    private void setupViews() {
        ListView answerListView = findViewById(R.id.answerListView);

        titleView = findViewById(R.id.titleView);
        bodyView = findViewById(R.id.bodyView);
        tagsView = findViewById(R.id.tagsView);
        authorView = findViewById(R.id.authorView);
        container = findViewById(R.id.questionContainer);

        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable;
                collapsed = !collapsed;

                if (collapsed) {
                    drawable = ContextCompat.getDrawable(ViewQuestionActivity.this, R.drawable.ic_keyboard_arrow_down_grey_700_18dp);
                    container.setVisibility(View.GONE);
                } else {
                    drawable = ContextCompat.getDrawable(ViewQuestionActivity.this, R.drawable.ic_keyboard_arrow_up_grey_700_18dp);
                    container.setVisibility(View.VISIBLE);
                }

                titleView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }
        });

        answerListView.setAdapter(adapter = new AnswerAdapter(this));
        answerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QAApp app = (QAApp) getApplication();
                Answer answer = (Answer) adapter.getItem(position);
                boolean isMyQuestion = app.getUser().getUserId().equals(answer.getUser().getUserId());
                Intent intent = new Intent(ViewQuestionActivity.this, ViewAnswerActivity.class);
                intent.putExtra("answer_id", answer.getAnswerId());
                intent.putExtra("question_id", answer.getQuestionId());
                intent.putExtra("answer_body", answer.getBody());
                intent.putExtra("is_my_question", isMyQuestion);
                startActivityForResult(intent, 2);
            }
        });

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewQuestionActivity.this, EditAnswerActivity.class);
                intent.putExtra("question_id", questionId);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void loadQuestion() {
        Intent intent = getIntent();

        if (intent.hasExtra("question_id")) {
            questionId = intent.getStringExtra("question_id");
            QuestionService.fetchQuestion(this, questionId);
        } else {
            Toast.makeText(this, "Need question ID.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_question_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            loadQuestion();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
