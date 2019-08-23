package com.ivan.horniichuk.notebook.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import com.ivan.horniichuk.notebook.data.NotebookDbContract.EventContract;
import com.ivan.horniichuk.notebook.data.NotebookDbContract.ReminderContract;
import com.ivan.horniichuk.notebook.utils.DateUtils;

import java.util.Date;


public class EventProvider extends ContentProvider {

    public static final String LOG_TAG = EventProvider.class.getSimpleName();
    private DbHelper mDbHelper;

    private static final int EVENTS = 100;
    private static final int EVENT_ID = 101;
    private static final int REMINDERS = 102;
    private static final int REMINDER_ID = 103;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static {
        sUriMatcher.addURI(NotebookDbContract.CONTENT_AUTHORITY, EventContract.PATH_EVENT,EVENTS);
        sUriMatcher.addURI(NotebookDbContract.CONTENT_AUTHORITY, EventContract.PATH_EVENT+"/#",EVENT_ID);
        sUriMatcher.addURI(NotebookDbContract.CONTENT_AUTHORITY, ReminderContract.PATH_REMINDER,REMINDERS);
        sUriMatcher.addURI(NotebookDbContract.CONTENT_AUTHORITY, ReminderContract.PATH_REMINDER+"/#",REMINDER_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper=new DbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor=null;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case EVENTS:
                cursor = database.query(EventContract.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case EVENT_ID:
                selection = EventContract.COLUMN_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(EventContract.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case REMINDERS:
                /*
                    SELECT event.id, event.title, event.due_date, reminder.type, reminder.r_due_date, event.days
   .                    FROM event, reminder
                    WHERE event.id=reminder.event_id AND ((event.days!=0 AND reminder.type=1) OR reminder.type=0)
                    ORDER BY reminder.r_due_date ASC;
                 */
                String query="SELECT "+ EventContract.TABLE_NAME+"."+ EventContract.COLUMN_ID +", "+
                        EventContract.TABLE_NAME +"."+ EventContract.COLUMN_TITLE +", "+
                        ReminderContract.TABLE_NAME+"."+ ReminderContract.COLUMN_TYPE +", "+
                        ReminderContract.TABLE_NAME+"."+ ReminderContract.COLUMN_DUE_DATE +
                        " FROM "+ EventContract.TABLE_NAME +", "+ ReminderContract.TABLE_NAME +
                        " WHERE "+ EventContract.TABLE_NAME +"."+ EventContract.COLUMN_ID +" = "+
                        ReminderContract.TABLE_NAME+"."+ ReminderContract.COLUMN_EVENT_ID+" and (("+
                        EventContract.COLUMN_DAYS+"!=0 and "+ReminderContract.COLUMN_TYPE+"="+ReminderContract.TYPE_REMINDER+
                        ") or "+ReminderContract.COLUMN_TYPE+"="+ReminderContract.TYPE_EVENT+" ) Order by "+
                        ReminderContract.TABLE_NAME+"."+ ReminderContract.COLUMN_DUE_DATE +" ASC;";
                cursor = database.rawQuery(query, null);
                break;
            case REMINDER_ID:
                selection = ReminderContract.COLUMN_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(ReminderContract.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case EVENTS:
                return insertEvent(uri,contentValues);
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
    }

    private Uri insertEvent(Uri uri, ContentValues contentValues) {
        String title=contentValues.getAsString(EventContract.COLUMN_TITLE);
        if(title.isEmpty()||title==null)
            throw new IllegalArgumentException("Title must be defined");
        Long due_date=contentValues.getAsLong(EventContract.COLUMN_DUE_DATE);
        if(due_date==null||! DateUtils.verifyToCurrentDate(new Date(due_date)))
            throw new IllegalArgumentException("Date is incorect, or before today");
        Integer status=contentValues.getAsInteger(EventContract.COLUMN_STATUS);
        if(status<0||status>2||status==null)
            throw new IllegalArgumentException("Incorrect status");
        Integer days=contentValues.getAsInteger(EventContract.COLUMN_DAYS);
        if(days<0||days==null)
            throw new IllegalArgumentException("Incorrect days due date");
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id=db.insert(EventContract.TABLE_NAME,null,contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case EVENTS:
                return updateEvent(uri, contentValues, selection, selectionArgs);
            case EVENT_ID:
                selection= EventContract.COLUMN_ID +"=?";
                selectionArgs=new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateEvent(uri,contentValues,selection,selectionArgs);

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

    }
    private int updateEvent(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){

        if(contentValues.size()==0)
            return 0;

        if(contentValues.containsKey(EventContract.COLUMN_TITLE))
        {
            String title=contentValues.getAsString(EventContract.COLUMN_TITLE);
            if(title.isEmpty()||title==null)
                throw new IllegalArgumentException("Title must be defined");
        }
        if(contentValues.containsKey(EventContract.COLUMN_DUE_DATE)) {
            Long due_date = contentValues.getAsLong(EventContract.COLUMN_DUE_DATE);
            if (due_date == null || !DateUtils.verifyToCurrentDate(new Date(due_date)))
                throw new IllegalArgumentException("Date is incorect, or before today");
        }
        if(contentValues.containsKey(EventContract.COLUMN_STATUS)) {
            Integer status = contentValues.getAsInteger(EventContract.COLUMN_STATUS);
            if (status < 0 || status > 2 || status == null)
                throw new IllegalArgumentException("Incorrect status");
        }
        if(contentValues.containsKey(EventContract.COLUMN_DAYS))
        {
            Integer days=contentValues.getAsInteger(EventContract.COLUMN_DAYS);
            if(days<0||days==null)
                throw new IllegalArgumentException("Incorrect days due date");
        }

        SQLiteDatabase db=mDbHelper.getWritableDatabase();

        getContext().getContentResolver().notifyChange(uri,null);
        return db.update(EventContract.TABLE_NAME,contentValues,selection,selectionArgs);
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match) {
            case EVENTS:
                getContext().getContentResolver().notifyChange(uri,null);
                return db.delete(EventContract.TABLE_NAME,selection,selectionArgs);
            case EVENT_ID:
                selection= EventContract.COLUMN_ID+"=?";
                selectionArgs=new String[] { String.valueOf(ContentUris.parseId(uri)) };
                getContext().getContentResolver().notifyChange(uri,null);
                return db.delete(EventContract.TABLE_NAME,selection,selectionArgs);
            case REMINDERS:
                getContext().getContentResolver().notifyChange(uri,null);
                return db.delete(ReminderContract.TABLE_NAME,selection,selectionArgs);
            case REMINDER_ID:
                selection= ReminderContract.COLUMN_ID+"=?";
                selectionArgs=new String[] { String.valueOf(ContentUris.parseId(uri)) };
                getContext().getContentResolver().notifyChange(uri,null);
                return db.delete(ReminderContract.TABLE_NAME,selection,selectionArgs);

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }
}
