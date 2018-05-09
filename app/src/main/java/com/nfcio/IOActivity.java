package com.nfcio;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.ManagerFactoryParameters;

public class IOActivity extends AppCompatActivity implements View.OnClickListener, NfcAdapter.OnNdefPushCompleteCallback {
    private static final int REQ_NFC = 231, REQ_TAG = 234;
    private RecordAdapter adpWrite, adpRead;
    private NfcAdapter mAdapter;
    private EditText edtRecord;
    ListView lstWriteRecords, lstReadRecords;
    private PendingIntent dispatchIntent;
    private NfcHelper nfcHelper;

    private final BroadcastReceiver nfcReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)){
                if(intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, -1) == NfcAdapter.STATE_TURNING_OFF){
                    IOActivity.this.finish();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_io);
        IntentFilter nfcFilter = new IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
        this.registerReceiver(this.nfcReceiver, nfcFilter);
        this.dispatchIntent = PendingIntent.getActivity(this, REQ_TAG, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        this.edtRecord = this.findViewById(R.id.edt_record);
        this.lstWriteRecords = this.findViewById(R.id.lst_w_records);
        this.lstReadRecords = this.findViewById(R.id.lst_r_records);

        Button btnAdd = this.findViewById(R.id.btn_add_record),
                btnWrite = this.findViewById(R.id.btn_write);

        btnAdd.setOnClickListener(this);
        btnWrite.setOnClickListener(this);



        this.adpWrite = new RecordAdapter(this, R.layout.record_item, new ArrayList<RecordAdapter.Record>());
        this.adpRead = new RecordAdapter(this, R.layout.record_item, new ArrayList<RecordAdapter.Record>());

        this.lstWriteRecords.setAdapter(this.adpWrite);
        this.lstReadRecords.setAdapter(this.adpRead);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.NFC) == PackageManager.PERMISSION_GRANTED)
            this.init();
        else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.NFC}, REQ_NFC);

    }

    private final void init(){
        this.mAdapter = NfcAdapter.getDefaultAdapter(this);
        if(this.mAdapter == null) {
            Toast.makeText(this, "Nfc is not supported on this device.", Toast.LENGTH_SHORT).show();
            this.finish();
        }else{
            this.mAdapter.setOnNdefPushCompleteCallback(this, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(this.mAdapter != null){

            IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            tagDetected.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
            tagDetected.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);

            IntentFilter[] intentFilters = new IntentFilter[]{tagDetected};
            this.mAdapter.enableForegroundDispatch(this, dispatchIntent, intentFilters, null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Toast.makeText(this.getApplicationContext(), "TAG:", Toast.LENGTH_SHORT).show();
        switch (intent.getAction()){
            case NfcAdapter.ACTION_NDEF_DISCOVERED:
                case NfcAdapter.ACTION_TECH_DISCOVERED:
                    processTag(intent);
                break;
        }
        super.onNewIntent(intent);
    }

    private void processTag(Intent intent) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if(tag != null){
                Ndef ndef = Ndef.get(tag);
                if(ndef == null){
                    NfcA nfcA = NfcA.get(tag);
                    if(nfcA != null) {
                        this.nfcHelper = new NfcAHelper(tag);
                        byte[] data = ((NfcAHelper)this.nfcHelper).read();
                        if(data != null){
                            for(int pos = 0; pos < data.length; pos++){
                                Toast.makeText(this, ((byte)'T') + "|" + data[pos], Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else{
                        Toast.makeText(this, "Unsupported Tag", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else
                    this.nfcHelper = new NDEFHelper(tag);
            }else{
                Toast.makeText(this, "Invalid Tag", Toast.LENGTH_SHORT).show();
                finish();
            }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(this.mAdapter != null)
            this.mAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQ_NFC){
            if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                Toast.makeText(this, "Nfc Permission in needed inorder to access nfc", Toast.LENGTH_SHORT).show();
                this.finish();
            }else
                this.init();
        }else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_record:
                addRecordToWrite();
                break;
            case R.id.btn_write:
                write();
                break;
        }
    }

    private void addRecordToWrite(){
        RecordAdapter.Record record = new RecordAdapter.Record(edtRecord.getText().toString());
        this.adpWrite.add(record);
        this.adpWrite.notifyDataSetChanged();
        edtRecord.setText("");
    }

    private void write(){
        RecordAdapter.Record[] records = compileRecords();
        this.nfcHelper.write(records);
    }

    private RecordAdapter.Record[] compileRecords() {

        RecordAdapter.Record[] records = new RecordAdapter.Record[this.adpWrite.getCount()];
        for(int pos = 0; pos < this.adpWrite.getCount(); pos++){
             records[pos] = this.adpWrite.getItem(pos);
//            byte[] payload = record.Value.getBytes(Charset.forName("UTF-8"));
//            NdefRecord ndefRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
//            records[pos] = ndefRecord;
        }
        return records;
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {

        Toast.makeText(this, "Records Written", Toast.LENGTH_SHORT).show();
        this.adpWrite.clear();
    }


}
