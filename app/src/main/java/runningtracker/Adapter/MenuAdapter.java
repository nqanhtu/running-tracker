package runningtracker.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import runningtracker.model.modelrunning.MenuObject;
import runningtracker.R;

public class MenuAdapter extends BaseAdapter{

    private Context context;
    private int layout;
    private List<MenuObject> menuObjectList;

    public MenuAdapter(Context context, int layout, List<MenuObject> menuObjectList) {
        this.context = context;
        this.layout = layout;
        this.menuObjectList = menuObjectList;
    }

    @Override
    public int getCount() {
        return menuObjectList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(layout,null);
        TextView txtName = view.findViewById(R.id.txtName);
        ImageView imageItem = view.findViewById(R.id.imageItem);

        MenuObject menuObject  = menuObjectList.get(i);
        txtName.setText(menuObject.getDescription());
        imageItem.setImageResource(menuObject.getImage());
        return view;
    }
}
