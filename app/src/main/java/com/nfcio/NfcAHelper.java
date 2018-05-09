package com.nfcio;

import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.util.Log;

import java.io.IOException;

/**
 * Created by ASANDA on 2018/05/09.
 * for Pandaphic
 */
public class NfcAHelper extends NfcHelper {
    private static final String TAG = "NFCA.OUTPUT";
    private final byte WRITE = (byte)0xA2, READ = 0x30, PAGE = 1;
    private final NfcA nfcA;

    public NfcAHelper(Tag tag){
        super(tag);
        this.nfcA = NfcA.get(tag);
    }

    @Override
    public void write(RecordAdapter.Record[] records) {
        try {
            if(!this.nfcA.isConnected()){
                this.nfcA.connect();
            }
            byte[] data = new byte[]{'T', 'E', 'S', 'T'};
            byte[] command = new byte[]{
                    this.WRITE,
                    (byte)(PAGE & 0x0ff),
                    data[0],
                    data[1],
                    data[2],
                    data[3]
            };

            byte[] result = this.nfcA.transceive(command);
            for(int pos = 0; pos < records.length; pos++){
                Log.i(TAG, result[pos] + ":");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(nfcA != null){
                try {
                    this.nfcA.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public byte[] read(){
        try{
        if(!this.nfcA.isConnected()){
            this.nfcA.connect();
        }

        byte[] command = new byte[]{
                READ,
                (byte)(PAGE * 0x0ff)
        };

        return this.nfcA.transceive(command);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(this.nfcA != null) {
                try {
                    this.nfcA.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
