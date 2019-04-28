package com.example.sphtech.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sphtech.R;

import java.util.List;

public class YearAdapter extends ArrayAdapter<YearData> {

    private int resourceId;

    public YearAdapter(Context context, int textViewResourceId,
                       List<YearData> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        YearData yeardata = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.txtYear = (TextView) view.findViewById(R.id.txtYear);
            viewHolder.txtValue = (TextView) view.findViewById(R.id.txtValue);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.txtYear.setText(yeardata.getYear());
        viewHolder.txtValue.setText(yeardata.getValue());
        return view;
    }
    class ViewHolder {
        TextView txtYear;
        TextView txtValue;
    }

}