package es.ibrands.torrats.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import es.ibrands.torrats.R;
import es.ibrands.torrats.activity.CalendarDetailActivity;
import es.ibrands.torrats.model.CalendarEvent;
import es.ibrands.torrats.util.DateDeserializer;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by pablomoreno on 16/03/18.
 */
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.RecipeViewHolder>
{
    private static final String TAG = CalendarAdapter.class.getSimpleName();
    public static final String CALENDAR_LIST = "calendarEventList";
    public static final String DESCRIPTION = "description";

    private Context context;
    private List<CalendarEvent> calendarEventList;

    private SharedPreferences sharedPreferences;

    public CalendarAdapter(Context context, List<CalendarEvent> calendarEventList)
    {
        this.context = context;
        this.calendarEventList = calendarEventList;

        sharedPreferences = context.getSharedPreferences(
            "preferences",
            Context.MODE_PRIVATE
        );
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        @BindView(R.id.calendar_title_list_text_view)
        TextView calendarTitleText;

        @BindView(R.id.start_at_text_view)
        TextView startAtText;

        @BindView(R.id.card_view)
        CardView cardView;

        @BindView(R.id.calendar_thumbnail_image_view)
        AppCompatImageView thumbView;

        public RecipeViewHolder(View view)
        {
            super(view);

            ButterKnife.bind(this, view);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .create();

            int position = getAdapterPosition();

            String jsonCalendar = gson.toJson(calendarEventList.get(position));
            sharedPreferences.edit().putString(CALENDAR_LIST, jsonCalendar).apply();

            String jsonDescription = gson.toJson(calendarEventList.get(position).getDescription());

            Intent intent = new Intent(context, CalendarDetailActivity.class);
            intent.putExtra(DESCRIPTION, jsonDescription);
            context.startActivity(intent);
        }
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(context).inflate(
            R.layout.calendar_item_list,
            viewGroup,
            false
        );

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder recipeViewHolder, final int position)
    {
        Log.d(TAG, "onBindViewHolder");
        recipeViewHolder.calendarTitleText.setText(calendarEventList.get(position).getTitle());

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        recipeViewHolder.startAtText.setText(dateFormat.format(calendarEventList.get(position).getStartAt()));

        String imageUrl = calendarEventList.get(position).getImage();

        // throws an error if empty path
        if (!imageUrl.equals("")) {
            Picasso.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .into(recipeViewHolder.thumbView);
        }
    }

    @Override
    public int getItemCount()
    {
        if (calendarEventList == null) {
            return 0;
        }

        return calendarEventList.size();
    }
}
