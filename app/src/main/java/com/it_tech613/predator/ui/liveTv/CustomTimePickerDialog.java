package com.it_tech613.predator.ui.liveTv;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TimePicker;

import androidx.annotation.NonNull;


import com.it_tech613.predator.apps.Constants;

import java.util.Calendar;

public class CustomTimePickerDialog extends TimePickerDialog {
    public static final int TIME_PICKER_INTERVAL = 1;
    private boolean mEventIgnored = false;
    private CenterClickListner listener;
    private int ihour=-1;
    private int imin=-1;
    public CustomTimePickerDialog(Context context, int theme,
                                  OnTimeSetListener callBack, int hourOfDay, int minute,
                                  boolean is24HourView, final CenterClickListner listener) {
        super(context, theme, callBack, hourOfDay, minute, is24HourView);
        this.listener = listener;
    }

    public CustomTimePickerDialog(Context context,
                                  OnTimeSetListener callBack, int hourOfDay, int minute,
                                  boolean is24HourView) {
        super(context, callBack, hourOfDay, minute, is24HourView);
    }
    @Override
    public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
        super.onTimeChanged(timePicker, hourOfDay, minute);
        if (!mEventIgnored) {
            minute = getRoundedMinute(minute);
            mEventIgnored = true;
            timePicker.setCurrentMinute(minute);
            mEventIgnored = false;
            ihour = hourOfDay;
            imin = minute;
        }
    }

    private int getRoundedMinute(int minute) {
        if (minute % TIME_PICKER_INTERVAL != 0) {
            int minuteFloor = minute - (minute % TIME_PICKER_INTERVAL);
            minute = minuteFloor
                    + (minute == minuteFloor + 1 ? TIME_PICKER_INTERVAL : 0);
            if (minute == 60)
                minute = 0;
        }
        return minute;
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_BACK:
                    if(ihour==-1 || imin == -1){
                        final Calendar myCalender = Calendar.getInstance();
                        ihour = myCalender.get(Calendar.HOUR_OF_DAY);
                        imin= myCalender.get(Calendar.MINUTE);
                    }
                    Log.e("picker_null", Constants.GetCorrectFormatTime(ihour,imin));
                    listener.OnCenterClick(CustomTimePickerDialog.this,Constants.GetCorrectFormatTime(ihour,imin));
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public interface CenterClickListner{
        public void OnCenterClick(TimePickerDialog dialog, String schedul_time);
    }

}
