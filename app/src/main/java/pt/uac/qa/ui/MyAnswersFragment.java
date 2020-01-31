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

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import pt.uac.qa.R;
import pt.uac.qa.model.Answer;
import pt.uac.qa.services.AnswerService;

public class MyAnswersFragment extends BaseFragment {
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(AnswerService.RESULT_ERROR)) {
                Exception error = (Exception) intent.getSerializableExtra(AnswerService.RESULT_ERROR);
                Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            } else if (intent.hasExtra(AnswerService.RESULT_ANSWERS)) {
                final List<Answer> answers = (List<Answer>) intent.getSerializableExtra(AnswerService.RESULT_ANSWERS);
                adapterAnswer.loadItems(answers);
            } else {
                loadMyAnswers();
            }

            progressBar.setVisibility(View.GONE);
            answerList.setVisibility(View.VISIBLE);
        }
    };

    private AnswerAdapter adapterAnswer;
    private ListView answerList;
    private ProgressBar progressBar;

    private List<String> selectedAnswers = new ArrayList<>();


    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_layout, container, false);
        answerList = root.findViewById(R.id.listView);
        progressBar = root.findViewById(R.id.progressBar);
        answerList.setAdapter(adapterAnswer = new AnswerAdapter(getActivity()));

        answerList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                answerList.setItemChecked(position, true);
                return false;
            }
        });

        answerList.setMultiChoiceModeListener(this);

        answerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Answer answer = (Answer) adapterAnswer.getItem(position);
                Intent intent = new Intent(getActivity(), EditAnswerActivity.class);
                intent.putExtra("answer_id", answer.getAnswerId());
                intent.putExtra("answer_body", answer.getBody());
                startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, new IntentFilter(AnswerService.INTENT_FILTER));
        loadMyAnswers();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        Answer answer = (Answer) adapterAnswer.getItem(position);

        if (checked) {
            selectedAnswers.add(answer.getAnswerId());
        } else {
            selectedAnswers.remove(answer.getAnswerId());
        }

        mode.setTitle(String.format("%s selected", selectedAnswers.size()));
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        mode.finish();
        deleteSelectedAnswers();
        return true;
    }

    @Override
    protected void refresh() {
        loadMyAnswers();
        Toast.makeText(getActivity(), "Refresh my answers", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void search(CharSequence constraint) {
        adapterAnswer.getFilter().filter(constraint);
    }

    private void deleteSelectedAnswers() {
        if (selectedAnswers.isEmpty())
            return;

        AnswerService.deleteAnswers(getActivity(),
                selectedAnswers.toArray(new String[0]));
    }

    private void loadMyAnswers() {
        answerList.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        AnswerService.fetchMyAnswers(getActivity());
    }
}