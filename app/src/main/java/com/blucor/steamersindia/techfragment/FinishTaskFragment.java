package com.blucor.steamersindia.techfragment;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.blucor.steamersindia.model.SpareModel;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public static List<String> checkedlist = new ArrayList<>();


    TextView tvComplaints;

    Button btnSubmit;

    EditText etRequiredMaterial;


    EditText etMaterialUSed;

    RecyclerView recyclerView;

    ArrayList<SpareModel> mArraylist=new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.finishtask, container, false);

        //loader
        loader = new CustomLoader(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        DrawerActivity.tvHeaderText.setText("Assigned Task");

        btnSubmit=view.findViewById(R.id.btnSubmit);

        tvComplaints=view.findViewById(R.id.tvComplaints);

        etRequiredMaterial=view.findViewById(R.id.etRequiredMaterial);
        etMaterialUSed=view.findViewById(R.id.etMaterialUSed);


        recyclerView=view.findViewById(R.id.recyclerView);

        pref=new Preferences(getActivity());

        if (Utils.isNetworkConnectedMainThred(getActivity())) {
            loader.show();
            String URL = getString(R.string.API_URL) + "required_material.php";
            HitGetMaterialAPI(URL,"82623203");

        } else {
            Toasty.warning(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

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

                   Log.e("cheklist",""+checkedlist);
//                if(etMaterialUSed.getText().toString().trim().equalsIgnoreCase(""))
//                {
//                    Toasty.warning(getActivity(), "Enter material used", Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                    if (Utils.isNetworkConnectedMainThred(getActivity())) {
//                        loader.show();
//                        String URL = getString(R.string.API_URL) + "finish_task.php";
//                        HitFinishTaskApi(URL,getArguments().getString("complaint_no"),etMaterialUSed.getText().toString());
//                    } else {
//                        Toasty.warning(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
//                    }
//                }
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

    private void HitGetMaterialAPI(String url, final String complaint_no) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loader.cancel();
                Log.e("getTask_response", response);
                try {
                    mArraylist.clear();
                    SpareModel spareModel;
                   JSONArray jsonArray=new JSONArray(response);
                   for(int i=0;i<jsonArray.length();i++)
                   {
                       spareModel=new SpareModel();
                       JSONObject jsonObject=jsonArray.getJSONObject(i);
                       spareModel.setSpare_id(jsonObject.getString("spare_id"));
                       spareModel.setCapacity(jsonObject.getString("capacity"));
                       spareModel.setSize(jsonObject.getString("size"));
                       spareModel.setSpare_name(jsonObject.getString("spare_name"));
                       spareModel.setUpdated_quantity(jsonObject.getString("updated_quantity"));
                       mArraylist.add(spareModel);
                   }
                    setAdapter(recyclerView,mArraylist);

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

    public void setAdapter(RecyclerView mRecyclerview,ArrayList<SpareModel> mArraylist)
    {
        mRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(),1));
        mRecyclerview.setAdapter(new ComplaintAdapter(mArraylist));
    }

    //*Recyclerview Adapter*//
    private class ComplaintAdapter extends RecyclerView.Adapter<Holder> {
        ArrayList<SpareModel> data = new ArrayList<>();

        int[] image;
        String[] array;

        public ComplaintAdapter(ArrayList<SpareModel> favList) {
            data = favList;
        }
        public ComplaintAdapter() {
        }
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.spare_items, parent, false));
        }
        @Override
        public void onBindViewHolder(@NonNull final Holder holder, final int position) {

            holder.tvCapacity.setText(data.get(position).getCapacity());
            holder.tvPartName.setText(data.get(position).getSpare_name());
            holder.tvSize.setText(data.get(position).getSize());
            holder.tvQty.setText(data.get(position).getUpdated_quantity());



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
        TextView tvPartName;
        TextView tvCapacity;
        TextView tvSize;
        TextView tvQty;
        EditText etQty;

        public Holder(View itemView) {
            super(itemView);

            tvPartName = itemView.findViewById(R.id.tvPartName);
            tvCapacity = itemView.findViewById(R.id.tvCapacity);
            tvSize = itemView.findViewById(R.id.tvSize);
            tvQty = itemView.findViewById(R.id.tvQty);
            etQty=itemView.findViewById(R.id.etQty);

        }
    }

}
