package com.it_tech613.predator.ui.liveTv;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.it_tech613.predator.R;
import com.it_tech613.predator.apps.Constants;

import java.text.ParseException;
import java.util.Calendar;


public class RecordingDlg extends Dialog {
    private String channel_name;
    private int duration;
    private LinearLayout ly_start;
    private TextView txt_start;
    private CheckBox checkBox;
    private boolean is_checked = false;
    public RecordingDlg(@NonNull Context context, String name, final RecordingDlg.DialogUpdateListener listener) {
        super(context);
        this.channel_name = name;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_record);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final EditText txt_name = (EditText)findViewById(R.id.txt_old_pass);
        final EditText txt_duration= (EditText)findViewById(R.id.txt_new_pass);
        txt_start = findViewById(R.id.txt_time);
        ly_start = findViewById(R.id.ly_start);
        checkBox = findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                is_checked = isChecked;
                if(isChecked){
                    ly_start.setVisibility(View.VISIBLE);
                }else {
                    ly_start.setVisibility(View.GONE);
                }
            }
        });
        ly_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showPickerDialog(false);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        txt_name.setText(channel_name);
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnUpdateSkipClick(RecordingDlg.this,null,0,null,is_checked);
            }
        });
        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_duration.getText().toString().isEmpty()){
                    Toast.makeText(context,"Duration can not be blank",Toast.LENGTH_SHORT).show();
                    return;
                }
                channel_name = txt_name.getText().toString();
                duration = Integer.parseInt(txt_duration.getText().toString());
                listener.OnUpdateNowClick(RecordingDlg.this,channel_name,duration,txt_start.getText().toString(),is_checked);
            }
        });
    }

    private void showPickerDialog(boolean is24HrView) throws ParseException {

        final Calendar myCalender = Calendar.getInstance();
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);
        CustomTimePickerDialog dialog = new CustomTimePickerDialog(getContext(),android.R.style.Theme_Holo_Light_Dialog_NoActionBar,mTimePickerListener, hour, minute, is24HrView, (dialog1, schedul_time) -> {
            dialog1.dismiss();
            txt_start.setText(schedul_time);
        });
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
//        dialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setVisibility(View.GONE);
//        dialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
    }


    private TimePickerDialog.OnTimeSetListener mTimePickerListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            txt_start.setText(Constants.GetCorrectFormatTime(hourOfDay,minute));
        }
    };

    public interface DialogUpdateListener {
        public void OnUpdateNowClick(Dialog dialog, String channel_name, int duration, String time, boolean is_checked);
        public void OnUpdateSkipClick(Dialog dialog, String channel_name, int duration, String time, boolean is_checked);
    }
}
