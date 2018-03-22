package es.ibrands.torrats.activity;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import es.ibrands.torrats.R;
import es.ibrands.torrats.data.ArticleLoader;
import es.ibrands.torrats.data.ItemsContract;
import es.ibrands.torrats.data.UpdaterService;
import es.ibrands.torrats.ui.DynamicHeightNetworkImageView;
import es.ibrands.torrats.ui.ImageLoaderHelper;
import es.ibrands.torrats.util.Utility;

/**
 * An activity representing a list of photos. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of photos, which when
 * touched, lead to a {@link PhotoGalleryDetailActivity} representing bigger photo. On tablets, the
 * activity presents a grid of photos as cards.
 */
public class PhotoGalleryListActivity extends BaseActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = PhotoGalleryListActivity.class.toString();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_gallery_list_activity);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        getLoaderManager().initLoader(0, null, this);

        if (savedInstanceState == null) {
            refresh();
        }

        Log.d(TAG, "before is Connected");
        if (!Utility.isConnected(this)) {
            Log.d(TAG, "before show snackbar");
            Snackbar.make(
                findViewById(R.id.photo_gallery_list_activity),
                getString(R.string.no_internet_connection),
                Snackbar.LENGTH_SHORT
            ).show();
            Log.d(TAG, "snackbar shown");
        }
    }

    private void refresh()
    {
        startService(new Intent(this, UpdaterService.class));
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
            new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
    }

    private boolean mIsRefreshing = false;

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                updateRefreshingUI();
            }
        }
    };

    private void updateRefreshingUI()
    {
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        Adapter adapter = new Adapter(cursor);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);

        int noOfColumns = calculateGridNoOfColumns();
        StaggeredGridLayoutManager sglm =
            new StaggeredGridLayoutManager(noOfColumns, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);
    }

    private int calculateGridNoOfColumns()
    {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        return (int) (dpWidth / scalingFactor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        mRecyclerView.setAdapter(null);
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private Cursor mCursor;

        public Adapter(Cursor cursor)
        {
            mCursor = cursor;
        }

        @Override
        public long getItemId(int position)
        {
            mCursor.moveToPosition(position);
            return mCursor.getLong(ArticleLoader.Query._ID);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = getLayoutInflater().inflate(R.layout.photo_gallery_list_item, parent, false);
            final ViewHolder vh = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))
                    ));
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position)
        {
            mCursor.moveToPosition(position);

            holder.thumbnailView.setImageUrl(
                mCursor.getString(ArticleLoader.Query.THUMB_URL),
                ImageLoaderHelper.getInstance(PhotoGalleryListActivity.this).getImageLoader()
            );

            holder.thumbnailView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));
        }

        @Override
        public int getItemCount()
        {
            return mCursor.getCount();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public DynamicHeightNetworkImageView thumbnailView;

        public ViewHolder(View view)
        {
            super(view);
            thumbnailView = (DynamicHeightNetworkImageView) view.findViewById(R.id.thumbnail);
        }
    }
}
