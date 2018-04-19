package com.example.xmpp;

import com.example.xmpp.activity.LoginAcitvity;
import com.example.xmpp.uitls.ThreadUtils;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;


public class SplashActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        //停留3秒进入登陆界面
        ThreadUtils.runInThread(new Runnable() {
			
			@Override
			public void run() {
				SystemClock.sleep(3000);
				//进入主要界面
				Intent intent = new Intent(SplashActivity.this,LoginAcitvity.class);
				startActivity(intent);
				finish();
			}
		});
    }


}
