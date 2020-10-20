package com.blucor.steamersindia.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.blucor.steamersindia.main.DrawerActivity;
import com.blucor.steamersindia.model.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class MyComplaints extends Fragment {

    //view
    View view;

    //recyclerview
    RecyclerView recyclerView;

    //custom loader
    CustomLoader loader;

    //pref
    Preferences pref;


    //Arraylist
    ArrayList<Model> mArrayList;


    ArrayList<String> arrayList=new ArrayList<>();


    Boolean isBillGenerated;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mycomplaint, container, false);
        DrawerActivity.tvHeaderText.setText("My Complaints");


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
        //loader
        loader = new CustomLoader(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        //find id
        recyclerView=view.findViewById(R.id.recyclerView);

        pref=new Preferences(getActivity());

        mArrayList=new ArrayList<>();

        if (Utils.isNetworkConnectedMainThred(getActivity())) {
            loader.show();
            String URL = getString(R.string.API_URL) + "getcomplaints.php";
            HitComplaintApi(URL,pref.get(Constants.id));
        } else {
            Toasty.warning(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }


        return view;

    }

    public void setAdapter(RecyclerView mRecyclerview,ArrayList<Model> arrayList)
    {
        mRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(),1));
        mRecyclerview.setAdapter(new ComplaintAdapter(arrayList));
    }
    //*Recyclerview Adapter*//
    private class ComplaintAdapter extends RecyclerView.Adapter<Holder> {
        ArrayList<Model> data = new ArrayList<>();

        int[] image;
        String[] array;

        public ComplaintAdapter(ArrayList<Model> favList) {
            data = favList;
        }

        public ComplaintAdapter() {
        }
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.complaint_items, parent, false));
        }
        @Override
        public void onBindViewHolder(@NonNull final Holder holder, final int position) {

            holder.tvComplaintno.setText(data.get(position).getComplaint_no());

            String[] splited = data.get(position).getComplaintdate().split("\\s+");
            holder.tvdate.setText(splited[0]);

            if(data.get(position).getStatus().equalsIgnoreCase("Pending"))
            {
                holder.tvStatus.setTextColor(getResources().getColor(R.color.red_900));
            }
            else
            {
                holder.tvStatus.setTextColor(getResources().getColor(R.color.green_600));
            }
            holder.tvStatus.setText(data.get(position).getStatus());


            final String description=data.get(position).getComplaintArray().replace("\\","");


            holder.llInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DescriptionPopup(data.get(position).getProductname(),description);
                }
            });


            holder.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if(data.get(position).getEstimate_bill().equalsIgnoreCase("0"))
                    {
                        Toasty.error(getActivity(), "Technician not Assigned yet", Toast.LENGTH_SHORT).show();

                    }

                    else
                    {
                        replaceFragmentWithAnimation(new ApproveRequest(),data.get(position).getComplaint_no());

                    }




                }
            });

        }
        public int getItemCount() {
            return data.size();
        }
        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

    private class Holder extends RecyclerView.ViewHolder {

        LinearLayout llInfo;
        LinearLayout llMain;
        TextView tvdate;
        TextView tvComplaintno;
        TextView tvStatus;

        public Holder(View itemView) {
            super(itemView);

            tvdate = itemView.findViewById(R.id.tvdate);
            tvComplaintno = itemView.findViewById(R.id.tvComplaintno);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            llInfo = itemView.findViewById(R.id.llInfo);
            llMain = itemView.findViewById(R.id.llMain);
        }
    }

    public void DiscountPopup() {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.discount_popup);
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
    }

    public void DescriptionPopup(String product_name,String array) {
        arrayList.clear();
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alertdialog);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView tvComplaints=dialog.findViewById(R.id.tvComplaints);
        TextView tvProductname=dialog.findViewById(R.id.tvProductname);
        TextView tvOk=dialog.findViewById(R.id.tvOk);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        try {
            JSONArray jsonArray=new JSONArray(array);
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                if(jsonObject.getString("Answer").equalsIgnoreCase("Yes"))
                {
                arrayList.add(jsonObject.getString("complaint"));
                }
            }
            tvProductname.setText("Product name :"+product_name);
            StringBuilder builder = new StringBuilder();
            int index=0;
            for (String details : arrayList) {
                index++;
                builder.append(index +") "+details + "\n");
            }
            tvComplaints.setText(builder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void HitComplaintApi(String url, final String customer_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                loader.cancel();
                Log.e("Complaints_response", response);
                mArrayList.clear();

                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.getString("Success").equalsIgnoreCase("true"))
                    {
                        Model model;
                       JSONArray jsonArray=jsonObject.getJSONArray("complaint");
                       for(int i=0;i<jsonArray.length();i++)
                       {
                           model=new Model();
                           JSONObject mJsonobject=jsonArray.getJSONObject(i);

                           model.setComplaint_description(mJsonobject.getString("complaint_description"));
                           model.setComplaint_no(mJsonobject.getString("complaint_no"));
                           model.setStatus(mJsonobject.getString("status"));
                           model.setComplaintdate(mJsonobject.getString("date"));
                           model.setProductname(mJsonobject.getString("prod_name"));
                           model.setComplaintArray(mJsonobject.getString("complaint_array"));



                           model.setEstimate_bill(mJsonobject.getString("estimate_bill"));


                           mArrayList.add(model);
                       }


                        Collections.reverseOrder();
                        setAdapter(recyclerView,mArrayList);
                    }
                    else
                    {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                HashMap<String,String> map;

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

    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.commit();
    }


    public void replaceFragmentWithAnimation(Fragment fragment,String complaint_no) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);

        Bundle bundle = new Bundle();
        bundle.putString("complaint_no", complaint_no);
        fragment.setArguments(bundle);
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.commit();
    }
}
