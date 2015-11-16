package com.refect.facebookforwear;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.refect.shared.utils.Utils;

public class LauncherActivity extends AppCompatActivity {

    private Button btnSetAppId;
    private EditText editAppId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        btnSetAppId = (Button) findViewById(R.id.btn_set_app_id);
        editAppId = (EditText) findViewById(R.id.edit_app_id);

        btnSetAppId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editAppId.length() > 0) {
                    Utils.storeSetting("facebook_app_id", editAppId.getText().toString().trim(), getApplicationContext());
                    Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Enter a valid App Id", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(Utils.getSetting("facebook_app_id", null, getApplicationContext()) != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launcher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
