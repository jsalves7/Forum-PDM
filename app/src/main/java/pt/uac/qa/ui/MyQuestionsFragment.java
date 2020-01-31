package pt.uac.qa.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import pt.uac.qa.R;
import pt.uac.qa.model.Question;
import pt.uac.qa.services.QuestionService;

public class MyQuestionsFragment extends BaseFragment {
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressWarnings("unchecked")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(QuestionService.RESULT_ERROR)) {
                Exception error = (Exception) intent.getSerializableExtra(QuestionService.RESULT_ERROR);
                Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            } else if (intent.hasExtra(QuestionService.RESULT_QUESTIONS)) {
                final List<Question> questions = (List<Question>) intent.getSerializableExtra(QuestionService.RESULT_QUESTIONS);
                adapter.loadItems(questions);
            } else {
                loadMyQuestions();
            }

            progressBar.setVisibility(View.GONE);
            questionList.setVisibility(View.VISIBLE);
        }
    };

    private QuestionAdapter adapter;
    private ListView questionList;
    private ProgressBar progressBar;

    private List<String> selectedQuestions = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_layout, container, false);
        questionList = root.findViewById(R.id.listView);
        progressBar = root.findViewById(R.id.progressBar);
        questionList.setAdapter(adapter = new QuestionAdapter(getActivity()));

        questionList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                questionList.setItemChecked(position, true);
                return false;
            }
        });

        questionList.setMultiChoiceModeListener(this);

        questionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Question question = (Question) adapter.getItem(position);
                Intent intent = new Intent(getActivity(), EditQuestionActivity.class);
                intent.putExtra("question_id", question.getQuestionId());
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, new IntentFilter(QuestionService.INTENT_FILTER));
        loadMyQuestions();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        Question question = (Question) adapter.getItem(position);

        if (checked) {
            selectedQuestions.add(question.getQuestionId());
        } else {
            selectedQuestions.remove(question.getQuestionId());
        }

        mode.setTitle(String.format("%s selected", selectedQuestions.size()));
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        mode.finish();
        deleteSelectedQuestions();
        return true;
    }

    @Override
    protected void refresh() {
        loadMyQuestions();
    }

    @Override
    protected void search(CharSequence constraint) {
        adapter.getFilter().filter(constraint);
    }

    private void deleteSelectedQuestions() {
        if (selectedQuestions.isEmpty())
            return;

        QuestionService.deleteQuestions(getActivity(),
                selectedQuestions.toArray(new String[0]));
    }

    private void loadMyQuestions() {
        questionList.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        QuestionService.fetchMyQuestions(getActivity());
    }
}