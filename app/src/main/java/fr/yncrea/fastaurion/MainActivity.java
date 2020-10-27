package fr.yncrea.fastaurion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fr.yncrea.fastaurion.api.AurionService;
import fr.yncrea.fastaurion.utils.Constants;
import fr.yncrea.fastaurion.utils.PreferenceUtils;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private Executor executor = Executors.newSingleThreadExecutor();
    private AurionService aurionService;
    private String username = "";
    private String password = "";
    private String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Intent intent = getIntent();
        if (null != intent) {
            final Bundle extras = intent.getExtras();
            if ((null != extras) && (extras.containsKey(Constants.Login.EXTRA_LOGIN))) {

                OkHttpClient client = new OkHttpClient.Builder()
                        .followRedirects(false)
                        .followSslRedirects(false)
                        .build();

                Retrofit retrofit = new Retrofit.Builder()
                        .client(client)
                        .baseUrl("https://aurion.yncrea.fr")
                        .build();
                this.aurionService = retrofit.create(AurionService.class);

                final String login = extras.getString((Constants.Login.EXTRA_LOGIN));
                TextView textView = findViewById(R.id.helloTextView);
                textView.setText(login);
                getSupportActionBar().setSubtitle(extras.getString(Constants.Preferences.PREF_NAME));
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