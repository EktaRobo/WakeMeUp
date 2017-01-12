package com.example.ekta.tryout5;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;

/**
 * Created by ekta on 12/1/17.
 */

public class ServiceResultReceiver extends ResultReceiver {

    private Receiver mReceiver;

    public ServiceResultReceiver(Handler handler) {
        super(handler);
        // TODO Auto-generated constructor stub
    }

    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);

    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }

}
