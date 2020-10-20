package com.blucor.steamersindia.techfragment;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.blucor.steamersindia.fragment.ApproveRequest;
import com.blucor.steamersindia.fragment.DashboardFragment;
import com.blucor.steamersindia.fragment.MyComplaints;
import com.blucor.steamersindia.main.DrawerActivity;
import com.blucor.steamersindia.model.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ReturnItems extends Fragment {

    //view
    View view;

    //custom loader
    CustomLoader loader;

    //return
    RecyclerView recyclerView;

    //Arraylist
    ArrayList<Model> mArrayList;


    //pref
    Preferences preferences;

    public  String complaint_no;
    public  String material_used;
    public  String material_required;
    public  String return_material;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.return_material, container, false);

        recyclerView=view.findViewById(R.id.recyclerView);


        mArrayList=new ArrayList<>();


        preferences=new Preferences(getActivity());


        DrawerActivity.tvHeaderText.setText("Return Item");

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

        if (Utils.isNetworkConnectedMainThred(getActivity())) {
            loader.show();
            String URL = getString(R.string.API_URL) + "getreturndetails.php";
            HitgetreturnitemApi(URL,preferences.get(Constants.id));
        } else {
            Toasty.warning(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

        return  view;
    }


    public void setAdapter(RecyclerView mRecyclerview,ArrayList<Model> arrayList)
    {
        mRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(),1));
        mRecyclerview.setAdapter(new ReturnAdapter(arrayList));
    }


    //*Recyclerview Adapter*//
    private class ReturnAdapter extends RecyclerView.Adapter<Holder> {
        ArrayList<Model> data = new ArrayList<>();

        int[] image;
        String[] array;

        public ReturnAdapter(ArrayList<Model> favList) {
            data = favList;
        }

        public ReturnAdapter() {
        }
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.return_items, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final Holder holder, final int position) {

            holder.tvcomplaintno.setText(data.get(position).getComplaint_no());


            if(data.get(position).getStatus().equalsIgnoreCase("1"))
            {

                holder.tvStatus.setText("Returned");
                holder.tvStatus.setTextColor(getResources().getColor(R.color.green_600));
            }
            else
            {
                holder.tvStatus.setText("Return Now");
                holder.tvStatus.setTextColor(getResources().getColor(R.color.red_900));
                holder.tvStatus.setPaintFlags(holder.tvStatus.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);


                holder.tvStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        complaint_no=data.get(position).getComplaint_no();
                        material_required=data.get(position).getMaterial_required();
                        material_used=data.get(position).getMaterial_used();
                        return_material=data.get(position).getReturn_material();
                        replaceFragmentWithAnimation(new ReturnDetails());
                    }
                });

            }


            holder.tvDate.setText(getDate(data.get(position).getTask_finish_date()));

            holder.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


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


        LinearLayout llMain;
        TextView tvDate;
        TextView tvcomplaintno;
        TextView tvStatus;

        public Holder(View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tvDate);
            tvcomplaintno = itemView.findViewById(R.id.tvcomplaintno);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            llMain = itemView.findViewById(R.id.llMain);
        }
    }


    private void HitgetreturnitemApi(String url, final String tech_id) {
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
                        JSONArray jsonArray=jsonObject.getJSONArray("Return");

                        for(int i=0;i<jsonArray.length();i++)
                        {

                            model=new Model();
                            JSONObject mJsonobject=jsonArray.getJSONObject(i);

                            Log.e("msss",mJsonobject.toString());




                                model.setComplaint_no(mJsonobject.getString("complaint_no"));
                                model.setStatus(mJsonobject.getString("return_accepted"));
                                model.setMaterial_required(mJsonobject.getString("material_required"));
                                model.setMaterial_used(mJsonobject.getString("material_used"));
                                model.setTask_finish_date(mJsonobject.getString("task_finish_date"));
                                model.setReturn_material(mJsonobject.getString("return_material"));



                            mArrayList.add(model);
                        }



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
                parms.put("tech_id",tech_id);
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

    private String getDate(String dt) {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");

        Date date = null;
        try {
            date = inputFormat.parse(dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String outputDateStr = outputFormat.format(date);

        return  outputDateStr;
    }


    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        Bundle args = new Bundle();
        args.putString("complaint_no", complaint_no);
        args.putString("material_required", material_required);
        args.putString("material_used", material_used);
        args.putString("return_material", return_material);
        fragment.setArguments(args);
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.commit();
    }
}
