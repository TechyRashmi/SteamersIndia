package com.blucor.steamersindia.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.blucor.steamersindia.main.DrawerActivity;
import com.blucor.steamersindia.model.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Phaser;

import es.dmoral.toasty.Toasty;

public class ComplaintsFragment extends Fragment {


    //declare
    Spinner spProducts;
    Spinner spTimeSlot;

    View view;




    Button btnSubmit;


    LinearLayout llContainer;

    EditText et_feedback;


    //custom loader
    CustomLoader loader;

    //pref
    Preferences pref;

    RelativeLayout rrDate;

    TextView tvDate;


   ArrayList<Model> mArraylist=new ArrayList<>();
   ArrayList<Spinner> spinnerArrayList;
   ArrayList<TextView> textViewArrayList;

   ArrayList<HashMap<String,String>> arrayList=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.complaints, container, false);


        findId();

        spinnerArrayList=new ArrayList<>();
        textViewArrayList=new ArrayList<>();


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


        rrDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int count=0;


                for(int i=0;i<spinnerArrayList.size();i++)
                {
                    HashMap<String,String> map=new HashMap<>();

                    if(spinnerArrayList.get(i).getSelectedItem().toString().equalsIgnoreCase("yes"))
                    {
                        count=1;
                        map.put("complaint",textViewArrayList.get(i).getText().toString());
                        map.put("Answer","Yes");
                    }
                    else
                    {

                        map.put("complaint",textViewArrayList.get(i).getText().toString());
                        map.put("Answer","No");
                    }
                    arrayList.add(map);

                }

                JSONArray array = new JSONArray(arrayList);

                Log.e("array",""+array);

                if(spProducts.getSelectedItemPosition()==0)
                {

                    Toasty.warning(getActivity(), "Select product", Toast.LENGTH_SHORT).show();
                }

               else if(count==0)
                {
                    Toasty.warning(getActivity(), "Select Atleast one problem", Toast.LENGTH_SHORT).show();

                }

               else if(tvDate.getText().toString().trim().equalsIgnoreCase("Select Date"))
                {
                    Toasty.warning(getActivity(), "Select Date", Toast.LENGTH_SHORT).show();

                }
               else if(spTimeSlot.getSelectedItemPosition()==0)
                {
                    Toasty.warning(getActivity(), "Select Time Slot", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    if (Utils.isNetworkConnectedMainThred(getActivity())) {
                        loader.show();

                        String URL = getString(R.string.API_URL) + "register_complaint.php";

                        String description="";
                        if(et_feedback.getText().toString().trim().equalsIgnoreCase(""))
                        {
                            description="NA";
                        }
                        else
                        {
                            description=et_feedback.getText().toString();
                        }
                        HitRegisterComplaintApi(URL,mArraylist.get(spProducts.getSelectedItemPosition()).getProductID(),pref.get(Constants.id),array.toString(),description,tvDate.getText().toString(),spTimeSlot.getSelectedItem().toString());

                    } else {
                        Toasty.warning(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });


        DrawerActivity.tvHeaderText.setText("Register Complaint");

        //loader
        loader = new CustomLoader(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        if (Utils.isNetworkConnectedMainThred(getActivity())) {
            loader.show();


            String url = getString(R.string.API_URL) + "getUserproducts.php";
            HitGetProductApi(url,pref.get(Constants.id));

        } else {
            Toasty.warning(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
        return view;

    }

    public void findId()
    {
        spProducts=view.findViewById(R.id.spProducts);
        spTimeSlot=view.findViewById(R.id.spTimeSlot);
        btnSubmit=view.findViewById(R.id.btnSubmit);
        llContainer=view.findViewById(R.id.llContainer);
        et_feedback=view.findViewById(R.id.et_feedback);
        rrDate=view.findViewById(R.id.rrDate);
        tvDate=view.findViewById(R.id.tvDate);
    }

    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.commit();
    }

    public class SpinnerAdapter extends ArrayAdapter<Model> {

        ArrayList<Model> list;

        public SpinnerAdapter(Context context, int textViewResourceId, ArrayList<Model> list) {

            super(context, textViewResourceId, list);

            this.list = list;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(R.layout.spinner_adapter, parent, false);
            TextView label = (TextView) row.findViewById(R.id.tvSpinnerText);
            //label.setTypeface(typeface3);
            label.setText(list.get(position).getProductName());
            return row;
        }
    }

    public void Popup(String complaint_no) {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.service_complaint_popup);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        ImageView ivClose=dialog.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                replaceFragmentWithAnimation(new DashboardFragment());
            }
        });

        TextView tvInfo=dialog.findViewById(R.id.tvInfo);

        Toasty.success(getActivity(), "Success", Toast.LENGTH_SHORT).show();

       // tvInfo.setText("We apologize for the inconvenience.Your service request no. " +complaint_no +".");
        tvInfo.setText("Dear Customer,your request no "+complaint_no +" is registered and will be attended shortly. ");
    }

    private void HitComplaintApi(String url, final String product_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                loader.cancel();
                Log.e("Complaint_response", response);

                HashMap<String,String> map;

                    try {
                    JSONArray jsonArray=new JSONArray(response);


                        //llContainer.removeView((View) addView.getParent());


                        if(jsonArray.length()!=0)
                    {

                        for(int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject object=jsonArray.getJSONObject(i);

                            //inflating layout dynamically


                            LayoutInflater layoutInflater =
                                    (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View addView = layoutInflater.inflate(R.layout.inflate, null);
                           // llContainer.removeAllViews();
                            TextView tvProblem=addView.findViewById(R.id.tvProblem);
                            tvProblem.setText(object.getString("check_list"));
                            Spinner spinner=addView.findViewById(R.id.spYesno);


                            llContainer.addView(addView);
                            spinnerArrayList.add(spinner);
                            textViewArrayList.add(tvProblem);
                        }
                    }
                    else
                    {
                       // llContainer.setVisibility(View.GONE);
                    Log.e("msg","No record found");
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
                parms.put("product_id",product_id);
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

    private void HitRegisterComplaintApi(String url, final String product_id, final String customer_id, final String complaint_array, final String complaint_description,final String pref_date,final String pref_time) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                loader.cancel();
                Log.e("Register_response", response);

                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.getString("success").equalsIgnoreCase("1"))
                    {
                        Popup(jsonObject.getString("complaint_no"));
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
                parms.put("product_id",product_id);
                parms.put("customer_id",customer_id);
                parms.put("complaint_array",complaint_array);
                parms.put("complaint_description",complaint_description);
                parms.put("pref_date",pref_date);
                parms.put("pref_time",pref_time);
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

    private void HitGetProductApi(String url, final String customer_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                loader.cancel();
                Log.e("Product_response", response);
                Model model=new Model();

                model.setProductID("0");
                model.setProductName("-Select-");
                mArraylist.add(model);

                try {
                    JSONObject object=new JSONObject(response);

                    if(object.getString("Success").equalsIgnoreCase("true"))
                    {
                        JSONArray array=object.getJSONArray("Product");
                        for(int i=0;i<array.length();i++)
                        {

                            model=new Model();
                            JSONObject obj=array.getJSONObject(i);
                            model.setProductID(obj.getString("prod_id"));
                            model.setProductName(obj.getString("prod_name"));
                            mArraylist.add(model);
                        }


                        spProducts.setAdapter(new SpinnerAdapter(getContext(),R.layout.spinner_adapter,mArraylist));

                        spProducts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if (Utils.isNetworkConnectedMainThred(getActivity())) {

                                   // loader.show();

                                   Log.e("testtt",""+llContainer.getChildCount());

                                      if(llContainer.getChildCount() > 0)
                                        llContainer.removeAllViews();


                               String URL = getString(R.string.API_URL) + "getComplaintById.php";
                                HitComplaintApi(URL,mArraylist.get(i) .getProductID());


                                } else {
                                    Toasty.warning(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

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

    final Calendar myCalendar = Calendar.getInstance();


    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };
    private void updateLabel() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        tvDate.setText(sdf.format(myCalendar.getTime()));
    }

}
