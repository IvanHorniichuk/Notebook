package com.ivan.horniichuk.notebook.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class NotebookDbContract {

    public static final String CONTENT_AUTHORITY = "com.ivan.horniichuk.notebook.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static abstract class EventContract implements BaseColumns {


        public static final String TABLE_NAME = "event";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_DUE_DATE = "due_date";
        public static final String COLUMN_DAYS = "days";

        public static final int STATUS_SCHEDULED = 0;
        public static final int STATUS_DONE = 1;
        public static final int STATUS_DISMISSED = 2;

        public static final String PATH_EVENT = "events";
        public static final Uri EVENT_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_EVENT);
    }

    public static abstract class ReminderContract implements BaseColumns {

        public static final String TABLE_NAME = "reminder";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_EVENT_ID = "event_id";
        public static final String COLUMN_DUE_DATE = "r_due_date";
        public static final String COLUMN_TYPE = "type";

        public static final String CONSTRAINT_FOREIGN_KEY = "event_id_fk";

        public static final int TYPE_REMINDER=1;
        public static final int TYPE_EVENT=0;

        public static final String TRIGGER_ON_INSERT="on_event_insert_trigger";
        public static final String TRIGGER_ON_UPDATE="on_event_update_triger";

        public static final String PATH_REMINDER = "reminders";
        public static final Uri REMINDER_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_REMINDER);
    }
}
