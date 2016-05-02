package com.example.admin.androidtemplete;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.github.pedrovgs.lynx.LynxActivity;
import com.github.pedrovgs.lynx.LynxConfig;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

import hugo.weaving.DebugLog;


@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity{

//    @Pref
//    MainPreference_ pref;

    @DebugLog
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        pref.flag().put(true);
    }

    @DebugLog
    @Override
    protected void onPause() {
        super.onPause();
    }

    @DebugLog
    @Override
    protected void onResume() {
        super.onResume();

        new TimerUtil(getApplicationContext()).setTimer(60 * 1000);
    }

    @DebugLog
    @Override
    protected void onStart() {
        super.onStart();
    }

    @DebugLog
    @Override
    protected void onStop() {
        super.onStop();
    }

    @DebugLog
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @DebugLog
    @Click(R.id.clickStart)
    void clickStart() {
//        if(pref.flag().get()) {
            new TimerUtil(getApplicationContext()).setTimer(60 * 1000);
//        }
    }

    @DebugLog
    @Click(R.id.clickStop)
    void clickStop() {
//        pref.flag().put(false);
    }


    @DebugLog
    @Background
    void checkRental() {

    }

    @DebugLog
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getAction() == KeyEvent.ACTION_UP && e.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            openLynxActivity();
        }
        return super.dispatchKeyEvent(e);
    }

    @DebugLog
    void openLynxActivity() {
        LynxConfig lynxConfig = new LynxConfig();
        lynxConfig.setMaxNumberOfTracesToShow(40000);

        Intent lynxActivityIntent = LynxActivity.getIntent(this, lynxConfig);
        startActivity(lynxActivityIntent);
    }
}

