package es.ibrands.torrats.activity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import es.ibrands.torrats.R;
import es.ibrands.torrats.util.SimpleIdlingResource;

/**
 * Created by pablomoreno on 16/03/18.
 */
public class CalendarListActivity extends AppCompatActivity
{
    private static boolean mTwoPane;

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource()
    {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }

        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_list_activity);
        getSupportActionBar().setTitle(R.string.calendar_list_title);

        mTwoPane = false;
        if (findViewById(R.id.calendar_list_tablet_container) != null) {
            mTwoPane = true;
        }

        getIdlingResource();
    }

    public static boolean getPanelMode()
    {
        return mTwoPane;
    }
}
