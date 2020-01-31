package pt.uac.qa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import pt.uac.qa.model.User;
import pt.uac.qa.ui.EditQuestionActivity;

public class MainActivity extends AppCompatActivity {
    public static final String INTENT_FILTER = "pt.uac.qa.MAIN_INTENT_FILTER";
    public static final String EXTRA_PARAM_CONSTRAINT = "pt.uac.qa.PARAM_CONSTRAINT";

    private AppBarConfiguration appBarConfiguration;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setupToolbar();
        setupActionButton();
        setupNavigation();

        displayUserInformation();
    }

    private void displayUserInformation() {
        final NavigationView navigationView = findViewById(R.id.nav_view);
        final TextView usernameView = navigationView.getHeaderView(0).findViewById(R.id.usernameView);
        final TextView emailView = navigationView.getHeaderView(0).findViewById(R.id.emailView);
        final QAApp app = (QAApp) getApplication();
        final User user = app.getUser();

        usernameView.setText(user.getName());
        emailView.setText(user.getEmail());
    }

    private void setupNavigation() {
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_questions, R.id.nav_my_questions, R.id.nav_my_answers)
                .setDrawerLayout(drawer)
                .build();

        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                final int id = destination.getId();

                if (id != R.id.nav_questions) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            sendBroadcast(new Intent(INTENT_FILTER));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupActionButton() {
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditQuestionActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void setupToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupSearch(final Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setQueryHint("Pesquisa...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final Intent intent = new Intent(INTENT_FILTER);
                intent.putExtra(EXTRA_PARAM_CONSTRAINT, newText);
                sendBroadcast(intent);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            sendBroadcast(new Intent(INTENT_FILTER));
            return true;
        } else if (item.getItemId() == R.id.action_logout) {
            QAApp app = (QAApp) getApplication();
            app.logout();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        setupSearch(menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}
