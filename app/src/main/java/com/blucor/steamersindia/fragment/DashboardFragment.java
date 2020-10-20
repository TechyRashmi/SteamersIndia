package com.blucor.steamersindia.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blucor.steamersindia.R;
import com.blucor.steamersindia.adapter.CardPagerAdapter;
import com.blucor.steamersindia.extra.Constants;
import com.blucor.steamersindia.extra.CustomLoader;
import com.blucor.steamersindia.extra.Preferences;
import com.blucor.steamersindia.extra.Utils;
import com.blucor.steamersindia.main.LoginActivity;
import com.blucor.steamersindia.techfragment.AllotedRequest;
import com.blucor.steamersindia.techfragment.ReturnItems;
import com.blucor.steamersindia.techfragment.ServiceRequestFragment;
import com.google.android.material.tabs.TabLayout;
import com.hsalf.smileyrating.SmileyRating;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;


public class DashboardFragment extends Fragment  implements View.OnClickListener {


    //Linear layout
    LinearLayout llMain;
    LinearLayout llServiceComplaint;
    LinearLayout llNewOffer;
    LinearLayout llCustomerCare;
    LinearLayout llNewProduct;


    LinearLayout llNewServiceRequest;
    LinearLayout llAllotedRequest;
    LinearLayout llReturnItems;



    //view
    View view;


    //Pref
    Preferences pref;


    //custom loader
    CustomLoader loader;


    public  String complaint_no;

    Boolean isFeedback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.customer_dashboard, container, false);

        //getId
        findID();



        //loader
        loader = new CustomLoader(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        //pref
        pref=new Preferences(getActivity());




       //Setonclick
        llServiceComplaint.setOnClickListener(this);
        llNewOffer.setOnClickListener(this);
        llCustomerCare.setOnClickListener(this);
        llNewProduct.setOnClickListener(this);


         llNewServiceRequest.setOnClickListener(this);
         llAllotedRequest.setOnClickListener(this);
         llReturnItems.setOnClickListener(this);




        //check condition
        if(pref.get(Constants.role).equalsIgnoreCase("user"))
        {
             llServiceComplaint.setVisibility(View.VISIBLE);
             llNewOffer.setVisibility(View.VISIBLE);
             llCustomerCare.setVisibility(View.VISIBLE);
             llNewProduct.setVisibility(View.VISIBLE);

            llNewServiceRequest.setVisibility(View.GONE);
            llAllotedRequest.setVisibility(View.GONE);
            llReturnItems.setVisibility(View.GONE);


            if (Utils.isNetworkConnectedMainThred(getActivity())) {
                loader.show();
                String URL = getString(R.string.API_URL) + "checkfeedback.php";
                HitcheckpopupApi(URL,pref.get(Constants.id));
            } else {
                Toasty.warning(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            llServiceComplaint.setVisibility(View.GONE);
            llNewOffer.setVisibility(View.GONE);
            llCustomerCare.setVisibility(View.GONE);
            llNewProduct.setVisibility(View.GONE);

            llNewServiceRequest.setVisibility(View.VISIBLE);
            llAllotedRequest.setVisibility(View.VISIBLE);
            llReturnItems.setVisibility(View.VISIBLE);
        }


        return view;
    }




    public void findID()
    {
        llMain = view.findViewById(R.id.llMain);
        llServiceComplaint = view.findViewById(R.id.llServiceComplaint);
        llNewOffer = view.findViewById(R.id.llNewOffer);
        llCustomerCare = view.findViewById(R.id.llCustomerCare);
        llNewProduct = view.findViewById(R.id.llNewProduct);

        llNewServiceRequest = view.findViewById(R.id.llNewServiceRequest);
        llAllotedRequest = view.findViewById(R.id.llAllotedRequest);
        llReturnItems = view.findViewById(R.id.llReturnItems);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.llServiceComplaint:


                if(isFeedback.equals(true))
                {
                    FeedbackPopup(complaint_no);
                }
                else
                {
                    replaceFragmentWithAnimation(new ComplaintsFragment());
                    Animation();
                }



                break;

            case R.id.llNewProduct:
                replaceFragmentWithAnimation(new NewProductFragment());
                Animation();
                break;

            case R.id.llNewOffer:
                replaceFragmentWithAnimation(new PromotionsList());
                break;


            case R.id.llCustomerCare:
                CallNowPopup();
                break;


            case R.id.llNewServiceRequest:
                replaceFragmentWithAnimation(new ServiceRequestFragment());
                break;

                case R.id.llAllotedRequest:
                replaceFragmentWithAnimation(new AllotedRequest());
                break;

            case R.id.llReturnItems:
                replaceFragmentWithAnimation(new ReturnItems());
                break;
        }
    }


    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.commit();
    }

    public void CallNowPopup() {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
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

    public void FeedbackPopup(final String complaint_no) {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.feedbackpopup);
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

        final  SmileyRating smileyRating=dialog.findViewById(R.id.smile_rating);
        final EditText etComments=dialog.findViewById(R.id.etComments);
        Button btnSubmit=dialog.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               int rating = smileyRating.getSelectedSmiley().getRating();

                if (Utils.isNetworkConnectedMainThred(getActivity())) {
                    loader.show();
                    String URL = getString(R.string.API_URL) + "submitFeedback.php";
                    HitFeedbackApi(URL,pref.get(Constants.id),complaint_no,""+rating,etComments.getText().toString());
                    dialog.cancel();
                } else {
                    Toasty.warning(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView tvText=dialog.findViewById(R.id.tvText);
        tvText.setText("We hope your complaint no "+ complaint_no+ " has been resolved successfully.Kindy rate our service.");

        ImageView ivClose=dialog.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();

            }
        });

    }

    public void Animation()
    {
        getActivity().overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_left);

    }


    private void HitcheckpopupApi(String url,final String customer_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loader.cancel();
                Log.e("getPopupResponse", response);
                try {
                    JSONObject object=new JSONObject(response);

                    if(object.getString("Success").equalsIgnoreCase("true"))
                    {
                        isFeedback=true;
                        complaint_no=object.getString("complaint_no");
                        FeedbackPopup(complaint_no);
                    }
                    else
                    {
                        isFeedback=false;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loader.cancel();
                        Log.e("error", "" + error);

                    }
                })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parms = new HashMap<String, String>();
                parms.put("customer_id",customer_id);
                return parms;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                Log.e("volly_error", "" + error);
            }
        });
    }

    private void HitFeedbackApi(String url, final String customer_id, final String complaint_no, final String rating, final String comments) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                loader.cancel();
                Log.e("feedback_response", response);

                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.getString("success").equals("1"))
                    {
                        Toasty.success(getActivity(), "FeedBack submitted", Toast.LENGTH_SHORT).show();

                        replaceFragmentWithAnimation(new DashboardFragment());

                    }
                    else
                    {
                        Toasty.error(getActivity(), "some error occurred", Toast.LENGTH_SHORT).show();

                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loader.cancel();
                        Log.e("error", "" + error);

                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parms = new HashMap<String, String>();
                parms.put("complaint_no",complaint_no);
                parms.put("customer_id",customer_id);
                parms.put("rating",rating);
                parms.put("comments",comments);


                Log.e("paramss",""+parms);

                return parms;


            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                Log.e("volly_error", "" + error);
            }
        });
    }

    public void callPhoneNumber()
    {
        try
        {
            if(Build.VERSION.SDK_INT > 22)
            {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 101);

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

}