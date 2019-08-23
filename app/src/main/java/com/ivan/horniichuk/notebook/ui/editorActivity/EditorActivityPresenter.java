package com.ivan.horniichuk.notebook.ui.editorActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.ivan.horniichuk.notebook.R;
import com.ivan.horniichuk.notebook.data.EventEntry;
import com.ivan.horniichuk.notebook.data.NotebookDbContract;
import com.ivan.horniichuk.notebook.ui.BasePresenter;

import java.util.Date;

public class EditorActivityPresenter extends BasePresenter<EditorActivityContract> {
    public void initializeViewsValues(Uri uri)
    {
        EventEntry entry;
        if(uri==null)
        {
            entry=new EventEntry(context.getResources().getString(R.string.new_task), NotebookDbContract.EventContract.STATUS_SCHEDULED,(new Date()).getTime(),3);
        }
        else
        {
            Cursor cursor=context.getContentResolver().query(uri,null,null,null,null);
            cursor.moveToFirst();

            int titleIndex=cursor.getColumnIndex(NotebookDbContract.EventContract.COLUMN_TITLE);
            int dueDateIndex=cursor.getColumnIndex(NotebookDbContract.EventContract.COLUMN_DUE_DATE);
            int statusIndex=cursor.getColumnIndex(NotebookDbContract.EventContract.COLUMN_STATUS);
            int daysIndex=cursor.getColumnIndex(NotebookDbContract.EventContract.COLUMN_DAYS);

            String title=cursor.getString(titleIndex);
            long dueDate=cursor.getLong(dueDateIndex);
            int status=cursor.getInt(statusIndex);
            int days=cursor.getInt(daysIndex);
            entry=new EventEntry(title,status,dueDate,days);
        }
        view.setEvent(entry);
    }
    public void updateEventEntry(EventEntry entry,Uri uri)
    {
        context.getContentResolver().update(uri,createContentValues(entry),null,null);
    }

    public void insertEventEntry(EventEntry entry)
    {
        context.getContentResolver().insert(NotebookDbContract.EventContract.EVENT_CONTENT_URI,createContentValues(entry));
    }

    private ContentValues createContentValues(EventEntry entry)
    {
        ContentValues values=new ContentValues();
        values.put(NotebookDbContract.EventContract.COLUMN_TITLE,entry.getTitle());
        values.put(NotebookDbContract.EventContract.COLUMN_DUE_DATE,entry.getDueDate());
        values.put(NotebookDbContract.EventContract.COLUMN_STATUS,entry.getStatus());
        values.put(NotebookDbContract.EventContract.COLUMN_DAYS,entry.getReminderDays());
        return values;
    }
}
