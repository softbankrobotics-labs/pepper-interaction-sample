package com.softbankrobotics.interactionsample.statemachine.common;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.aldebaran.qi.Future;
import com.softbankrobotics.interactionsample.behaviors.common.Behavior;
import com.softbankrobotics.interactionsample.statemachine.EngagementState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * A base state machine, to extend for specific usages.
 *
 * The idea is that you inherit from this and jut have to specify the business logic, namely:
 *  - getTargetState to decide which state to pick
 *  - tables of (state -> behavior) and (state -> fragment)
 * ... and the base class takes care of the rest; by ensuring that only one behavior is active
 * at a time, and switching to the new state when appropriate.
 *
 * The child class will have to call updateState whenever a new state needs to be calculated.
 */
public abstract class BaseStateMachine {
    private Behavior currentBehavior = null;
    private Future<Void> currentAction = null;
    private EngagementState previousState = null;
    private EngagementState nextState = null;
    // The tables to (optionally) fill with states
    protected Map<EngagementState, Behavior> behaviors = new HashMap<>();
    protected Map<EngagementState, Fragment> fragments = new HashMap<>();

    /* Live data, to be observed by others
     */
    public MutableLiveData<EngagementState> state = new MutableLiveData<>();
    public MutableLiveData<Fragment> fragment = new MutableLiveData<>();
    public LiveData<List<StateStatus>> allStates = Transformations.map(state, (_state) -> {
        List<StateStatus> stateStatuses = new ArrayList<>();
        for (EngagementState val : EngagementState.values()) {
            if (val != EngagementState.TRANSITION) {
                stateStatuses.add(new StateStatus(val.toString(),
                        val == _state,
                        val == previousState,
                        val == nextState));
            }
        }
        return stateStatuses;
    });

    /* To override with your state-specific rules
     */
    protected abstract EngagementState getTargetState();

    private Behavior getStateBehavior(EngagementState state) {
        if (behaviors.containsKey(state)) {
            return behaviors.get(state);
        } else {
            return null;
        }
    }

    private Fragment getStateFragment(EngagementState state) {
        if (fragments.containsKey(state)) {
            return fragments.get(state);
        } else {
            return null;
        }
    }

    protected void updateState() {
        nextState = getTargetState();
        if ((currentAction != null) && !currentAction.isDone()) {
            // We're already waiting for something.
            // Update nextState anyway, purely for GUI
            return;
        }
        if (nextState != state.getValue()) {
            if (currentBehavior != null) {
                // If there is already a behavior, stop it
                currentAction = currentBehavior.stop();
                currentBehavior = null;
                // When the stop takes time, wait for it to finish
                if ((currentAction != null) && !currentAction.isDone()) {
                    // We need to wait!
                    previousState = state.getValue();
                    state.postValue(EngagementState.TRANSITION);
                    currentAction.thenConsume(fut -> {
                        // Regardless of whether we succeeded
                        updateState();
                    });
                    return; // and don't do anything else (stay in transition state)
                }
            }
            currentBehavior = null;
            previousState = null; // We forget the previous state
            state.postValue(nextState);
            currentBehavior = getStateBehavior(nextState);
            if (currentBehavior != null) {
                currentBehavior.start();
            }
            fragment.postValue(getStateFragment(nextState));
        }
    }

}
