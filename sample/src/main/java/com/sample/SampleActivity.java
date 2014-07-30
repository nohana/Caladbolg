package com.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.caladbolg.Caladbolg;
import com.caladbolg.Caladbolg.OnCancelPickColorListener;
import com.caladbolg.Caladbolg.OnPickedColorListener;


public class SampleActivity extends ActionBarActivity implements OnPickedColorListener, OnCancelPickColorListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        if (getSupportFragmentManager().findFragmentByTag("caladbolg") == null) {
            Caladbolg caladbolg = Caladbolg.getInstance(Color.BLACK);
            caladbolg.show(getSupportFragmentManager(), "caladbolg");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sample, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCancelPickColor() {
        Toast.makeText(this, "cancel", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPickedColor(int rgb, int alpha) {
        Toast.makeText(this, String.format("RGB:%d Alpha:%d", rgb, alpha), Toast.LENGTH_LONG).show();
    }
}
