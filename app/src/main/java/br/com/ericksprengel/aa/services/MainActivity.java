package br.com.ericksprengel.aa.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView mStatus;
    Intent mContadorIntent;
    ContadorService mContadorService;
    boolean mIsBound;

    private ServiceConnection mContadorServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(BuildConfig.LOG_TAG, "MainActivity#ContadorServiceConnection: onServiceConnected (" + Thread.currentThread().getId() + ")");
            mContadorService = ((ContadorService.ContadorBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mContadorService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mContadorIntent = new Intent(getApplicationContext(), ContadorService.class);

        // add start and stop actions
        findViewById(R.id.main_activity_start_service_button).setOnClickListener(this);
        findViewById(R.id.main_activity_stop_service_button).setOnClickListener(this);

        findViewById(R.id.main_activity_bind_service_button).setOnClickListener(this);
        findViewById(R.id.main_activity_unbind_service_button).setOnClickListener(this);

        findViewById(R.id.main_activity_get_contagem_button).setOnClickListener(this);

        mStatus = (TextView) findViewById(R.id.main_activity_status_textview);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_activity_start_service_button:
                startService(mContadorIntent);
                break;
            case R.id.main_activity_stop_service_button:
                stopService(mContadorIntent);
                break;
            case R.id.main_activity_bind_service_button:
                doBindContadorService();
                break;
            case R.id.main_activity_unbind_service_button:
                doUnbindContadorService();
                break;
            case R.id.main_activity_get_contagem_button:
                mStatus.setText(String.valueOf(mContadorService.getContagem()));
                break;
            default:
                Toast.makeText(getApplicationContext(), "Ops... não conheço esta view.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindContadorService();
    }



    void doBindContadorService() {
        bindService(new Intent(this,
                ContadorService.class), mContadorServiceConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindContadorService() {
        if (mIsBound) {
            unbindService(mContadorServiceConnection);
            mIsBound = false;
        }
    }
}
