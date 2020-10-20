package com.blucor.steamersindia.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.blucor.steamersindia.main.DrawerActivity;
import com.blucor.steamersindia.model.Model;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;


public class NewProductFragment extends Fragment {

    //Viewpager
    ViewPager mViewPager;

    //view
    View view;

    //Tablayout
    TabLayout tabLayout;

    //Int
    int currentPage = 0;

    //timer
    Timer timer;

    //long
    final long DELAY_MS = 1000;
    final long PERIOD_MS = 3000;


    //linear
    LinearLayout llMain;


    //Images
    private static final int[] mResources = {
            R.drawable.page1,
            R.drawable.page2,
            R.drawable.page3,
    };


    int[] images = {R.drawable.product1, R.drawable.product2, R.drawable.product3,R.drawable.product4,R.drawable.product5,R.drawable.product6};
    int[] images1 = {R.drawable.product5, R.drawable.product2, R.drawable.product1,R.drawable.product6};

    String[] array={"Spa Tub","Sauna Bath","Heater and Controller","Sauna Bath"};
    String[] array1={"Infrared Sauna Carbon Heater","Sauna Bath","Steam Bath","Shower Enclosure Wall To Wall","Jacuzzi Massage Bath Tub","Multifunction Steam & Shower Room"};

    //Arraylist
    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

    //Recyclerview
    RecyclerView recyclerView;
    RecyclerView productRecyclerView;


    //custom loader
    CustomLoader loader;


    //arraylist
    ArrayList<Model> mArraylist = new ArrayList<>();

