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
import com.blucor.steamersindia.extra.Utils;
import com.blucor.steamersindia.fragment.DashboardFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ReturnDetails extends Fragment {




    //Edittext
    EditText etRequiredMaterial;
    EditText etMaterialUSed;
    EditText etReturnItems;


    //textview
    TextView tvReturnStatus;



    //Button
    Button btnSubmit;

    //View
    View view;


    //custom loader
    CustomLoader loader;




    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.return_details, container, false);

        etRequiredMaterial=view.findViewById(R.id.etRequiredMaterial);
        etMaterialUSed=view.findViewById(R.id.etMaterialUSed);
        etReturnItems=view.findViewById(R.id.etReturnItems);
        tvReturnStatus=view.findViewById(R.id.tvReturnStatus);

        btnSubmit=view.findViewById(R.id.btnSubmit);


        //set values
        etRequiredMaterial.setText(getArguments().getString("material_required"));
        etMaterialUSed.setText(getArguments().getString("material_used"));


        if(getArguments().getString("return_material").equals(""))
        {
            btnSubmit.setVisibility(View.VISIBLE);
            tvReturnStatus.setVisibility(View.GONE);
        }
        else
        {
            btnSubmit.setVisibility(View.GONE);
            tvReturnStatus.setVisibility(View.VISIBLE);
            etReturnItems.setText(getArguments().getString("return_material"));
            etReturnItems.setEnabled(false);
        }


        //loader
        loader = new CustomLoader(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);


        //Back
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        replaceFragmentWithAnimation(new ReturnItems());
                        return true;
                    }
                }
                return false;
            }
        });


        //onClick

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etReturnItems.getText().toString().trim().isEmpty())
                {
                    Toasty.warning(getActivity(), "Mention items", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    if (Utils.isNetworkConnectedMainThred(getActivity())) {
                        loader.show();
                        String URL = getString(R.string.API_URL) + "return_items.php";
                        HitReturnItemApi(URL,getArguments().getString("complaint_no"),etReturnItems.getText().toString());
                    } else {
                        Toasty.warning(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        return  view;

    }



    private void HitReturnItemApi(String url,final String complaint_no, final String return_material) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loader.cancel();
                Log.e("getReturn_response", response);
                try {
                    JSONObject object=new JSONObject(response);

                    if(object.getString("success").equalsIgnoreCase("1"))
                    {
                        Toasty.success(getActivity(), "Return Requested Successfullly", Toast.LENGTH_SHORT).show();
                        replaceFragmentWithAnimation(new ReturnItems());
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
                })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parms = new HashMap<String, String>();
                parms.put("complaint_no",complaint_no);
                parms.put("return_material",return_material);

                Log.e("params",""+parms);
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

    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.commit();
    }
}
