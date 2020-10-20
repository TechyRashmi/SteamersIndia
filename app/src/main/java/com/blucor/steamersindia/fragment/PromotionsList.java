package com.blucor.steamersindia.fragment;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.blucor.steamersindia.model.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;


public class PromotionsList extends Fragment {

    View view;
    //custom loader
    CustomLoader loader;

    //recyclerview
    RecyclerView recyclerView;

    //arraylist
    ArrayList<Model> mArraylist = new ArrayList<>();

    //preferences
    Preferences pref;

    //Relative layout
    RelativeLayout rrMain;





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.promotion_list, container, false);


        //pref
        pref = new Preferences(getActivity());

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
                        replaceFragmentWithAnimation(new DashboardFragment());
                        return true;
                    }
                }
                return false;
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        rrMain = view.findViewById(R.id.rrMain);

        if (Utils.isNetworkConnectedMainThred(getActivity())) {
            loader.show();
            String URL = getString(R.string.API_URL) + "getpromotions.php";
            HitPromotionsApi(URL,pref.get(Constants.id));
        } else {
            Toasty.warning(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
        //http_request();
        return view;
    }

    //*Recyclerview Adapter*//
    private class PromotionAdapter extends RecyclerView.Adapter<Holder> {
        ArrayList<Model> data = new ArrayList();

        int[] image;
        String[] array;

        public PromotionAdapter(ArrayList<Model> promotions) {
            data = promotions;
        }

        public PromotionAdapter() {
        }

        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.promotions_items, parent, false));
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onBindViewHolder(@NonNull final Holder holder, final int position) {

            if (position % 2 == 0) {
                holder.llMain.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.red_600));
            }

            holder.tvDiscount.setText(data.get(position).getDiscount() + " % Discount");


            String[] separated = data.get(position).getDate().split(" ");



            String dt = getDate((separated[0]));
            holder.tvTime.setText("Valid until " + dt);


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
        TextView tvDiscount;
        TextView tvTime;

        public Holder(View itemView) {
            super(itemView);

            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            tvTime = itemView.findViewById(R.id.tvTime);
            llMain = itemView.findViewById(R.id.llMain);

        }
    }

    public void setAdapter(RecyclerView mRecyclerview, ArrayList<Model> arrayList) {
        mRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        mRecyclerview.setAdapter(new PromotionAdapter(arrayList));
    }


    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.commit();
    }


    private void HitPromotionsApi(String url, final String customer_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                loader.cancel();
                Log.e("Offer_response", response);

                try {
                    JSONObject object = new JSONObject(response);

                    Model model;
                    if (object.getString("Success").equalsIgnoreCase("true")) {
                        rrMain.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        JSONArray jsonArray = object.getJSONArray("offer");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            model = new Model();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            model.setDiscount(jsonObject.getString("discount"));
                            model.setDate(jsonObject.getString("expired_date"));
                            mArraylist.add(model);
                        }

                        setAdapter(recyclerView, mArraylist);

                    }
                    else
                    {
                        rrMain.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
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


}
