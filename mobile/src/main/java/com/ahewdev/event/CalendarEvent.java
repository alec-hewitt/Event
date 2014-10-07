package com.ahewdev.event;

import java.util.Date;

/**
 * Created by Alec on 10/2/2014.
 */
public class CalendarEvent implements Comparable<CalendarEvent>{

    private String title;
    private Date begin;
    private Date end;
    private boolean allDay;

    public CalendarEvent(){

    }

    public CalendarEvent(String title, Date begin, Date end, boolean allDay){
        this.title = title;
        this.begin = begin;
        this.end = end;
        this.allDay = allDay;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){

    }

    public Date getBegin(){
        return begin;
    }

    public void setBegin(Date begin){
        this.begin = begin;
    }

    public Date getEnd(){
        return end;
    }

    public void setEnd(Date end){
        this.end = end;
    }

    public boolean isAllDay(){
        return allDay;
    }

    public void setAllDay(boolean allDay){
        this.allDay = allDay;
    }

    @Override
    public String toString(){
        return getTitle() + " " + getBegin() + " " + getEnd() + " " + isAllDay();
    }

    @Override
    public int compareTo(CalendarEvent other) {
        // -1 = less, 0 = equal, 1 = greater
        return getBegin().compareTo(other.begin);
    }
}
