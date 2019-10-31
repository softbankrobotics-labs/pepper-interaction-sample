package com.softbankrobotics.interactionsample;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.softbankrobotics.interactionsample.fragments.StateMachineWatcher;
import com.softbankrobotics.interactionsample.statemachine.InteractionStateMachine;

import java.util.ArrayList;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {
    String TAG = "MainActivity";
    QiContext qiContext;
    InteractionStateMachine stateMachine;

    ///////////////////////////////////
    // Android lifecycle callbacks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QiSDK.register(this, this);
    }

    @Override
    protected void onDestroy() {
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    ///////////////////////////////////
    // Robot lifecycle callbacks
    Fragment lastFragment = null;

    void setFragment(Fragment fragment) {
        runOnUiThread(() -> {
            if (fragment != null) {
                FragmentTransaction transition = getSupportFragmentManager().beginTransaction();
                transition.replace(R.id.stateFragmentHolder, fragment);
                transition.commit();
            } else if (lastFragment != null) {
                FragmentTransaction transition = getSupportFragmentManager().beginTransaction();
                transition.remove(lastFragment);
                transition.commit();
            }
            lastFragment = fragment;
        });
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        Log.i(TAG, "Robot Focus gained");
        this.qiContext = qiContext;
        stateMachine = new InteractionStateMachine(qiContext, getApplicationContext());
        for (StateMachineWatcher watcher : stateMachineWatchers) {
            runOnUiThread(() -> watcher.onStateMachineReady(stateMachine));
        }
        runOnUiThread(() -> stateMachine.fragment.observe(this, this::setFragment));
        stateMachine.start();
    }

    @Override
    public void onRobotFocusLost() {
        Log.i(TAG, "Robot Focus lost");
        stateMachine.stop();
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        Log.i(TAG, "Robot Focus refused because " + reason);
    }

    ArrayList<StateMachineWatcher> stateMachineWatchers = new ArrayList<>();

    public void addStateMachineWatcher(StateMachineWatcher watcher) {
        stateMachineWatchers.add(watcher);
        if (stateMachine != null) {
            runOnUiThread(() -> watcher.onStateMachineReady(stateMachine));
        }
    }
}
