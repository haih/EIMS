// Copyright 2012 Square, Inc.
package com.csoft.calendarview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.csoft.employeemanager.R;
import com.csoft.util.T;

public class MonthView extends LinearLayout 
{
  TextView title;
  CalendarGridView grid;
  private Listener listener;
  List<CalendarCellView> data=new ArrayList<CalendarCellView>();
  boolean reachTofirst=false;
  public static MonthView create(ViewGroup parent, LayoutInflater inflater,
      DateFormat weekdayNameFormat, Listener listener, Calendar today, int dividerColor,
      int dayBackgroundResId, int dayTextColorResId, int titleTextColor, int headerTextColor) {
    final MonthView view = (MonthView) inflater.inflate(R.layout.month, parent, false);
    view.setDividerColor(dividerColor);
    view.setDayTextColor(dayTextColorResId);
    view.setTitleTextColor(titleTextColor);
    view.setHeaderTextColor(headerTextColor);

    if (dayBackgroundResId != 0) {
      view.setDayBackground(dayBackgroundResId);
    }

    final int originalDayOfWeek = today.get(Calendar.DAY_OF_WEEK);

    int firstDayOfWeek = today.getFirstDayOfWeek();
    final CalendarRowView headerRow = (CalendarRowView) view.grid.getChildAt(0);
    for (int offset = 0; offset < 7; offset++) {
      today.set(Calendar.DAY_OF_WEEK, firstDayOfWeek + offset);
      final TextView textView = (TextView) headerRow.getChildAt(offset);
      textView.setText(weekdayNameFormat.format(today.getTime()));
    }
    today.set(Calendar.DAY_OF_WEEK, originalDayOfWeek);
    view.listener = listener;
    return view;
  }

  public MonthView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    title = (TextView) findViewById(R.id.title);
    grid = (CalendarGridView) findViewById(R.id.calendar_grid);
  }

  public void init(MonthDescriptor month, List<List<MonthCellDescriptor>> cells,
      boolean displayOnly) {
    Logr.d("Initializing MonthView (%d) for %s", System.identityHashCode(this), month);
    long start = System.currentTimeMillis();
    title.setText(month.getLabel());

    final int numRows = cells.size();
    grid.setNumRows(numRows);
    for (int i = 0; i < 6; i++) {
      CalendarRowView weekRow = (CalendarRowView) grid.getChildAt(i + 1);
      weekRow.setListener(listener);
      if (i < numRows) {
        weekRow.setVisibility(VISIBLE);
        List<MonthCellDescriptor> week = cells.get(i);
        for (int c = 0; c < week.size(); c++) {
          MonthCellDescriptor cell = week.get(c);
          //FrameLayout layout=(FrameLayout) weekRow.getChildAt(c);
         // CalendarCellView cellView=(CalendarCellView) layout.getChildAt(0);
          CalendarCellView cellView = (CalendarCellView) weekRow.getChildAt(c);
         
          cellView.setText(Integer.toString(cell.getValue()));
          cellView.setEnabled(cell.isCurrentMonth());
          cellView.setClickable(!displayOnly);
          
          if(cell.getValue()==1) reachTofirst=!reachTofirst;
          if(reachTofirst)
          {
        	  data.add(cellView);
          }
          //
          //secret is here
         /* BadgerImageView bdimg=new BadgerImageView(cellView.getContext(),cellView);
          bdimg.setImageResource(R.drawable.right);
          bdimg.show();*/
           
          cellView.setSelectable(cell.isSelectable());
          cellView.setSelected(cell.isSelected());
          cellView.setCurrentMonth(cell.isCurrentMonth());
          cellView.setToday(cell.isToday());
          cellView.setRangeState(cell.getRangeState());
          cellView.setHighlighted(cell.isHighlighted());
          cellView.setTag(cell);
        }
      } else {
        weekRow.setVisibility(GONE);
      }
    }
    Logr.d("MonthView.init took %d ms", System.currentTimeMillis() - start);
  }

  public void setDividerColor(int color) {
    grid.setDividerColor(color);
  }

  public void setDayBackground(int resId) {
    grid.setDayBackground(resId);
  }

  public void setDayTextColor(int resId) {
    grid.setDayTextColor(resId);
  }

  public void setTitleTextColor(int color) {
    title.setTextColor(color);
  }

  public void setHeaderTextColor(int color) {
    grid.setHeaderTextColor(color);
  }

  public interface Listener {
    void handleClick(MonthCellDescriptor cell);
  }
  public List<CalendarCellView> getCellList()
  {
	  return data;
  }
  public CalendarCellView getByDay(int day)
  {
	  return data.get(day-1);
  }
}
