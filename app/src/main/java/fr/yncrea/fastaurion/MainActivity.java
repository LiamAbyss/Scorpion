package fr.yncrea.fastaurion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fr.yncrea.fastaurion.utils.Constants;
import fr.yncrea.fastaurion.utils.PreferenceUtils;

public class MainActivity extends AppCompatActivity {
    private Executor executor = Executors.newSingleThreadExecutor();
    private String username = "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Intent intent = getIntent();
        if (null != intent) {
            final Bundle extras = intent.getExtras();
            if ((null != extras) && (extras.containsKey(Constants.Login.EXTRA_LOGIN))) {
                final String login = extras.getString((Constants.Login.EXTRA_LOGIN));
                getSupportActionBar().setSubtitle(login);
                TextView textView = findViewById(R.id.helloTextView);
                textView.setText(login);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fastaurion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if( id == R.id.actionLogout)
        {
            PreferenceUtils.setLogin(null);
            finish();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }
}