package es.ibrands.torrats.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by pablomoreno on 22/3/18.
 */
public class SplashActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Start home activity

        startActivity(new Intent(SplashActivity.this, CalendarListActivity.class));

        // close splash activity
        finish();
    }
}
