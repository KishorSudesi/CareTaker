package com.hdfc.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.hdfc.newzeal.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

// ///////////////////////////////////////////////////////////////////////////////////////
// Inner Class
public class CalendarAdapter extends BaseAdapter implements View.OnClickListener {
    private static final int DAY_OFFSET = 1;
    private final Context _context;
    private final List<String> list;
    private final String[] weekNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private final HashMap<String, Integer> eventsPerMonthMap;
    String flag = "abc";
    String date_month_year;
    private int daysInMonth;
    private int currentDayOfMonth;
    private int currentWeekDay;
    private Button gridcell;
    private TextView num_events_per_day;

    // Days in Current Month
    public CalendarAdapter(Context context, int month, int year) {
        super();
        this._context = context;
        this.list = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
        setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

        // Print Month
        printMonth(month, year);

        // Find Number of Events
        eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
    }

    private String getMonthAsString(int i) {
        return months[i];
    }

    private int getNumberOfDaysOfMonth(int i) {
        return daysOfMonth[i];
    }

    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    private void printMonth(int mm, int yy) {
        int trailingSpaces = 0;
        int daysInPrevMonth = 0;
        int prevMonth = 0;
        int prevYear = 0;
        int nextMonth = 0;
        int nextYear = 0;

        int currentMonth = mm - 1;
        daysInMonth = getNumberOfDaysOfMonth(currentMonth);
        Log.e("m-y", String.valueOf(daysInMonth + " ~ " + currentMonth));

        // Gregorian Calendar : MINUS 1, set to FIRST OF MONTH
        GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);

        if (currentMonth == 11) {
            prevMonth = currentMonth - 1;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            nextMonth = 0;
            prevYear = yy;
            nextYear = yy + 1;
        } else if (currentMonth == 0) {
            prevMonth = 11;
            prevYear = yy - 1;
            nextYear = yy;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            nextMonth = 1;
        } else {
            prevMonth = currentMonth - 1;
            nextMonth = currentMonth + 1;
            nextYear = yy;
            prevYear = yy;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
        }

        int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
        trailingSpaces = currentWeekDay;

        //Log.e("LEAP ", String.valueOf());

        if (cal.isLeapYear(cal.get(Calendar.YEAR)) && currentMonth == 1) {
            ++daysInMonth;
        }

        // Daya
        /*for (int i = 0; i < weekNames.length; i++){
            list.add(weekNames[i] + "-WHITE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
        }*/


        // Trailing Month days
        for (int i = 0; i < trailingSpaces; i++) {
            list.add(String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i) + "-GREY" + "-" + getMonthAsString(prevMonth) + "-" + prevYear);
        }

        // Current Month Days
        for (int i = 1; i <= daysInMonth; i++) {
            if (i == getCurrentDayOfMonth())
                list.add(String.valueOf(i) + "-GREEN" + "-" + getMonthAsString(currentMonth) + "-" + yy);
            else
                list.add(String.valueOf(i) + "-WHITE" + "-" + getMonthAsString(currentMonth) + "-" + yy);

            Log.e("RTr", String.valueOf(i));
        }

        // Leading Month days
        for (int i = 0; i < list.size() % 7; i++) {
            list.add(String.valueOf(i + 1) + "-GREY" + "-" + getMonthAsString(nextMonth) + "-" + nextYear);
        }
    }

    private HashMap<String, Integer> findNumberOfEventsPerMonth(int year, int month) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        return map;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.calendar_cell_view, parent, false);
        }

        // Get a reference to the Day gridcell
        gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
        gridcell.setOnClickListener(this);

        // ACCOUNT FOR SPACING

        String[] day_color = list.get(position).split("-");
        String theday = day_color[0];
        String themonth = day_color[2];
        String theyear = day_color[3];
        if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null)) {
            if (eventsPerMonthMap.containsKey(theday)) {
                num_events_per_day = (TextView) row.findViewById(R.id.num_events_per_day);
                Integer numEvents = eventsPerMonthMap.get(theday);
                num_events_per_day.setText(numEvents.toString());
            }
        }

        // Set the Day GridCell
        gridcell.setText(theday);
        gridcell.setTag(theday + "-" + themonth + "-" + theyear);

        if (day_color[1].equals("GREY")) {
            gridcell.setTextColor(Color.LTGRAY);
        }

        if (day_color[1].equals("WHITE")) {
            gridcell.setTextColor(Color.BLACK);
        }

        if (day_color[1].equals("BLUE")) {
            gridcell.setTextColor(Color.BLUE);
        }

        if (day_color[1].equals("GREEN")) {
            gridcell.setBackgroundColor(_context.getResources().getColor(R.color.colorPrimaryDark));
            gridcell.setTextColor(Color.WHITE);
        }

        if (theday.equalsIgnoreCase("8") || theday.equalsIgnoreCase("21")) {
            gridcell.setBackgroundColor(Color.LTGRAY);
            gridcell.setTextColor(Color.WHITE);
        }

        return row;
    }

    @Override
    public void onClick(View view) {
        date_month_year = (String) view.getTag();
        flag = "Date selected ...";
        //selectedDayMonthYearButton.setText("Selected: " + date_month_year);
    }

    public int getCurrentDayOfMonth() {
        return currentDayOfMonth;
    }

    private void setCurrentDayOfMonth(int currentDayOfMonth) {
        this.currentDayOfMonth = currentDayOfMonth;
    }

    public int getCurrentWeekDay() {
        return currentWeekDay;
    }

    public void setCurrentWeekDay(int currentWeekDay) {
        this.currentWeekDay = currentWeekDay;
    }
}