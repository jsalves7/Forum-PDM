package pt.uac.qa.ui.adapter;

import android.view.View;

/**
 * Created by Patrício Cordeiro <patricio.cordeiro@gmail.com> on 10-01-2020.
 */
public interface ViewHolder<T> {
    void init(View view);
    void display(T item);
}
