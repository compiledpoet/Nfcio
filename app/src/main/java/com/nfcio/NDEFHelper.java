package com.nfcio;

import android.nfc.Tag;

/**
 * Created by ASANDA on 2018/05/08.
 * for Pandaphic
 */
public class NDEFHelper extends NfcHelper {


    public NDEFHelper(Tag tag){
        super(tag);
    }

    @Override
    public void write(RecordAdapter.Record[] records) {

    }
}
