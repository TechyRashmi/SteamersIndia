package com.blucor.steamersindia.adapter.Volly;


import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by admin on 2/20/2017.
 */
public interface VolleyResult {

    public void volleySuccess(String requestType, JSONObject response);
    public void volleyError(String requestType, VolleyError error);
}
