package es.ibrands.torrats.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.google.gson.Gson;

import es.ibrands.torrats.R;
import es.ibrands.torrats.adapter.CalendarAdapter;
import es.ibrands.torrats.model.CalendarEvent;

import java.util.Date;

/**
 * Created by pablomoreno on 16/03/18.
 */
public class CalendarAppWidgetProvider extends AppWidgetProvider
{
    SharedPreferences sharedPreferences;

    public static void updateAppWidget(
        Context context,
        AppWidgetManager appWidgetManager,
        int appWidgetId,
        String eventTitle,
        Date startAt
    ) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_app);
        remoteViews.setTextViewText(R.id.calendar_title_text_view_widget, eventTitle);
        remoteViews.setTextViewText(R.id.calendar_start_at_text_view_widget, startAt.toString());

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        sharedPreferences = context.getSharedPreferences(
            "preferences",
            Context.MODE_PRIVATE
        );

        String jsonRecipe = sharedPreferences.getString(CalendarAdapter.CALENDAR_LIST, null);
        Gson gson = new Gson();
        CalendarEvent calendarEvent = gson.fromJson(jsonRecipe, CalendarEvent.class);

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(
                context,
                appWidgetManager,
                appWidgetId,
                calendarEvent.getTitle(),
                calendarEvent.getStartAt()
            );
        }
    }

    @Override
    public void onEnabled(Context context)
    {
        // not implemented
    }

    @Override
    public void onDisabled(Context context)
    {
        // not implemented
    }
}

