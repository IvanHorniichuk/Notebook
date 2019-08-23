package com.ivan.horniichuk.notebook.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="notebook.db";
    public static final int DATABASE_VERSION=3;

    private static final String CREATE_TABLE1="CREATE TABLE "+ NotebookDbContract.EventContract.TABLE_NAME + " (\n"+
            NotebookDbContract.EventContract.COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            NotebookDbContract.EventContract.COLUMN_TITLE +" TEXT NOT NULL,\n" +
            NotebookDbContract.EventContract.COLUMN_DUE_DATE +" INTEGER NOT NULL,\n" +
            NotebookDbContract.EventContract.COLUMN_STATUS + " INTEGER NOT NULL DEFAULT 0,\n" +
            NotebookDbContract.EventContract.COLUMN_DAYS + " INTEGER NOT NULL DEFAULT 3 );\n" ;

    private static final String CREATE_TABLE2="CREATE TABLE "+ NotebookDbContract.ReminderContract.TABLE_NAME +" (\n " +
            NotebookDbContract.ReminderContract.COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            NotebookDbContract.ReminderContract.COLUMN_EVENT_ID+" INTEGER NOT NULL,\n" +
            NotebookDbContract.ReminderContract.COLUMN_TYPE+" INTEGER DEFAULT 0,\n" +
            NotebookDbContract.ReminderContract.COLUMN_DUE_DATE+" INTEGER NOT NULL,\n" +
            " CONSTRAINT "+ NotebookDbContract.ReminderContract.CONSTRAINT_FOREIGN_KEY +
            " FOREIGN KEY ("+ NotebookDbContract.ReminderContract.COLUMN_EVENT_ID +") REFERENCES "+
            NotebookDbContract.EventContract.TABLE_NAME +"("+ NotebookDbContract.EventContract.COLUMN_ID +") " +
            ");";

    /*
    CREATE TRIGGER aft_insert AFTER INSERT ON "event"
        BEGIN
            INSERT INTO reminder(event_id,due_date,is_reminder) VALUES(NEW.id,NEW.due_date-NEW.days*86400,1);
            INSERT INTO reminder(event_id,due_date,is_reminder) VALUES(NEW.id,NEW.due_date,0);
        END
    * */
    private static final String CREATE_TRIGGER1="CREATE TRIGGER "+ NotebookDbContract.ReminderContract.TRIGGER_ON_INSERT
            +" AFTER INSERT ON "+ NotebookDbContract.EventContract.TABLE_NAME + " \nBEGIN\n" +
            "INSERT INTO "+ NotebookDbContract.ReminderContract.TABLE_NAME+"("+ NotebookDbContract.ReminderContract.COLUMN_EVENT_ID+","+
            NotebookDbContract.ReminderContract.COLUMN_DUE_DATE+","+ NotebookDbContract.ReminderContract.COLUMN_TYPE+")" +
            " VALUES(NEW."+ NotebookDbContract.EventContract.COLUMN_ID +",NEW."+ NotebookDbContract.EventContract.COLUMN_DUE_DATE +
            "-NEW."+ NotebookDbContract.EventContract.COLUMN_DAYS +"*86400000,"+ NotebookDbContract.ReminderContract.TYPE_REMINDER +");\n" +
            "INSERT INTO "+ NotebookDbContract.ReminderContract.TABLE_NAME+"("+ NotebookDbContract.ReminderContract.COLUMN_EVENT_ID+","+
            NotebookDbContract.ReminderContract.COLUMN_DUE_DATE+","+ NotebookDbContract.ReminderContract.COLUMN_TYPE+")\n" +
            " VALUES(NEW."+ NotebookDbContract.EventContract.COLUMN_ID +",NEW."+ NotebookDbContract.EventContract.COLUMN_DUE_DATE +","+
            NotebookDbContract.ReminderContract.TYPE_EVENT +");\n" +
            "END";
    /*
    CREATE TRIGGER on_date_update UPDATE ON event
        BEGIN
               UPDATE reminder SET due_date = new.due_date-new.days*86400 WHERE event_id = old.id and is_reminder=1;
                UPDATE reminder SET due_date = new.due_date WHERE event_id = old.id and is_reminder=0;
        END
     */
    private static final String CREATE_TRIGGER2="CREATE TRIGGER "+ NotebookDbContract.ReminderContract.TRIGGER_ON_UPDATE +" UPDATE ON "+
            NotebookDbContract.EventContract.TABLE_NAME +" \n" +
            "  BEGIN\n" +
            "    UPDATE "+ NotebookDbContract.ReminderContract.TABLE_NAME +" SET "+ NotebookDbContract.ReminderContract.COLUMN_DUE_DATE +
            " = new."+ NotebookDbContract.EventContract.COLUMN_DUE_DATE +"-new."+ NotebookDbContract.EventContract.COLUMN_DAYS+
            "*86400000 WHERE "+ NotebookDbContract.ReminderContract.COLUMN_EVENT_ID +" = old."+ NotebookDbContract.EventContract.COLUMN_ID +
            " and "+ NotebookDbContract.ReminderContract.COLUMN_TYPE +"="+ NotebookDbContract.ReminderContract.TYPE_REMINDER +";\n" +
            " UPDATE "+ NotebookDbContract.ReminderContract.TABLE_NAME +" SET "+ NotebookDbContract.ReminderContract.COLUMN_DUE_DATE +
            "= new."+ NotebookDbContract.EventContract.COLUMN_DUE_DATE +" WHERE "+ NotebookDbContract.ReminderContract.COLUMN_EVENT_ID +
            " = old."+ NotebookDbContract.EventContract.COLUMN_ID +" and "+ NotebookDbContract.ReminderContract.COLUMN_TYPE +"="+
            NotebookDbContract.ReminderContract.TYPE_EVENT +";\n END";

    private static final String DROP_TABLE1="DROP TABLE IF EXISTS "+ NotebookDbContract.EventContract.TABLE_NAME+";\n";
    private static final String DROP_TABLE2="DROP TABLE IF EXISTS "+ NotebookDbContract.ReminderContract.TABLE_NAME+";";

    private static final String DROP_TRIGGER1="DROP TRIGGER IF EXISTS "+ NotebookDbContract.ReminderContract.TRIGGER_ON_INSERT +";\n";
    private static final String DROP_TRIGGER2="DROP TRIGGER IF EXISTS "+ NotebookDbContract.ReminderContract.TRIGGER_ON_UPDATE +";";



    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE1);
        db.execSQL(CREATE_TABLE2);
        db.execSQL(CREATE_TRIGGER1);
        db.execSQL(CREATE_TRIGGER2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE1);
        db.execSQL(DROP_TABLE2);
        db.execSQL(DROP_TRIGGER1);
        db.execSQL(DROP_TRIGGER2);
        onCreate(db);

    }
}
