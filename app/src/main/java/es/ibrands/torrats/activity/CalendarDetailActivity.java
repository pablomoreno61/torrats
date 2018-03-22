package es.ibrands.torrats.activity;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import es.ibrands.torrats.adapter.CalendarAdapter;
import es.ibrands.torrats.fragment.CalendarDetailFragment;
import es.ibrands.torrats.R;
import es.ibrands.torrats.fragment.VideoFragment;
import es.ibrands.torrats.model.CalendarEvent;
import es.ibrands.torrats.widget.CalendarAppWidgetProvider;

/**
 * Created by pablomoreno on 16/03/18.
 */
public class CalendarDetailActivity extends BaseActivity
{
    private static final String TAG = CalendarDetailActivity.class.getSimpleName();
    private static final String CALENDAR_DETAIL_ROTATION = "calendarDetailRotation";
    private boolean mCalendarDetailRotation;
    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_detail_activity);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.calendar_detail_title);
        // Enable the Up button
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            mCalendarDetailRotation = savedInstanceState.getBoolean(CALENDAR_DETAIL_ROTATION);
        }

        if (findViewById(R.id.video_player_tablet) != null) {
            twoPane = true;

            getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.video_player_tablet, new VideoFragment()).commit();
        }

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();

            String titleJson = getIntent().getStringExtra(CalendarAdapter.TITLE);
            bundle.putString(CalendarDetailFragment.CALENDAR_TITLE, titleJson);

            String descriptionJson = getIntent().getStringExtra(CalendarAdapter.DESCRIPTION);
            bundle.putString(CalendarDetailFragment.CALENDAR_DESCRIPTION, descriptionJson);

            String imageJson = getIntent().getStringExtra(CalendarAdapter.IMAGE);
            bundle.putString(CalendarDetailFragment.CALENDAR_IMAGE, imageJson);

            String startAtJson = getIntent().getStringExtra(CalendarAdapter.START_AT);
            bundle.putString(CalendarDetailFragment.CALENDAR_START_AT, startAtJson);

            bundle.putBoolean(CalendarDetailFragment.PANE, twoPane);

            CalendarDetailFragment calendarDetailFragment = new CalendarDetailFragment();
            calendarDetailFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(
                R.id.calendar_detail_fragment,
                calendarDetailFragment
            ).commit();

            mCalendarDetailRotation = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.add_widget_action) {
            SharedPreferences sharedPreferences = getSharedPreferences(
                "preferences",
                Context.MODE_PRIVATE
            );

            Gson gson = new Gson();
            CalendarEvent calendarEvent = gson.fromJson(
                sharedPreferences.getString(CalendarAdapter.CALENDAR_LIST, null),
                CalendarEvent.class
            );

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            Bundle bundle = new Bundle();
            int appWidgetId = bundle.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            );

            CalendarAppWidgetProvider.updateAppWidget(
                this,
                appWidgetManager,
                appWidgetId,
                calendarEvent.getTitle(),
                calendarEvent.getStartAt()
            );

            Toast.makeText(
                this,
                calendarEvent.getTitle() + " " + getString(R.string.added_to_widget_message),
                Toast.LENGTH_SHORT
            ).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.getBoolean(CALENDAR_DETAIL_ROTATION, mCalendarDetailRotation);
    }
}
