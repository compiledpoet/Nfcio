package com.nfcio;

import android.nfc.Tag;

/**
 * Created by ASANDA on 2018/05/08.
 * for Pandaphic
 */
public abstract class NfcHelper {

    public NfcHelper(Tag tag){

    }
    public abstract void write(RecordAdapter.Record[] records);
}
