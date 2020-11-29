package com.blucor.steamersindia.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.blucor.steamersindia.R;


public abstract class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private Context mContext;
    private float mBaseElevation;
    public View.OnClickListener clickListener;

    int [] array;

    protected abstract void onCategoryClick(View view, String str);

    public CardPagerAdapter(Context context,int[] resources) {
        mContext = context;
        this.array=resources;
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCategoryClick(v, String.valueOf(v.getTag()));
            }
        };
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.adapter, container, false);
        container.addView(view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);
        ImageView iv = (ImageView) view.findViewById(R.id.iv);
        if (this.mBaseElevation == 0.0f) {
            this.mBaseElevation = cardView.getCardElevation();
        }

//        Picasso.with(this.mContext).load((String) ((HashMap) this.arraylist.get(position)).get("BannerImage")).into(iv);

        iv.setImageDrawable(mContext.getResources().getDrawable(array[position]));

        cardView.setMaxCardElevation(this.mBaseElevation * ((float) this.array.length));

        view.setTag(position);
        view.setOnClickListener(clickListener);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public float getBaseElevation() {
        return 0;
    }

    @Override
    public CardView getCardViewAt(int i) {
        return null;
    }

    @Override
    public int getCount() {
        return array.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


}