    //preferences
    Preferences pref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.newproducts, container, false);

        //pref
        pref = new Preferences(getActivity());

        //loader
        loader = new CustomLoader(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        DrawerActivity.tvHeaderText.setText("New Products");

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

        mViewPager = view.findViewById(R.id.myviewpager);



        if (Utils.isNetworkConnectedMainThred(getActivity())) {
            loader.show();
            String URL = getString(R.string.API_URL) + "getProducts.php";
            HitPromotionsApi(URL);
        } else {
            Toasty.warning(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }


        //findingID
        findID(view);

        tabLayout.setupWithViewPager(mViewPager, true);
        final int NUM_PAGES = 4;
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES - 1) {
                    currentPage = 0;
                }
                mViewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);


        CardPagerAdapter mCardPagerAdapter =new CardPagerAdapter(getActivity(),mResources) {
            @Override
            protected void onCategoryClick(View view, String str) {

            }
        };


        mViewPager.setAdapter(mCardPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setClipToPadding(false);
        mViewPager.setCurrentItem(1, true);
        mViewPager.setPageMargin(10);

        return view;
    }

    public void findID(View v)
    {
        tabLayout = v.findViewById(R.id.tabDots);
        recyclerView =v.findViewById(R.id.recyclerView);
        productRecyclerView =v.findViewById(R.id.productRecyclerView);
        tabLayout.setupWithViewPager(mViewPager, true);
    }

    public void setAdapter(RecyclerView mRecyclerview)
    {
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        mRecyclerview.setAdapter(new ProductCategoryAdapter(images1,array));
    }

    public void setProductAdapter(RecyclerView mRecyclerview,ArrayList<Model> arrayList)
    {
        mRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(),2));
        mRecyclerview.setAdapter(new ProductAdapter(arrayList));
    }


    //*Recyclerview Adapter*//
    private class ProductCategoryAdapter extends RecyclerView.Adapter<Holder> {
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        int[] image;
        String[] array;

        public ProductCategoryAdapter(ArrayList<HashMap<String, String>> favList) {
            data = favList;
        }

        public ProductCategoryAdapter(int[] image,String[] array) {

            this.image=image;
            this.array=array;
        }

        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_items, parent, false));
        }
        @Override
        public void onBindViewHolder(@NonNull final Holder holder, final int position) {

            holder.tvCatName.setText(array[position]);
            // holder.image.setImageDrawable(getResources().getDrawable(image[position]));
            holder.rrMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
        public int getItemCount() {
            return array.length;
        }
        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

    private class Holder extends RecyclerView.ViewHolder {
        LinearLayout rrMain;
        ImageView image;
        TextView tvCatName;


        public Holder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            tvCatName = itemView.findViewById(R.id.tvCatName);
            rrMain = itemView.findViewById(R.id.rrMain);

        }
    }

    //*Recyclerview Product Adapter*//
    private class ProductAdapter extends RecyclerView.Adapter<MyHolder> {
        ArrayList<Model> data = new ArrayList();

        int[] image;
        String[] array;

        public ProductAdapter(ArrayList<Model> favList) {
            data = favList;
        }

        public ProductAdapter(int[] image,String[] array) {
            this.image=image;
            this.array=array;

        }

        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items, parent, false));
        }
        @Override
        public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {

            holder.tvProductname.setText(data.get(position).getProd_name());

            String url="http://example.i-tech.consulting/Steamers_India/upload/" + data.get(position).getProd_image();

            Glide.with(getActivity()).load(url).into(holder.ivProductimage);

          //  holder.ivProductimage.setImageDrawable(getResources().getDrawable(image[position]));

            holder.rrMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Popup(data.get(position).getProd_name(),data.get(position).getProd_description(),data.get(position).getProd_id());
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

    private class MyHolder extends RecyclerView.ViewHolder {
        LinearLayout rrMain;
        ImageView ivProductimage;
        TextView tvProductname;

        public MyHolder(View itemView) {
            super(itemView);
            ivProductimage = itemView.findViewById(R.id.ivProductimage);
            tvProductname = itemView.findViewById(R.id.tvProductname);
             rrMain = itemView.findViewById(R.id.rrMain);
        }
    }
    public void replaceFragmentWithAnimation(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.commit();
    }

    public void Popup(String name,String description, final String customer_id) {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.product_details);
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

        TextView tvProductDescription=dialog.findViewById(R.id.tvProductDescription);
        tvProductDescription.setText("Enriched with years of experience in the industry, we are engaged in offering "+ name +".");

        llMain=dialog.findViewById(R.id.llMain);

        ImageView ivClose=dialog.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });




        try {
            JSONArray array=new JSONArray(description);

            Log.e("array",array.toString());



            for(int i=0;i<array.length();i++)
            {
                JSONObject object=array.getJSONObject(i);

                LayoutInflater layoutInflater =
                        (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.product_desc, null);

                TextView tvSpecification=addView.findViewById(R.id.tvSpecification);
                TextView tvtext=addView.findViewById(R.id.tvtext);

                if(!object.getString("Specification").isEmpty()||!object.getString("Value").isEmpty())
                {
                    tvSpecification.setText(object.getString("Specification"));
                    tvtext.setText(object.getString("Value"));
                }


                llMain.addView(addView);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }




        TextView tvIntrested=dialog.findViewById(R.id.tvIntrested);
        tvIntrested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Utils.isNetworkConnectedMainThred(getActivity())) {
                    loader.show();
                    String URL = getString(R.string.API_URL) + "intrestproduct.php";
                    Hitintrestedapi(URL,pref.get(Constants.id),customer_id);
                } else {
                    Toasty.warning(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
                dialog.cancel();
            }
        });
    }

    private void HitPromotionsApi(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                loader.cancel();
                Log.e("product_response", response);

                try {
                    JSONArray array=new JSONArray(response);
                    Model model;
                    for(int i=0;i<array.length();i++)
                    {
                        model=new Model();
                        JSONObject jsonObject=array.getJSONObject(i);


                        model.setProd_id(jsonObject.getString("prod_id"));
                        model.setProd_name(jsonObject.getString("prod_name"));
                        model.setProd_image(jsonObject.getString("prod_image"));
                        model.setProd_model_no(jsonObject.getString("prod_model_no"));
                        model.setProd_description(jsonObject.getString("prod_description"));
                        mArraylist.add(model);
                    }
                    setProductAdapter(productRecyclerView,mArraylist);

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


    private void Hitintrestedapi(String url, final String product_id, final String customer_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                loader.cancel();
                Log.e("intrest_response", response);

                try {
                    JSONObject jsonObject=new JSONObject(response);

                    if(jsonObject.getString("success_msg").equalsIgnoreCase("1"))
                    {
                        Toasty.success(getActivity(), "Thanks for your intrest.We will share the Quotations & Details with you soon.", Toast.LENGTH_LONG, true).show();

                    }
                    else
                    {
                        Toasty.error(getActivity(), "Please try again", Toast.LENGTH_LONG, true).show();

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
                parms.put("customer_id",customer_id);
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
}
