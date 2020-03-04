package com.it_tech613.predator.ui.liveTv;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.it_tech613.predator.R;


/**
 * Created by RST on 3/23/2018.
 */

public class StopRecordingDlg extends Dialog {
    private TextView txt_dec;
    private String str_dec="";
    private Button btn_ok;
    private Button btn_cancel;
    public StopRecordingDlg(@NonNull Context context,final String dec,final StopRecordingDlg.DialogUpdateListener listener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_stop);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        str_dec = dec;
        txt_dec = findViewById(R.id.txt_dec);
        btn_ok = findViewById(R.id.btn_ok);
        btn_cancel = findViewById(R.id.btn_cancel);
        if(str_dec!=null && !str_dec.isEmpty()){
            txt_dec.setText(str_dec);
            btn_ok.setText("Play");
            btn_cancel.setText("Remove");
        }

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnUpdateNowClick(StopRecordingDlg.this);
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnUpdateSkipClick(StopRecordingDlg.this);
            }
        });


    }

    public interface DialogUpdateListener {
        public void OnUpdateNowClick(Dialog dialog);
        public void OnUpdateSkipClick(Dialog dialog);
    }
}
