package com.mahendran_sakkarai.camera.profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.mahendran_sakkarai.camera.R;
import com.mahendran_sakkarai.camera.utils.AppUtil;

import java.io.File;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle bundle = getIntent().getExtras();
        ImageView imageView = (ImageView)findViewById(R.id.profile_pic);
        String imageLocation = null;
        if (bundle != null) {
            imageLocation = bundle.getString(AppUtil.PICTURE_LOCATION);
        }

        if (imageLocation != null) {
            File imageFile = new File(imageLocation);
            if (imageFile.exists()) {
                Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                imageView.setImageBitmap(imageBitmap);
            }
        }
    }
}
