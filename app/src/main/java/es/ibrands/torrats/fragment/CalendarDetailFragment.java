package es.ibrands.torrats.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import es.ibrands.torrats.R;
import es.ibrands.torrats.activity.VideoPlayerActivity;
import es.ibrands.torrats.adapter.CalendarAdapter;
import es.ibrands.torrats.adapter.VideoAdapter;
import es.ibrands.torrats.model.Ingredient;
import es.ibrands.torrats.model.Step;
import es.ibrands.torrats.util.OnClickInterface;

import java.util.List;

/**
 * Created by pablomoreno on 30/01/18.
 */
public class CalendarDetailFragment extends Fragment implements OnClickInterface
{
    public static final String CALENDAR_DATA = "ingredients_data";
    public static final String DESCRIPTION = "description";
    public static final String URL = "url";
    public static final String ID = "id";
    public static final String PANE = "pane";
    public static final String THUMBNAIL_URL = "thumbnailurl";
    public static final String STEPS_DATA = "stepsData";
    private static final String RECYCLER_VIEW_STATE = "recyclerViewState";

    private static final String TAG = CalendarDetailFragment.class.getSimpleName();
    @BindView(R.id.description_text_view)
    TextView descriptionText;

    @BindView(R.id.step_recycler_view)
    RecyclerView stepRecyclerView;

    private LinearLayoutManager linearLayoutManager;
    private String description;
    private List<Step> stepList;
    //private List<Ingredient> ingredientList;
    private VideoAdapter videoAdapter;
    private boolean twoPane;
    private Parcelable mListState;

    public CalendarDetailFragment()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();

        Gson gson = new Gson();

        String jsonDescription = bundle.getString(CALENDAR_DATA);
        description = gson.fromJson(
            jsonDescription,
            new TypeToken<String>() {
                // nothing to do
            }.getType()
        );
/*
        Gson gson = new Gson();

        String jsonIngredientList = bundle.getString(CALENDAR_DATA);
        ingredientList = gson.fromJson(
            jsonIngredientList,
            new TypeToken<List<Ingredient>>() {
                // nothing to do
            }.getType()
        );

        String jsonStepList = bundle.getString(STEPS_DATA);
        stepList = gson.fromJson(
            jsonStepList,
            new TypeToken<List<Step>>() {
                // nothing to do
            }.getType()
        );
*/
        twoPane = bundle.getBoolean(PANE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().finish();
        }

        return true;
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup viewGroup,
        Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.calendar_detail_fragment, viewGroup, false);
        ButterKnife.bind(this, view);

/*
        StringBuffer stringBuffer = new StringBuffer();

        for (Ingredient ingredient : ingredientList) {
            String text = ingredient.getQuantity() + " " + ingredient.getMeasure()
                + " " + ingredient.getIngredient();
            stringBuffer.append("\u2022 " + text + " \n");
        }

        ingredientsText.setText(stringBuffer.toString());
*/
        descriptionText.setText(description);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        stepRecyclerView.setLayoutManager(linearLayoutManager);

        if (savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE);
        }

        videoAdapter = new VideoAdapter(getActivity(), stepList);
        videoAdapter.setOnClick(this);
        stepRecyclerView.setAdapter(videoAdapter);

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (mListState != null) {
            linearLayoutManager.onRestoreInstanceState(mListState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RECYCLER_VIEW_STATE, linearLayoutManager.onSaveInstanceState());
    }

    @Override
    public void onClick(
        Context context,
        Integer id,
        String description,
        String url,
        String thumbnailUrl
    ) {
        if (twoPane) {
            Bundle bundle = new Bundle();
            bundle.putInt(ID, id);
            bundle.putString(DESCRIPTION, description);
            bundle.putString(URL, url);
            bundle.putBoolean(PANE, twoPane);
            bundle.putString(THUMBNAIL_URL, thumbnailUrl);

            VideoFragment videoFragment = new VideoFragment();
            videoFragment.setArguments(bundle);

            getActivity().getSupportFragmentManager().beginTransaction().replace(
                R.id.video_player_tablet,
                videoFragment
            ).commit();
        } else {
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra(ID, id);
            intent.putExtra(DESCRIPTION, description);
            intent.putExtra(URL, url);
            intent.putExtra(THUMBNAIL_URL, thumbnailUrl);
            context.startActivity(intent);
        }
    }
}
