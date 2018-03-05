package runningtracker.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import runningtracker.R;
import runningtracker.data.model.Option;

/**
 * Created by Anh Tu on 2/12/2018.
 */

public class OptionAdapter extends BaseAdapter {


    private Context mContext;
    private List<Option> mOptionList;

    private class ViewHolder{
        ImageView imageView;
        TextView textView;
    }

    public OptionAdapter(Context mContext, List<Option> mOptionList) {
        this.mContext = mContext;
        this.mOptionList = mOptionList;
    }

    public int getCount() {
        return mOptionList.size();
    }

    public Option getItem(int i) {
        return mOptionList.get(i);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (view==null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.gridview_dashboard_item,null);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.iconOption);
            viewHolder.textView = (TextView) view.findViewById(R.id.textViewOption);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Option option = mOptionList.get(i);
        viewHolder.textView.setText(option.getDescription());
        viewHolder.imageView.setImageResource(option.getImage());
        return view;
    }


}
