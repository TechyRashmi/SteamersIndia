package com.blucor.steamersindia.main;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blucor.steamersindia.R;
import com.blucor.steamersindia.extra.Constants;
import com.blucor.steamersindia.extra.Preferences;
import com.blucor.steamersindia.fragment.ApproveRequest;
import com.blucor.steamersindia.fragment.ComplaintsFragment;
import com.blucor.steamersindia.fragment.DashboardFragment;
import com.blucor.steamersindia.fragment.MyComplaints;
import com.blucor.steamersindia.fragment.PromotionsList;
import com.blucor.steamersindia.techfragment.AllotedRequest;
import com.blucor.steamersindia.techfragment.ReturnItems;
import com.blucor.steamersindia.techfragment.ServiceRequestFragment;
import com.google.android.material.navigation.NavigationView;

public class DrawerActivity extends AppCompatActivity implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener {

    //declare
    DrawerLayout drawer;
    ImageView iv_menu;
    NavigationView navigationView;


    RelativeLayout rlHome;

    public static TextView tvHeaderText;

    Preferences preferences;

    //other
    public static int backPressed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        preferences=new Preferences(this);




        //navigationview
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        Menu menu = navigationView.getMenu();
        View headerview = navigationView.getHeaderView(0);
        rlHome = headerview.findViewById(R.id.rlHome);


        if(preferences.get(Constants.role).equalsIgnoreCase("user"))
        {
            menu.findItem(R.id.first).setVisible(true);
            menu.findItem(R.id.second).setVisible(true);
            menu.findItem(R.id.third).setVisible(true);


            menu.findItem(R.id.servicerequest).setVisible(false);
            menu.findItem(R.id.allotedrequest).setVisible(false);
            menu.findItem(R.id.returnMaterial).setVisible(false);

        }
        else
        {
            menu.findItem(R.id.first).setVisible(false);
            menu.findItem(R.id.second).setVisible(false);
            menu.findItem(R.id.third).setVisible(false);


            menu.findItem(R.id.servicerequest).setVisible(true);
            menu.findItem(R.id.allotedrequest).setVisible(true);
            menu.findItem(R.id.returnMaterial).setVisible(true);
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        iv_menu=findViewById(R.id.iv_menu);

        tvHeaderText=findViewById(R.id.tvHeaderText);

        tvHeaderText.setText("Steamers India");

        //Load dashboard
        replaceFragmentWithAnimation(new DashboardFragment());

        //onclick listener
        iv_menu.setOnClickListener(this);
        rlHome.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }
    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.iv_menu:
                drawer.openDrawer(Gravity.LEFT);
                break;

            case R.id.rlHome:
                replaceFragmentWithAnimation(new DashboardFragment());
              drawer.closeDrawers();
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.second) {
            replaceFragmentWithAnimation( new ComplaintsFragment());
        }
        else if(id==R.id.third)
        {
            replaceFragmentWithAnimation( new MyComplaints());
        }
        else if(id==R.id.fourth)
        {
            logout();
        }
        else if(id ==R.id.first)
        {
            replaceFragmentWithAnimation( new PromotionsList());
        }

  else if(id ==R.id.help)
        {
            CallNowPopup();
        }

        else if(id ==R.id.allotedrequest)
        {
            replaceFragmentWithAnimation(new AllotedRequest());
        }

        else if(id ==R.id.servicerequest)
        {
            replaceFragmentWithAnimation(new ServiceRequestFragment());
        }

        else if(id ==R.id.returnMaterial)
        {
            replaceFragmentWithAnimation(new ReturnItems());
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void CallNowPopup() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.callnowpopup);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


        ImageView ivClose=dialog.findViewById(R.id.ivClose);
        ImageView ivCallNow=dialog.findViewById(R.id.ivCallNow);
//        TextView tvOk=dialog.findViewById(R.id.tvOk);
//
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });


        ivCallNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                callPhoneNumber();
            }

        });
//
//        tvOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.cancel();
//            }
//        });

    }
    public void logout() {
        //Dialog
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alertyesno);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        //findId
        TextView tvYes = (TextView) dialog.findViewById(R.id.tvOk);
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tvcancel);
        TextView tvReason = (TextView) dialog.findViewById(R.id.textView22);
        TextView tvAlertMsg = (TextView) dialog.findViewById(R.id.tvAlertMsg);

        //set value
        tvAlertMsg.setText("Confirmation Alert..!!!");

        tvReason.setText("Are you sure want to logout?");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        //set listener
        tvYes.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DrawerActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_left);
                preferences.set(Constants.id, "");
                preferences.commit();

//                Toasty.success(DrawerActivity.this, "Logged out..!!!", Toast.LENGTH_SHORT).show();
                finishAffinity();
                dialog.dismiss();
            }
        });


        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void callPhoneNumber()
    {
        try
        {
            if(Build.VERSION.SDK_INT > 22)
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 101);

                    return;
                }

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + "8048764299"));
                startActivity(callIntent);

            }
            else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + "8048764299"));
                startActivity(callIntent);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults)
    {
        if(requestCode == 101)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                callPhoneNumber();
            }
            else
            {
                Log.e("TAG", "Permission not Granted");
            }
        }
    }


    @Override
    public void onBackPressed() {


        backPressed = backPressed + 1;
        if (backPressed == 1) {
            Toast.makeText(DrawerActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            new CountDownTimer(5000, 1000) { // adjust the milli seconds here
                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() { backPressed = 0;
                }
            }.start();
        }
        if (backPressed == 2) {
            backPressed = 0;
            finishAffinity();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}