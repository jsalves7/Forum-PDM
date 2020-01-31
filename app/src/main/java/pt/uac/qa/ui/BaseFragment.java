package pt.uac.qa.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;

import androidx.fragment.app.Fragment;
import pt.uac.qa.MainActivity;
import pt.uac.qa.R;

/**
 * Created by Patrício Cordeiro <patricio.cordeiro@gmail.com> on 16-01-2020.
 */
public abstract class BaseFragment extends Fragment implements AbsListView.MultiChoiceModeListener{
    private final BroadcastReceiver mainReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(MainActivity.EXTRA_PARAM_CONSTRAINT)) {
                search(intent.getStringExtra(MainActivity.EXTRA_PARAM_CONSTRAINT));
            } else {
                refresh();
            }
        }
    };

    protected abstract void refresh();
    protected abstract void search(CharSequence constraint);

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mainReceiver, new IntentFilter(MainActivity.INTENT_FILTER));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mainReceiver);
        super.onPause();
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {}

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        getParentActivity().getSupportActionBar().hide();
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        getParentActivity().getSupportActionBar().show();
    }

    protected MainActivity getParentActivity() {
        return (MainActivity) getActivity();
    }
}
