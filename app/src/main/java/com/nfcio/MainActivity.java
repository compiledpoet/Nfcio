package com.nfcio;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final int REQ_NFC = 132;
    private NfcAdapter nfcAdapter;
    private TextView txt_info;
    private NfcListener nfcListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.txt_info = this.findViewById(R.id.txt_info);

        this.nfcListener = new NfcListener();
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.NFC) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.NFC}, REQ_NFC);
        }else{
            this.init();
        }
    }

    private void init() {
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(this.nfcAdapter == null){
            Toast.makeText(this, "Device Doesn't Support NCF :(", Toast.LENGTH_LONG).show();
            this.finish();
            return;
        }

        this.checkConnection();
    }

    private void checkConnection() {
        if (this.nfcAdapter != null)
            if (!this.nfcAdapter.isEnabled()) {
                txt_info.setText("NFC is not enabled. Please Turn it on.");
                IntentFilter receiverFilter = new IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
                this.registerReceiver(this.nfcListener, receiverFilter);
            } else {
                Intent intent = new Intent(this, IOActivity.class);
                this.startActivity(intent);
                finish();
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQ_NFC){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.NFC) == PackageManager.PERMISSION_DENIED){
                Toast.makeText(this, "Application can't operate without NFC Permission", Toast.LENGTH_LONG).show();
                this.finish();
            }else{
                this.init();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
    }

    @Override
    protected void onPause() {
        this.unregisterReceiver(this.nfcListener);
        super.onPause();
    }
}
