package com.blucor.steamersindia;

import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import androidx.appcompat.app.AppCompatActivity;

import com.blucor.steamersindia.extra.Constants;
import com.blucor.steamersindia.extra.Preferences;
import com.blucor.steamersindia.main.DrawerActivity;
import com.blucor.steamersindia.main.LoginActivity;


public class SplashActivity extends AppCompatActivity {

    LinearLayout descimage,desctxt;
    Animation uptodown,downtoup;

    ImageView mLogo;


    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mLogo=findViewById(R.id.imageView2);


        preferences=new Preferences(this);


        descimage = (LinearLayout) findViewById(R.id.titleimage);
        desctxt = (LinearLayout) findViewById(R.id.titletxt);
        uptodown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this,R.anim.downtoup);

        descimage.setAnimation(downtoup);
        desctxt.setAnimation(uptodown);



        RotateAnimation rotate = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(3000);
        rotate.setInterpolator(new LinearInterpolator());
        mLogo.startAnimation(rotate);



        Thread myThread = new Thread(){
            @Override
            public void run(){
                try {
                    sleep(4000);

                    if(preferences.get(Constants.id).isEmpty())
                    {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(getApplicationContext(), DrawerActivity.class);
                        startActivity(intent);
                    }

                  //  finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        myThread.start();
    }


    }





