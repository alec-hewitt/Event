package com.ahewdev.event;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Alec on 10/5/2014.
 */
public class Week extends Activity{

    private CharSequence mTitle;

    List<CalendarEvent> eventList;

    LinearLayout layout;
    ListView lv;
    String title;
    String start;
    String eventText;
    String[] eventTextItems;
    ArrayAdapter<String> listAdapter;

    //reads calendars, and queries for events; dumps into lists respective of timeframe
    public void readCalendar(Context context, int days, int hours){

        ContentResolver contentResolver = context.getContentResolver();

        //make cursor and read from calendar
        /* CURSOR ONLY VALID FOR ANDROID API 4.0+ */
        //declaration
        final Cursor cursor;
        //initialization
        cursor = contentResolver.query(Uri.parse("content://com.android.calendar/events"),
                new String[]{ "calendar_id", "title", "description", "dtstart", "dtend", "eventLocation" },
                null, null, null);



        //create a set containing all calendar ID's available on device
        HashSet<String> calendarIds = getCalendarIds(cursor);

        //create hashmap of calendar ids and the events of each id
        HashMap<String, List<CalendarEvent>> eventMap = new HashMap<String, List<CalendarEvent>>();

        //loop over all calendars, to sort and return events
        for (String id : calendarIds) {

            //create builder to define time span
            //builder defiines parameters in which to search
            Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
            long now = new Date().getTime();

            //initial(first) time
            //add time restraints to the uri builder from function params, in order to filter events within
            ContentUris.appendId(builder, now);
            //end (final) time
            ContentUris.appendId(builder, now + (DateUtils.DAY_IN_MILLIS * days) + (DateUtils.HOUR_IN_MILLIS * hours));

            //create event cursor to find all events in calendar
            Cursor eventCursor = contentResolver.query(builder.build(),
                    new String[] {"title", "begin", "end", "allDay"}, id,
                    null, "startDay ASC, startMinute ASC");

            //debug count of events
            System.out.println("eventCursor count=" +eventCursor.getCount());

            //if there are any events, count will exceed zero
            if(eventCursor.getCount() > 0){

                //make list of calendar events in current calendar
                eventList = new ArrayList<CalendarEvent>();

                ///move cursor to first event
                eventCursor.moveToFirst();

                //temp CalendarEvent object, to hold this instances event information, before tranferring into list
                CalendarEvent ce = loadEvent(eventCursor);

                //adds first object to list of events
                eventList.add(ce);

                //initialize gui
                drawEvent(ce);

                //debug event objcet info (CalendarEvent class function)
                System.out.println(ce.toString());

                //while there are more events, move to next instance
                while(eventCursor.moveToNext()){

                    //put event into our temp object
                    ce = loadEvent(eventCursor);

                    //add event to the list
                    eventList.add(ce);

                    //initialize gui
                    drawEvent(ce);

                    //debug event objcet info (CalendarEvent class function)
                    System.out.println(ce.toString());
                }

                Collections.sort(eventList);
                eventMap.put(id, eventList);

                System.out.println(eventMap.keySet().size() + " " + eventMap.values());

            } else {
                //thre are no events
                //display message on screen
                TextView noEventsAvaillable = (TextView) findViewById(R.id.noEventsAvaillable);
                noEventsAvaillable.setText("You Have No Events This Week");
            }

        }

    }

    // Returns a new instance of the calendar object
    private static CalendarEvent loadEvent(Cursor csr) {
        return new CalendarEvent(csr.getString(0),
                new Date(csr.getLong(1)),
                new Date(csr.getLong(2)),
                !csr.getString(3).equals("0"));
    }

    private static HashSet<String>getCalendarIds(Cursor cursor) {

        HashSet<String> calendarIds = new HashSet<String>();

        try {
            //if there are any calendars, continue
            if (cursor.getCount() > 0) {
                //loop to set id for all of the calendars
                while (cursor.moveToNext()) {

                    String _id = cursor.getString(0);
                    String displayName = cursor.getString(1);
                    Boolean selected = !cursor.getString(2).equals("0");

                    System.out.println("Id: " + _id + " Display Name: " + displayName + " Selected: " + selected);
                    calendarIds.add(_id);
                }
            }
        }

        catch(AssertionError ex){
            ex.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return calendarIds;
    }

    public void drawEvent(CalendarEvent event){

        //button layout parameters
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        lv = (ListView) findViewById(R.id.eventsList);

        eventTextItems = new String[]{};

        ArrayList<String> eventArrayList = new ArrayList<String>();
        //add string array to the array list
        eventArrayList.addAll( Arrays.asList(eventTextItems));
        ///create array adapter using list of event texxt titems
        listAdapter = new ArrayAdapter<String>(this, R.layout.textv, eventArrayList);

        for(int i = 0; i < eventList.size(); i++){

            //title string
            title = eventList.get(i).getTitle();
            //change date to string on dd-MM-yyyy format e.g. "14-09-2011"
            SimpleDateFormat dateformatJava = new SimpleDateFormat("dd/MM hh:mm");
            start = dateformatJava.format(eventList.get(i).getBegin());
            //combine
            eventText = title +  "\n" + start;
            //add each event to the adapter for the list view
            listAdapter.add(eventText);

        }

        //set arrayadapter as the listviews adapter
        lv.setAdapter(listAdapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_events);

        readCalendar(getApplicationContext(), 7, 0);

        //today button
        final Button button1 = (Button) findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                launchToday();
            }
        });

        //month button
        final Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                launchMonth();
            }
        });

    }

    public void launchToday(){
        Intent todayIntent = new Intent(this, Events.class);
        startActivity(todayIntent);
    }

    public void launchMonth(){
        Intent monthIntent = new Intent(this, Month.class);
        startActivity(monthIntent);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
