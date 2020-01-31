package pt.uac.qa.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import pt.uac.qa.R;
import pt.uac.qa.model.Answer;
import pt.uac.qa.ui.adapter.ListViewAdapter;
import pt.uac.qa.ui.adapter.ViewHolder;

/**
 * Created by Patrício Cordeiro <patricio.cordeiro@gmail.com> on 20-01-2020.
 */
final class AnswerAdapter extends ListViewAdapter<Answer> {
    AnswerAdapter(Context context) {
        super(context);
    }

    @Override
    protected View inflateView() {
        return LayoutInflater.from(context).inflate(R.layout.answer_item_layout, null);
    }

    @Override
    protected ViewHolder<Answer> createViewHolder() {
        return new AnswerViewHolder();
    }

    @Override
    protected boolean acceptsItem(Answer item, CharSequence constraint) {
        return false;
    }

    private static final class AnswerViewHolder implements ViewHolder<Answer> {
        private TextView scoreView;
        private TextView bodyView;
        private TextView authorView;

        @Override
        public void init(View view) {
            scoreView = view.findViewById(R.id.scoreView);
            bodyView = view.findViewById(R.id.bodyView);
            authorView = view.findViewById(R.id.authorView);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void display(Answer item) {
            scoreView.setText("" + (item.getPositiveVotes() - item.getNegativeVotes()));
            bodyView.setText(item.getBody());
            authorView.setText(String.format(
                    "por %s %s",
                    item.getUser().getName(),
                    DateUtils.getRelativeTimeSpanString(
                            item.getDatePublished().getTime(),
                            System.currentTimeMillis(),
                            DateUtils.SECOND_IN_MILLIS)));
        }
    }
}
