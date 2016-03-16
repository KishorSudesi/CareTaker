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

import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.models.ActivityListModel;
import com.hdfc.models.ActivityModel;
import com.hdfc.caretaker.R;
import com.hdfc.caretaker.fragments.ActivityMonthFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends BaseAdapter {
    private static final int DAY_OFFSET = 1;
    private final Context _context;
    private final List<String> list;
    //private final HashMap<String, Integer> eventsPerMonthMap;
    private int currentDayOfMonth;
    private int currentWeekDay;

    public final static SimpleDateFormat writeFormatDate = new SimpleDateFormat("dd", Locale.US);
    public final static SimpleDateFormat writeFormatMonth = new SimpleDateFormat("MMMM", Locale.US);
    public final static SimpleDateFormat writeFormatYear = new SimpleDateFormat("yyyy", Locale.US);

    public final static SimpleDateFormat readFormat = new SimpleDateFormat("kk:mm aa dd MMM yyyy", Locale.US);

    // Days in Current Month
    public CalendarAdapter(Context context, int month, int year) {
        super();
        this._context = context;
        this.list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
        setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

        // Print Month
        printMonth(month, year);

        // Find Number of Events
       // eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
    }

    private String getMonthAsString(int i) {
        return Config.months[i];
    }

    private int getNumberOfDaysOfMonth(int i) {
        return Config.daysOfMonth[i];
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
        int daysInMonth = getNumberOfDaysOfMonth(currentMonth);

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

        trailingSpaces = cal.get(Calendar.DAY_OF_WEEK) - 1;

        if (cal.isLeapYear(cal.get(Calendar.YEAR)) && currentMonth == 1) {
            ++daysInMonth;
        }

        // Daya
        for (int i = 0; i < Config.weekNames.length; i++) {
            list.add(Config.weekNames[i] + "-WHITE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
        }


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
        }

        // Leading Month days
        for (int i = 0; i < list.size() % 7; i++) {
            list.add(String.valueOf(i + 1) + "-GREY" + "-" + getMonthAsString(nextMonth) + "-" + nextYear);
        }
    }

   /* private HashMap<String, Integer> findNumberOfEventsPerMonth(int year, int month) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();

     try {
         for (ActivityListModel activityModel : ActivityMonthFragment.activitiesModelArrayList) {

             //formatDateActivity(activityModel.getStrActivityDate());

             Date date = readFormat.parse(activityModel.getStrDate());
             //libs.convertStringToDate(activityModel.getStrActivityDate());

             int iMonth = Integer.parseInt(writeFormatMonth.format(date));
             int iYear = Integer.parseInt(writeFormatYear.format(date));
             int iDate = Integer.parseInt(writeFormatDate.format(date));

             if (iMonth == month && year == iYear) {
                 map.put(String.valueOf(iDate), iDate);
                 activitiesModelCalendar.add(activityModel);
             }
         }
     }catch (Exception e){
         e.printStackTrace();
     }

        return map;
    }*/

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
        Button gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
        //TextView num_events_per_day = (TextView) row.findViewById(R.id.num_events_per_day);
        //gridcell.setOnClickListener(this);

        String[] day_color = list.get(position).split("-");

        String theday = day_color[0];
        String themonth = day_color[2];
        String theyear = day_color[3];

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
            gridcell.setTextColor(_context.getResources().getColor(R.color.colorPrimary));
        }

       /* if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null)) {
            if (eventsPerMonthMap.containsKey(theday)) {
                Integer numEvents = eventsPerMonthMap.get(theday);
                Libs.log(String.valueOf(numEvents), " EVE ");
                gridcell.setTextColor(Color.RED);
            }
        }*/

        if(position>6) {

            int iDay = Integer.parseInt(day_color[0]);
            int iYear = Integer.parseInt(day_color[3]);

            try {
                for (ActivityListModel activityModel : ActivityMonthFragment.activitiesModelArrayList) {

                    Date date = readFormat.parse(activityModel.getStrDateTime());

                    String strActivityMonth = writeFormatMonth.format(date);
                    int iActivityYear = Integer.parseInt(writeFormatYear.format(date));
                    int iActivityDate = Integer.parseInt(writeFormatDate.format(date));

                    Libs.log(String.valueOf(iActivityYear + " == " + iYear + " && " + strActivityMonth + " EQS " + themonth + " && " + iActivityDate + " == " + iDay), " Compare ");

                    if (iActivityYear == iYear && strActivityMonth.trim().equalsIgnoreCase(themonth) && iActivityDate == iDay) {
                        gridcell.setTextColor(Color.RED);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return row;
    }

    /*@Override
    public void onClick(View view) {
        date_month_year = (String) view.getTag();
        flag = "Date selected ...";
        Libs.log(date_month_year, flag);

        view.setBackgroundColor(_context.getResources().getColor(R.color.colorPrimary));
    }*/

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