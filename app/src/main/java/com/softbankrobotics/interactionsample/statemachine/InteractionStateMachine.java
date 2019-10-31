package com.softbankrobotics.interactionsample.statemachine;

import android.content.Context;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.object.human.Human;
import com.softbankrobotics.interactionsample.R;
import com.softbankrobotics.interactionsample.behaviors.ChatBehavior;
import com.softbankrobotics.interactionsample.behaviors.SimpleSayBehavior;
import com.softbankrobotics.interactionsample.fragments.SplashImageFragment;
import com.softbankrobotics.interactionsample.utils.HumanEngager;
import com.softbankrobotics.interactionsample.utils.HumanInterestTracker;
import com.softbankrobotics.interactionsample.statemachine.common.BaseStateMachine;

public class InteractionStateMachine extends BaseStateMachine {
    private String TAG = "InteractionStateMachine";
    private QiContext qiContext;
    private HumanEngager humanEngager = null;
    private HumanInterestTracker humanInterestTracker = null;
    private Human interestedHuman = null;
    private boolean isInteracting = false;

    public InteractionStateMachine(QiContext qiContext, Context androidContext) {
        this.qiContext = qiContext;
        humanEngager = new HumanEngager(qiContext, 5*1000);
        humanInterestTracker = new HumanInterestTracker(qiContext);
        // Map of States -> Behaviors
        behaviors.put(EngagementState.ATTRACT, new SimpleSayBehavior(qiContext, "Hey, you!"));
        behaviors.put(EngagementState.ENGAGED, new ChatBehavior(qiContext, R.raw.engaged));
        // Map of States -> Fragments
        ;

        Fragment purpleHeart = new SplashImageFragment(R.drawable.heart,
                ContextCompat.getColor(androidContext, R.color.colorAttract));
        Fragment blueSpeechBubble = new SplashImageFragment(R.drawable.interacting,
                ContextCompat.getColor(androidContext, R.color.colorDialogue));
        fragments.put(EngagementState.ATTRACT, purpleHeart);
        fragments.put(EngagementState.ENGAGED, blueSpeechBubble);
    }

    @Override
    protected EngagementState getTargetState() {
        if (isInteracting) {
            return EngagementState.ENGAGED;
        } else if (interestedHuman != null) {
            return EngagementState.ATTRACT;
        } else {
            return EngagementState.IDLE;
        }
    }

    public void start() {
        humanEngager.onInteracting = (_isInteracting) -> {
                isInteracting = _isInteracting;
                updateState();
        };
        humanInterestTracker.onInterestedHuman = (human) -> {
            interestedHuman = human;
            updateState();
        };

        // Start everything
        humanEngager.start();
        humanInterestTracker.start();
        Log.i(TAG, "All trackers started");
    }

    public void stop() {
        humanEngager.stop();
        humanInterestTracker.stop();
    }

}
