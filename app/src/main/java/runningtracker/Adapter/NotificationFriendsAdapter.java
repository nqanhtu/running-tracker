package runningtracker.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import runningtracker.R;
import runningtracker.common.MyFirestore;
import runningtracker.data.model.Friend;

public class NotificationFriendsAdapter extends RecyclerView.Adapter<NotificationFriendsAdapter.ViewHolder> {


    private List<Friend> friends;
    private List<Friend> originalFriends;

    public List<Friend> getUpdatedFriends() {
        List<Friend> result = new ArrayList<>();
        for (int i = 0; i < this.friends.size(); i++) {

            Log.d("ABC", String.valueOf(friends.get(i).isNotify()) + " " + String.valueOf(originalFriends.get(i).isNotify()));

            if (friends.get(i).isNotify() != originalFriends.get(i).isNotify()) {
                result.add(friends.get(i));
            }
        }
        return friends;
    }

    public NotificationFriendsAdapter(List<Friend> friends) {


        this.originalFriends = friends;

        this.friends = new ArrayList<>(this.originalFriends);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @BindView(R.id.item_friend)
        LinearLayout item;
        @BindView(R.id.iconView)
        ImageView icon;
        @BindView(R.id.textView)
        TextView name;
        @BindView(R.id.imageView)
        ImageView avatar;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }


    @NonNull
    @Override
    public NotificationFriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_notification, parent, false);
        final ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = viewHolder.getAdapterPosition();
                Friend friend = friends.get(pos);

                if (friend.isNotify()) {
                    friend.setNotify(false);
                    friends.set(pos, friend);
                    viewHolder.icon.setImageResource(R.drawable.ic_uncheck_mark);
                } else {
                    friend.setNotify(true);
                    friends.set(pos, friend);
                    viewHolder.icon.setImageResource(R.drawable.ic_check_mark);
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationFriendsAdapter.ViewHolder holder, int position) {
        holder.name.setText(friends.get(position).getDisplayName());
        if (friends.get(position).isNotify()) {
            holder.icon.setImageResource(R.drawable.ic_check_mark);
        } else {
            holder.icon.setImageResource(R.drawable.ic_uncheck_mark);
        }
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }


}

