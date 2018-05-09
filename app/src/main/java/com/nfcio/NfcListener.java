package com.nfcio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.widget.Toast;

public class NfcListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //this will only be called when mainActivity is on top..
        if(intent.getAction().equals(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)){
            int state = intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, -1);
            Toast.makeText(context, "" + state, Toast.LENGTH_SHORT).show();
            switch (state){
                case NfcAdapter.STATE_TURNING_ON:
                    this.showIO(context);
                    break;
            }
        }
    }

    private void showIO(Context context) {
        Intent intent = new Intent(context, IOActivity.class);
        context.startActivity(intent);
    }

}
