package com.softbankrobotics.interactionsample.fragments;

import com.softbankrobotics.interactionsample.statemachine.InteractionStateMachine;

/*
 * An interface for those who want to be notified when the app's state machine is ready.
 */
public interface StateMachineWatcher {
    void onStateMachineReady(InteractionStateMachine stateMachine);
}
