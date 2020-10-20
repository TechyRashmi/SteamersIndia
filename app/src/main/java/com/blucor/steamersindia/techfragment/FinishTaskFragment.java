package com.blucor.steamersindia.techfragment;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.blucor.steamersindia.extra.Constants;
import com.blucor.steamersindia.extra.CustomLoader;
import com.blucor.steamersindia.extra.Preferences;
import com.blucor.steamersindia.extra.Utils;
import com.blucor.steamersindia.fragment.DashboardFragment;
import com.blucor.steamersindia.main.DrawerActivity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class FinishTaskFragment extends Fragment {

    //pref
    Preferences pref;

    //view
    View view;

    //custom loader
    CustomLoader loader;

    ArrayList<String> arrayList=new ArrayList<>();


    TextView tvComplaints;

    Button btnSubmit;

    EditText etRequiredMaterial;


    EditText etMaterialUSed;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.finishtask, container, false);

        //loader
        loader = new CustomLoader(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        DrawerActivity.tvHeaderText.setText("Assigned Task");

        btnSubmit=view.findViewById(R.id.btnSubmit);

        tvComplaints=view.findViewById(R.id.tvComplaints);

        etRequiredMaterial=view.findViewById(R.id.etRequiredMaterial);
        etMaterialUSed=view.findViewById(R.id.etMaterialUSed);

        pref=new Preferences(getActivity());

        //
        etRequiredMaterial.setText(getArguments().getString("Material"));



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

        try {
            JSONArray jsonArray =new JSONArray(getArguments().getString("array"));

            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject object=jsonArray.getJSONObject(i);

                if(object.getString("Answer").equalsIgnoreCase("Yes"))
                {
                    arrayList.add(object.getString("complaint"));
                }

                StringBuilder builder = new StringBuilder();
                int index=0;
                for (String details : arrayList) {
                    index++;
                    builder.append(index +") "+details + "\n");
                }
                tvComplaints.setText(builder.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Back
        view.setFocusableInTouchMode(true);
        view.requestFocus();

        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        replaceFragmentWithAnimation(new AllotedRequest());
                        return true;
                    }
                }
                return false;
            }
        });





        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etMaterialUSed.getText().toString().trim().equalsIgnoreCase(""))
                {
                    Toasty.warning(getActivity(), "Enter material used", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (Utils.isNetworkConnectedMainThred(getActivity())) {
                        loader.show();
                        String URL = getString(R.string.API_URL) + "finish_task.php";
                        HitFinishTaskApi(URL,getArguments().getString("complaint_no"),etMaterialUSed.getText().toString());
                    } else {
                        Toasty.warning(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        return view;
    }

    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.commit();
    }

    private void HitFinishTaskApi(String url, final String complaint_no,final String material_used) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loader.cancel();
                Log.e("getComplaints_response", response);
                try {
                    JSONObject object=new JSONObject(response);

                    if(object.getString("success").equalsIgnoreCase("1"))
                    {
                        Toasty.success(getActivity(), "Finish Task Successfully", Toast.LENGTH_SHORT).show();

                        replaceFragmentWithAnimation(new AllotedRequest());
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
                parms.put("material_used",material_used);
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
