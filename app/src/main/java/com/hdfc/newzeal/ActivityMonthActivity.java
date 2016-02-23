package com.hdfc.newzeal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdfc.adapters.CalendarAdapter;
import com.hdfc.config.Config;

import java.util.Calendar;
import java.util.Locale;

//  implements View.OnClickListener
public class ActivityMonthActivity extends AppCompatActivity {

    private static final String dateTemplate = "MMMM yyyy";
    /*private Button selectedDayMonthYearButton;
    private Button currentMonth;
    private ImageView prevMonth;
    private ImageView nextMonth;*/
    private GridView calendarView;
    private CalendarAdapter adapter;
    private Calendar _calendar;
    private int month, year, day;
    private TextView txtViewDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_month);

        //
        Button btnMonthly = (Button) findViewById(R.id.buttonMonthly);
        txtViewDate = (TextView) findViewById(R.id.activity_header_date);

        btnMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newIntent = new Intent(ActivityMonthActivity.this, ActivityListActivity.class);
                startActivity(newIntent);
            }
        });

        ImageView addActivity = (ImageView) findViewById(R.id.addActivity);

        addActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newIntent = new Intent(ActivityMonthActivity.this, AddNewActivityActivity.class);
                startActivity(newIntent);
            }
        });


        _calendar = Calendar.getInstance(Locale.getDefault());
        month = _calendar.get(Calendar.MONTH) + 1;
        year = _calendar.get(Calendar.YEAR);
        day = _calendar.get(Calendar.DATE);

        txtViewDate.setText("Activities on " + day + "-" + Config.months[month - 1] + "-" + year);

        /*selectedDayMonthYearButton = (Button) this.findViewById(R.id.selectedDayMonthYear);
        selectedDayMonthYearButton.setText("Select Date");

        prevMonth = (ImageView) this.findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);

        currentMonth = (Button) this.findViewById(R.id.currentMonth);
        currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));

        nextMonth = (ImageView) this.findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);*/

        calendarView = (GridView) this.findViewById(R.id.calendar);

        // Initialised
        adapter = new CalendarAdapter(ActivityMonthActivity.this, month, year);
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);

        //
    }
    /*private void setGridCellAdapterToDate(int month, int year){
        adapter = new CalendarAdapter(ActivityMonthActivity.this, month, year);
        _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
        currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);
    }*/

   /* @Override
    public void onClick(View v){
        if (v == prevMonth){
            if (month <= 1){
                month = 12;
                year--;
            }
            else
                month--;
            setGridCellAdapterToDate(month, year);
        }
        if (v == nextMonth){
            if (month > 11){
                month = 1;
                year++;
            }
            else
                month++;
            setGridCellAdapterToDate(month, year);
        }

    }*/

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

}
