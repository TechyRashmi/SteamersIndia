package com.blucor.steamersindia.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.blucor.steamersindia.R;

public class SignupActivity extends AppCompatActivity {


    LinearLayout llMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        llMain=findViewById(R.id.llMain);

        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        llMain.startAnimation(fadeInAnimation);
    }
}