package com.blucor.steamersindia.techfragment;

import android.graphics.Typeface;
import android.os.Bundle;
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
import androidx.recyclerview.widget.DividerItemDecoration;
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
import com.blucor.steamersindia.fragment.DashboardFragment;
import com.blucor.steamersindia.fragment.MyComplaints;
import com.blucor.steamersindia.main.DrawerActivity;
import com.blucor.steamersindia.model.Model;
import com.blucor.steamersindia.model.Techmodel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static android.widget.LinearLayout.HORIZONTAL;

public class ServiceRequestFragment extends Fragment {

   //view
    View view;


    //recyclerview
    RecyclerView recyclerView;

    //custom loader
    CustomLoader loader;

    //pref
    Preferences pref;


    //Arraylist
    ArrayList<Techmodel> mArrayList;


    ArrayList<String> arrayList=new ArrayList<>();


    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static  String date_time;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.request_tech, container, false);


        //pref
        pref = new Preferences(getActivity());

        //find id
        recyclerView=view.findViewById(R.id.recyclerView);

        mArrayList=new ArrayList<>();


        //loader
        loader = new CustomLoader(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        DrawerActivity.tvHeaderText.setText("Service Requests");

        pref=new Preferences(getActivity());



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

            String URL = getString(R.string.API_URL) + "getServiceRequests.php";
            HitGetRequestApi(URL,pref.get(Constants.id));

        } else {
            Toasty.warning(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    public void replaceFragmentWithAnimation(Fragment fragment,String array,String complaint_no) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.main_fragment_container, fragment);
        Bundle bundle=new Bundle();
        bundle.putString("array",array);
        bundle.putString("complaint_no",complaint_no);
        fragment.setArguments(bundle);
        transaction.commit();
    }

    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.commit();
    }
    public void setAdapter(RecyclerView mRecyclerview,ArrayList<Techmodel> arrayList)
    {
        mRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(),1));
        mRecyclerview.setAdapter(new ComplaintAdapter(arrayList));
    }
    //*Recyclerview Adapter*//
    private class ComplaintAdapter extends RecyclerView.Adapter<Holder> {
        ArrayList<Techmodel> data = new ArrayList<>();

        int[] image;
        String[] array;

        public ComplaintAdapter(ArrayList<Techmodel> favList) {
            data = favList;
        }

        public ComplaintAdapter() {
        }

        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.newrequest, parent, false));

        }
        @Override
        public void onBindViewHolder(@NonNull final Holder holder, final int position) {

            //setvalue
            holder.tvAddress.setText(data.get(position).getAddress());
            holder.tvComplaintid.setText("Complaint no :" + data.get(position).getComplaintno());
            holder.tvProductname.setText("Product Name: "+data.get(position).getProductname());
            holder.tvName.setText(data.get(position).getCutomername());
            holder.tvDate.setText(data.get(position).getPref_date()+ "(between "+data.get(position).getPref_time() + ")");


            if(data.get(position).getStatus().equalsIgnoreCase("pending"))
            {
                if(data.get(position).getTech_accepted().equalsIgnoreCase("1"))
                {
                    holder.tvAllotedtime.setText("REQUEST ACCEPTED");
                    holder.tvAllotedtime.setTypeface(Typeface.DEFAULT_BOLD);
                    holder.tvAllotedtime.setTextColor(getResources().getColor(R.color.green_600));
                    holder.llmain.setEnabled(false);
                }
                else
                {
                    holder.tvAllotedtime.setText("Alloted "+String.valueOf(getTimeAgo(Long.parseLong(data.get(position).getAlloted_time()))));
                }
            }
            else
            {
                holder.tvAllotedtime.setText("Complaint Closed");
                holder.tvAllotedtime.setTypeface(Typeface.DEFAULT_BOLD);
                holder.tvAllotedtime.setTextColor(getResources().getColor(R.color.red_900));
                holder.llmain.setEnabled(false);
            }



            holder.llmain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    replaceFragmentWithAnimation(new AcceptRequest(),data.get(position).getComplaintarray(),data.get(position).getComplaintno());
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
        TextView tvComplaintid;
        TextView tvName;
        TextView tvAddress;
        TextView tvDate;
        TextView tvProductname;
        TextView tvAllotedtime;
        LinearLayout llmain;
        public Holder(View itemView) {
            super(itemView);
            tvComplaintid = itemView.findViewById(R.id.tvComplaintid);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAllotedtime = itemView.findViewById(R.id.tvAllotedtime);
            tvProductname = itemView.findViewById(R.id.tvProductname);

            llmain=itemView.findViewById(R.id.llmain);

        }
    }

    private void HitGetRequestApi(String url, final String tech_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                loader.cancel();
                Log.e("myrequest_response", response);

                try {
                    JSONArray jsonArray=new JSONArray(response);

                    Techmodel techmodel;

                    for(int i=0;i<jsonArray.length();i++)
                    {
                        techmodel =new Techmodel();
                        JSONObject object=jsonArray.getJSONObject(i);
                        techmodel.setProductname(object.getString("prod_name"));
                        techmodel.setComplaintno(object.getString("complaint_no"));
                        techmodel.setCutomername(object.getString("fullname"));
                        techmodel.setComplaintarray(object.getString("complaint_array"));
                        techmodel.setCoplaintdescription(object.getString("complaint_description"));
                        techmodel.setAddress(object.getString("address2") +" "+ object.getString("zipcode"));
                        techmodel.setPref_date(object.getString("pref_date"));
                        techmodel.setPref_time(object.getString("pref_time"));
                        techmodel.setAlloted_time(object.getString("tech_alloted_date"));
                        techmodel.setTech_accepted(object.getString("tech_accepted"));
                        techmodel.setStatus(object.getString("status"));

                        mArrayList.add(techmodel);

                    }

                    setAdapter(recyclerView,mArrayList);

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


    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;


        }
        // long now = getCurrentTime(ctx);
        long now = System.currentTimeMillis();

        if (time > now || time <= 0) {
            return null;
        }
        // TODO: localize
        final long diff = now - time;

        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS)
        {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            //mins = diff / MINUTE_MILLIS ;
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            if((diff/HOUR_MILLIS)==1)
            {
                return  "an hour ago";
            }
            else {
                return diff / HOUR_MILLIS + " hours ago";
            }
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return date_time;
        }    }



}
