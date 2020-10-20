package com.blucor.steamersindia.extra;

import android.app.Dialog;
import android.content.Context;

import com.blucor.steamersindia.R;


/**
 * Created by admin on 4/9/2016.
 */


public class CustomLoader extends Dialog {



    public CustomLoader(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public CustomLoader(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
      //  setContentView(R.layout.custom_dialog);
        setContentView(R.layout.loader);

      /*  new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                // Write your code here to update the UI.
                                showPopup();
                                startStop();
                            }
                        });
                    } catch (Exception e) {

                    }
                }
            }
        }).start();*/

    }

    public CustomLoader(Context context, boolean cancelable,
                        OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
    }

}




