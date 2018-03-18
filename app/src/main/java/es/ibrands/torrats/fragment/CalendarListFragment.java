package es.ibrands.torrats.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.developers.coolprogressviews.DoubleArcProgress;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import es.ibrands.torrats.R;
import es.ibrands.torrats.activity.CalendarListActivity;
import es.ibrands.torrats.adapter.CalendarAdapter;
import es.ibrands.torrats.model.CalendarError;
import es.ibrands.torrats.model.CalendarEvent;
import es.ibrands.torrats.model.CalendarList;
import es.ibrands.torrats.util.DateDeserializer;
import es.ibrands.torrats.util.TorratsApiInterface;
import es.ibrands.torrats.util.SimpleIdlingResource;
import es.ibrands.torrats.util.Utility;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by pablomoreno on 17/03/18.
 */
public class CalendarListFragment extends Fragment
{
    private static final String TAG = CalendarListFragment.class.getSimpleName();
    private static final String API_URL = "http://www.torrats.com/wp-json/tribe/events/v1/";
    // http://torrats.com/wp-json/tribe/events/v1/events
    // http://torrats.com/wp-json/tribe/events/v1/events/616
    private static Retrofit retrofit = null;

    SimpleIdlingResource idlingResource;

    @BindView(R.id.calendar_recycler_view)
    RecyclerView calendarRecyclerView;

    @BindView(R.id.double_progress_arc)
    DoubleArcProgress doubleArcProgress;

    @BindView(R.id.empty_view)
    TextView emptyView;

    private TorratsApiInterface torratsApiInterface;
    private CalendarList mCalendarList;
    private CalendarError mCalendarError;
    private boolean mTwoPane;
    private CalendarAdapter calendarAdapter;

    public CalendarListFragment()
    {
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.calendar_list_fragment, container, false);
        ButterKnife.bind(this, view);

        torratsApiInterface = createRetrofit();
        idlingResource = (SimpleIdlingResource) ((CalendarListActivity) getActivity()).getIdlingResource();
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        doubleArcProgress.setVisibility(View.VISIBLE);
        if (Utility.isConnected(getActivity())) {
            Log.d(TAG, "getCalendarList");
            mCalendarList = getCalendarList();
        }

        return view;
    }

    public CalendarList getCalendarList()
    {
        torratsApiInterface.getList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<CalendarList>()
            {
                Disposable mDisposable;

                @Override
                public void onSubscribe(Disposable disposable)
                {
                    mDisposable = disposable;
                }

                @Override
                public void onNext(CalendarList calendarEventList)
                {
                    mCalendarList = calendarEventList;
                }

                @Override
                public void onError(Throwable e)
                {
                    String message = e.getMessage();
                    Log.d(TAG, "error: " + message);

                    if (e instanceof HttpException) {
                        Log.d(TAG, "httpException");
                        calendarRecyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);

                        return;
                        // We had non-2XX http error
                    } else if (e instanceof IOException) {
                        Log.d(TAG, "IOException");
                        // A network or conversion error happened
                    } else {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onComplete()
                {
                    Log.d(TAG, "start onComplete");
                    if (!mDisposable.isDisposed()) {
                        mDisposable.dispose();
                    }

                    calendarAdapter = new CalendarAdapter(getActivity(), mCalendarList.getEvents());
                    mTwoPane = CalendarListActivity.getPanelMode();
                    Log.d(TAG, "getPanelMode");
                    if (mTwoPane) {
                        Log.d(TAG, "mTwoPane");
                        // tablet mode
                        int noOfColumns = calculateGridNoOfColumns();

                        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                            getActivity(),
                            noOfColumns
                        );

                        calendarRecyclerView.setLayoutManager(gridLayoutManager);
                    } else {
                        Log.d(TAG, "mTwoPane else");
                        // mobile mode
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                            getActivity()
                        );

                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        calendarRecyclerView.setLayoutManager(linearLayoutManager);
                    }
                    Log.d(TAG, "setAdapter");
                    calendarRecyclerView.setAdapter(calendarAdapter);
                    idlingResource.setIdleState(true);

                    if (calendarAdapter.getItemCount() == 0) {
                        calendarRecyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    doubleArcProgress.setVisibility(View.GONE);
                }

                private int calculateGridNoOfColumns()
                {
                    Resources resources = getActivity().getResources();
                    DisplayMetrics displayMetrics = resources.getDisplayMetrics();
                    float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
                    int scalingFactor = 180;
                    return (int) (dpWidth / scalingFactor);
                }
            });

        return mCalendarList;
    }

    public static TorratsApiInterface createRetrofit()
    {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .create();
            GsonConverterFactory gsonConverteFactory = GsonConverterFactory.create(gson);

            retrofit = new Retrofit
                .Builder()
                .baseUrl(API_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(gsonConverteFactory)
                .build();
        }

        return retrofit.create(TorratsApiInterface.class);
    }
}
