package io.rong.app.ui.activity;

import android.os.Bundle;
import android.util.Log;

import io.rong.app.R;

/**
 * Created by zhjchen on 4/18/15.
 */

public class PublicServiceInfoActivity extends BaseActivity{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("","---------PublicServiceInfoActivity------------");
        setContentView(R.layout.de_pub_account_info);

    }

}
