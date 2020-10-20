package com.blucor.steamersindia.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blucor.steamersindia.R;
import com.blucor.steamersindia.extra.CustomLoader;
import com.blucor.steamersindia.extra.Utils;
import com.blucor.steamersindia.main.DrawerActivity;
import com.blucor.steamersindia.techfragment.ServiceRequestFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ApproveRequest extends Fragment {

    //view
    View view;

    //textview
    TextView tvComplaintid;
    TextView tvName;
    TextView tvDesignation;
    TextView tvContact;
    TextView tvAmount;
    TextView tvAccepted;


    //button
    Button btnSubmit;



    //custom loader
    CustomLoader loader;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.approverequest, container, false);

        //loader
        loader = new CustomLoader(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        tvComplaintid=view.findViewById(R.id.tvComplaintid);
        tvName=view.findViewById(R.id.tvName);
        tvDesignation=view.findViewById(R.id.tvDesignation);
        tvContact=view.findViewById(R.id.tvContact);
        tvAmount=view.findViewById(R.id.tvAmount);
        tvAccepted=view.findViewById(R.id.tvAccepted);
        btnSubmit=view.findViewById(R.id.btnSubmit);


        DrawerActivity.tvHeaderText.setText("Service Requests");

        //Back
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {

                        replaceFragmentWithAnimation(new DashboardFragment());

                        return true;
                    }
                }
                return false;
            }
        });


        if (Utils.isNetworkConnectedMainThred(getActivity())) {
            loader.show();
            String URL = getString(R.string.API_URL) + "getcomplaintdetails.php";
            HitComplaintDetailsApi(URL,getArguments().getString("complaint_no"));
        } else {
            Toasty.warning(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }


        //onclick
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isNetworkConnectedMainThred(getActivity())) {
                    loader.show();
                    String URL = getString(R.string.API_URL) + "customer_approve_request.php";
                    HitApproverequestApi(URL,getArguments().getString("complaint_no"));
                } else {
                    Toasty.warning(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return  view;
    }

    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.commit();
    }


    //***********************API*****************************************//
    private void HitComplaintDetailsApi(String url, final String complaint_no) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loader.cancel();
                Log.e("getComplaints_response", response);

                try {
                    JSONArray jsonArray =new JSONArray(response);

                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        tvName.setText(jsonObject.getString("fullname"));
                        tvContact.setText(jsonObject.getString("mobileno"));
                        tvAmount.setText("\u20b9" + jsonObject.getString("estimate_bill"));
                        String tech_id=jsonObject.getString("tech_id");
                        tvContact.setText(jsonObject.getString("mobileno"));
                        tvComplaintid.setText("Complaint no"+jsonObject.getString("complaint_no"));
                        if(jsonObject.getString("customer_approved").equalsIgnoreCase("yes"))
                        {
                            btnSubmit.setVisibility(View.GONE);
                            tvAccepted.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            btnSubmit.setVisibility(View.VISIBLE);
                            tvAccepted.setVisibility(View.GONE);
                        }

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
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parms = new HashMap<String, String>();
                parms.put("complaint_no",complaint_no);
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

    private void HitApproverequestApi(String url, final String complaint_no) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loader.cancel();
                Log.e("getComplaints_response", response);
                try {
                    JSONObject object=new JSONObject(response);

                    if(object.getString("success").equalsIgnoreCase("1"))
                    {
                        Toasty.success(getActivity(), "Request Approved Successfully", Toast.LENGTH_SHORT).show();
                        replaceFragmentWithAnimation(new MyComplaints());
                    }
                    else
                    {
                        Toasty.error(getActivity(), "Some error occured", Toast.LENGTH_SHORT).show();
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
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parms = new HashMap<String, String>();
                parms.put("complaint_no",complaint_no);

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

}
