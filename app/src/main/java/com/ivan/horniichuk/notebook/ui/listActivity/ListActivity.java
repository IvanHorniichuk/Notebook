package com.ivan.horniichuk.notebook.ui.listActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.ivan.horniichuk.notebook.R;
import com.ivan.horniichuk.notebook.data.NotebookDbContract;
import com.ivan.horniichuk.notebook.ui.editorActivity.EditorActivity;

public class ListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOADER_ID = 1;
    private RecyclerView mRecyclerView;
    private EventsCursorAdapter mAdapter;
    private LinearLayout mEmptyView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_view_event);
        mEmptyView=(LinearLayout) findViewById(R.id.empty_view);
        mProgressBar=(ProgressBar) findViewById(R.id.loading_progress);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                ((LinearLayoutManager) mLayoutManager).getOrientation()));
        mAdapter = new EventsCursorAdapter(this,onItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        LoaderManager.getInstance(ListActivity.this).restartLoader(LOADER_ID, null, ListActivity.this);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_event) {
            Intent i = new Intent(this, EditorActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private EventsCursorAdapter.OnItemClickListener onItemClickListener=new EventsCursorAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(long id) {
            Intent intent = new Intent(ListActivity.this, EditorActivity.class);
            intent.setData(ContentUris.withAppendedId(NotebookDbContract.EventContract.EVENT_CONTENT_URI,id));
            startActivity(intent);
        }
    };

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        Uri contactsUri = NotebookDbContract.ReminderContract.REMINDER_CONTENT_URI;
        mProgressBar.setVisibility(View.VISIBLE);
        return new CursorLoader(
                getApplicationContext(),
                contactsUri,
                null,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        if(mAdapter.getItemCount()!=0)
            mEmptyView.setVisibility(View.GONE);
        else
            mEmptyView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
