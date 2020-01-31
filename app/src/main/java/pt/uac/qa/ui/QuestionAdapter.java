package pt.uac.qa.ui;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import pt.uac.qa.R;
import pt.uac.qa.model.Question;
import pt.uac.qa.ui.adapter.ListViewAdapter;
import pt.uac.qa.ui.adapter.ViewHolder;

/**
 * Adapter
 */
final class QuestionAdapter extends ListViewAdapter<Question> {
    QuestionAdapter(Context context) {
        super(context);
    }

    @Override
    protected View inflateView() {
        return LayoutInflater.from(context).inflate(R.layout.question_item_layout, null);
    }

    @Override
    protected ViewHolder<Question> createViewHolder() {
        return new QuestionViewHolder();
    }

    @Override
    protected boolean acceptsItem(Question item, CharSequence constraint) {
        return item.getTitle().contains(constraint);
    }

    private static class QuestionViewHolder implements ViewHolder<Question> {
        private TextView title;
        private TextView author;

        @Override
        public void init(View view) {
            title = view.findViewById(R.id.questionTitle);
            author = view.findViewById(R.id.questionAuthor);
        }

        @Override
        public void display(Question item) {
            title.setText(item.getTitle());
            author.setText(String.format(
                    "por %s %s",
                    item.getUser().getName(),
                    DateUtils.getRelativeTimeSpanString(item.getDatePublished().getTime(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS)));
        }
    }
}
