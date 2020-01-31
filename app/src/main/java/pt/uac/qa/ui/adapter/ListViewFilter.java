package pt.uac.qa.ui.adapter;

import android.text.TextUtils;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patrício Cordeiro <patricio.cordeiro@gmail.com> on 10-01-2020.
 */
final class ListViewFilter<T> extends Filter {
    private final ListViewAdapter<T> adapter;

    public ListViewFilter(ListViewAdapter<T> adapter) {
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        if (TextUtils.isEmpty(constraint)) {
            return results(adapter.sourceItems);
        }

        List<T> filtered = new ArrayList<>();

        for (T item : adapter.sourceItems) {
            if (adapter.acceptsItem(item, constraint)) {
                filtered.add(item);
            }
        }

        return results(filtered);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.displayItems = (List<T>) results.values;
        adapter.notifyDataSetChanged();
    }


    private FilterResults results(final List<T> items) {
        FilterResults results = new FilterResults();
        results.values = items;
        results.count = items.size();
        return results;
    }
}
