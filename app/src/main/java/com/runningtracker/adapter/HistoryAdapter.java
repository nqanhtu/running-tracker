package com.runningtracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import runningtracker.R;
import com.runningtracker.data.model.history.DateHistoryObject;

public class HistoryAdapter extends BaseAdapter{

    private List<DateHistoryObject> dateHistoryObjectList;
    private Context context;
    private int layout;

    public HistoryAdapter( Context context, int layout, List<DateHistoryObject> dateHistoryObjectList) {
        this.dateHistoryObjectList = dateHistoryObjectList;
        this.context = context;
        this.layout = layout;
    }

    private class ViewHolder{
        TextView txtDateHistory;
        TextView txtDateHistoryHide;
    }

    @Override
    public int getCount() {
        return dateHistoryObjectList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);

            viewHolder = new ViewHolder();

            /**
             * mapping view
             * */
            viewHolder.txtDateHistory = convertView.findViewById(R.id.txtDateHistory);
            viewHolder.txtDateHistoryHide = convertView.findViewById(R.id.txtDateHistoryHide);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        /**
         * Assign value
        * */
        DateHistoryObject dateHistoryObject = dateHistoryObjectList.get(position);
        viewHolder.txtDateHistory.setText(dateHistoryObject.getDateTime());
        viewHolder.txtDateHistoryHide.setText(dateHistoryObject.getDateTime());
        return convertView;
    }
}
