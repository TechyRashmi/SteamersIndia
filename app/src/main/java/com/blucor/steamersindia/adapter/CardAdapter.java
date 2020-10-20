package com.blucor.steamersindia.adapter;

import androidx.cardview.widget.CardView;

public interface CardAdapter {
    public static final int MAX_ELEVATION_FACTOR = 8;

    float getBaseElevation();

    CardView getCardViewAt(int i);

    int getCount();
}
