package runningtracker.registerinfomation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import runningtracker.R;

import static android.app.Activity.RESULT_OK;

public class RegisterInformationFragment extends Fragment {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private final int PICK_IMAGE_REQUEST = 71;
    @BindView(R.id.profile_image_view)
    CircleImageView profileImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_register_information, container, false);
        ButterKnife.bind(this, view);
        setUpToolbar();
        return view;
    }

    @OnClick(R.id.open_image_button)
    public void openImage() {
        chooseImage();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filepath = data.getData();
            //  Picasso.get().load(filepath).resize(150,150).centerCrop().into(profileImageView);
            Glide
                    .with(this)
                    .load(filepath)
                    .into(profileImageView);

        }
    }

    private void setUpToolbar() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        toolbar.setTitle("Cập nhật thông tin tài khoản");
    }

}