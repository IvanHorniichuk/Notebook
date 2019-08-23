package com.ivan.horniichuk.notebook.ui.editorActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ivan.horniichuk.notebook.utils.DateUtils;
import com.ivan.horniichuk.notebook.R;
import com.ivan.horniichuk.notebook.data.EventEntry;
import com.ivan.horniichuk.notebook.data.NotebookDbContract;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class EditorActivity extends AppCompatActivity implements EditorActivityContract{

    private EditText titleEditText;
    private ImageButton cancelButton;
    private TextView dateTextView;
    private Spinner statusSpinner;
    private EditText reminderDaysEditText;
    private TextView daysView;

    private int currentStatus =0;
    private Calendar currentDate =Calendar.getInstance();
    private Uri currentUri=null;

    private EditorActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        titleEditText=(EditText)findViewById(R.id.et_title);
        cancelButton=(ImageButton)findViewById(R.id.btn_cancel);
        dateTextView=(TextView) findViewById(R.id.tv_date);
        statusSpinner=(Spinner) findViewById(R.id.spinner_status);
        reminderDaysEditText=(EditText) findViewById(R.id.et_reminder_days);
        daysView=(TextView)findViewById(R.id.days_view);

        presenter=new EditorActivityPresenter();
        presenter.onViewAttach(this,this);

        setupSpinner();
        statusSpinner.setOnTouchListener(onSpinnerDoubleTouch);

        titleEditText.setOnTouchListener(onTitleDoubleTouch);
        titleEditText.setOnEditorActionListener(onDoneAction);
        titleEditText.setInputType(InputType.TYPE_NULL);

        daysView.setOnTouchListener(onReminderDaysDoubleTouch);
        reminderDaysEditText.setOnTouchListener(onReminderDaysDoubleTouch);
        reminderDaysEditText.setOnEditorActionListener(onDoneAction);
        reminderDaysEditText.setInputType(InputType.TYPE_NULL);

        cancelButton.setOnClickListener(onCancelButtonClick);
        dateTextView.setOnTouchListener(onDateDoubleTouch);

        Intent intent=getIntent();
        currentUri=intent.getData();
        presenter.initializeViewsValues(currentUri);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void setupSpinner() {
        ArrayAdapter<String> statusSpinnerAdapter = new ArrayAdapter<String>(this,
                R.layout.status_spinner_item, Arrays.asList(getResources().getStringArray(R.array.array_status_options))) {

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                View v = null;
                v = super.getDropDownView(position, null, parent);
                if (position == currentStatus) {
                    ((TextView)v).setTextColor(ContextCompat.getColor(EditorActivity.this,R.color.colorBlue));
                }
                else {
                    ((TextView)v).setTextColor(ContextCompat.getColor(EditorActivity.this,android.R.color.tab_indicator_text));
                }
                return v;
            }
        };

        statusSpinnerAdapter.setDropDownViewResource(R.layout.status_spinner_dropdown_item);
        statusSpinner.setAdapter(statusSpinnerAdapter);
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.status_scheduled))) {
                        currentStatus = NotebookDbContract.EventContract.STATUS_SCHEDULED;
                    } else if (selection.equals(getString(R.string.status_done))) {
                        currentStatus = NotebookDbContract.EventContract.STATUS_DONE;
                    } else {
                        currentStatus = NotebookDbContract.EventContract.STATUS_DISMISSED;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentStatus = 0;
            }
        });
    }

    private View.OnTouchListener onTitleDoubleTouch =new View.OnTouchListener() {
        private GestureDetector gestureDetector = new GestureDetector(null, new GestureDetector.SimpleOnGestureListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                titleEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                titleEditText.setSelection(titleEditText.getText().length());
                return super.onDoubleTap(e);
            }
        });
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            return false;
        }
    };

    private View.OnTouchListener onReminderDaysDoubleTouch =new View.OnTouchListener() {
        private GestureDetector gestureDetector = new GestureDetector(null, new GestureDetector.SimpleOnGestureListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                reminderDaysEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                reminderDaysEditText.setSelection(reminderDaysEditText.getText().length());
                return super.onDoubleTap(e);
            }
        });
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            return false;
        }
    };

    private TextView.OnEditorActionListener onDoneAction =new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if(i== EditorInfo.IME_ACTION_DONE){
                textView.setInputType(InputType.TYPE_NULL);
            }
            return false;
        }
    };

    private View.OnTouchListener onSpinnerDoubleTouch =new View.OnTouchListener() {
        private GestureDetector gestureDetector = new GestureDetector(null, new GestureDetector.SimpleOnGestureListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                statusSpinner.performClick();
                return false;
            }
        });
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            return true;
        }
    };

    private View.OnTouchListener onDateDoubleTouch =new View.OnTouchListener() {
        private GestureDetector gestureDetector = new GestureDetector(null, new GestureDetector.SimpleOnGestureListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                openDateDialog();
                return false;
            }
        });
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            return true;
        }
    };

    private View.OnClickListener onCancelButtonClick= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EventEntry tmp=createEventEntry();
            if(tmp!=null) {
                if (currentUri != null)
                    presenter.updateEventEntry(tmp, currentUri);
                else
                    presenter.insertEventEntry(tmp);
                finish();
            }

        }
    };

    private DatePickerDialog.OnDateSetListener dateDialog=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            currentDate.set(Calendar.YEAR, year);
            currentDate.set(Calendar.MONTH, monthOfYear);
            currentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDate();
        }
    };

    public void openDateDialog() {
        DatePickerDialog dialog=new DatePickerDialog(this, dateDialog,
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }

    public void setInitialDate() {
        dateTextView.setText(DateUtils.toDateString(currentDate.getTime()));
    }


    @Override
    public void setEvent(EventEntry entry) {
        titleEditText.setText(entry.getTitle());
        currentDate.setTime(new Date(entry.getDueDate()));
        setInitialDate();
        selectStatusSpinnerItem(entry.getStatus());
        reminderDaysEditText.setText(String.valueOf(entry.getReminderDays()));
    }

    private void selectStatusSpinnerItem(int stausType)
    {
        currentStatus =stausType;
        statusSpinner.setSelection(stausType);
    }

    private EventEntry createEventEntry()
    {
        String title=titleEditText.getText().toString().trim();
        String days=reminderDaysEditText.getText().toString();
        if(!title.isEmpty() && title!=null && !days.isEmpty() && days!=null)
        {
            return new EventEntry(title,
                    currentStatus,
                    currentDate.getTime().getTime(),
                    Integer.valueOf(days));
        }
        else {
            Toast.makeText(this,"Fill Title and Days!",Toast.LENGTH_LONG).show();
            return null;
        }
    }
}
