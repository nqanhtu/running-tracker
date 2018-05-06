package runningtracker.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import runningtracker.R;
import runningtracker.data.model.User;

/**
 * Created by Anh Tu on 2/27/2018.
 */

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.MyViewHolder> {
    private static final String TAG = "FriendRequestsAdapter";
    private List<User> mFriendsList;
    private Dialog mDialog;
    private Context mContext;
    FirebaseFirestore db;
    FirebaseUser currentUser;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @BindView(R.id.item_friend)
        RelativeLayout item;
        @BindView(R.id.display_name_text_view)
        TextView displayName;
        @BindView(R.id.email_text_view)
        TextView email;

        MyViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public FriendRequestsAdapter(Context mContext, List<User> mFriendsList) {
        this.mContext = mContext;
        this.mFriendsList = mFriendsList;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public FriendRequestsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                 final int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(v);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDialog = new Dialog(mContext);
        mDialog.setContentView(R.layout.dialog);
        Objects.requireNonNull(mDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        viewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView dialogName = mDialog.findViewById(R.id.name_textview);
                TextView dialogEmail = mDialog.findViewById(R.id.email_textview);
                Button buttonAccept = mDialog.findViewById(R.id.accept_button);
                Button buttonReject = mDialog.findViewById(R.id.reject_button);

                final User friend = mFriendsList.get(viewHolder.getAdapterPosition());
                dialogName.setText(friend.getDisplayName());
                dialogEmail.setText(friend.getEmail());
                mDialog.show();
                buttonAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.collection("users").document(currentUser.getUid())
                                .collection("friends").document(friend.getUid()).set(friend)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(mContext, "Đã đồng ý lời mời kết bạn", Toast.LENGTH_SHORT).show();


                                    }
                                });

                        db.collection("users").document(friend.getUid())
                                .collection("friends").document(currentUser.getUid()).set(currentUser)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {


                                    }
                                });
                        db.collection("users").document(currentUser.getUid())
                                .collection("friendRequests").document(friend.getUid()).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error deleting document", e);
                                    }
                                });

                        db.collection("users").document(friend.getUid())
                                .collection("friends").document(currentUser.getUid()).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error deleting document", e);
                                    }
                                });

                    }
                });
                buttonReject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, "Đã hủy lời mời kết bạn", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.displayName.setText(mFriendsList.get(position).getDisplayName());
        holder.email.setText(mFriendsList.get(position).getEmail());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mFriendsList.size();
    }

}
