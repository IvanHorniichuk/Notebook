package com.ivan.horniichuk.notebook.ui.listActivity;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.ivan.horniichuk.notebook.utils.DateUtils;
import com.ivan.horniichuk.notebook.R;
import com.ivan.horniichuk.notebook.data.NotebookDbContract;

import java.util.Date;

public class EventsCursorAdapter extends BaseCursorAdapter<EventsCursorAdapter.EventViewHolder> {

        private Context mContext;
        private final OnItemClickListener listener;

        public interface OnItemClickListener {
            void onItemClick(long eventId);
        }

        public EventsCursorAdapter(Context context,OnItemClickListener listener) {
            super(null);
            this.listener=listener;
            this.mContext=context;
        }

        @Override
        public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View formNameView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
            return new EventViewHolder(formNameView);
        }

        @Override
        public void onBindViewHolder(EventViewHolder holder, Cursor cursor) {
            int titleIndex=cursor.getColumnIndex(NotebookDbContract.EventContract.COLUMN_TITLE);
            int typeIndex=cursor.getColumnIndex(NotebookDbContract.ReminderContract.COLUMN_TYPE);
            int eventDueDateIndex=cursor.getColumnIndex(NotebookDbContract.EventContract.COLUMN_DUE_DATE);
            int reminderDueDateIndex=cursor.getColumnIndex(NotebookDbContract.ReminderContract.COLUMN_DUE_DATE);
            int daysIndex=cursor.getColumnIndex(NotebookDbContract.EventContract.COLUMN_DAYS);

            String title;
            int type;
            long reminderDueDate;
            title=cursor.getString(titleIndex);
            type=cursor.getInt(typeIndex);
            reminderDueDate=cursor.getLong(reminderDueDateIndex);
            holder.eventDueDate.setText(DateUtils.toDateString(new Date(reminderDueDate)));
            holder.eventTitle.setText(title);
            if(type == NotebookDbContract.ReminderContract.TYPE_REMINDER)
            {
                holder.dueToTextView.setVisibility(View.VISIBLE);
                holder.eventTitle.setTextColor(ContextCompat.getColor(mContext,R.color.colorBlue));
                holder.eventDueDate.setTextColor(ContextCompat.getColor(mContext,R.color.colorBlue));
            }
            else
                {
                    holder.dueToTextView.setVisibility(View.GONE);
                    holder.eventTitle.setTextColor(ContextCompat.getColor(mContext,android.R.color.tab_indicator_text));
                    holder.eventDueDate.setTextColor(ContextCompat.getColor(mContext,android.R.color.tab_indicator_text));
                }
        }

    @Override
    public void swapCursor(Cursor newCursor) {
        super.swapCursor(newCursor);
    }

    class EventViewHolder extends RecyclerView.ViewHolder {

        TextView eventTitle;
        TextView eventDueDate;
        TextView dueToTextView;

        EventViewHolder(final View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.tv_event_title);
            eventDueDate= itemView.findViewById(R.id.tv_event_due_date);
            dueToTextView=itemView.findViewById(R.id.tv_due_to);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(EventViewHolder.this.getItemId());
                }
            });
        }
    }
}

