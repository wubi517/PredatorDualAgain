package com.it_tech613.predator.ui.record;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.it_tech613.predator.R;
import com.it_tech613.predator.models.RecordingModel;

import java.util.List;

/**
 * Created by RST on 2/26/2017.
 */

public class RecordingListAdapter extends BaseAdapter {

    Context context;
    List<RecordingModel> datas;
    LayoutInflater inflater;
    int selected_pos;
    TextView txt_name;
    TextView txt_date;
    TextView txt_time;
    TextView txt_size;

    public RecordingListAdapter(Context context, List<RecordingModel> datas) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.item_record_list, viewGroup, false);
        }
        txt_date = view.findViewById(R.id.txt_date);
        txt_name = view.findViewById(R.id.txt_name);
        txt_time = view.findViewById(R.id.txt_time);
        txt_size = view.findViewById(R.id.txt_size);
        RecordingModel model = datas.get(i);
        txt_name.setText(model.getName().substring(14));
        txt_date.setText("Date: "+model.getDate());
        txt_time.setText("Time: "+model.getTime());
        txt_size.setText("Size: "+model.getSize()+"MB");
        return view;
    }
    public void selectItem(int pos) {
        selected_pos = pos;
        notifyDataSetChanged();
    }
}
