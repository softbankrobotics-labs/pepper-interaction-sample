package com.softbankrobotics.interactionsample.statemachine;

/* An enum of possible states; the exact list can vary from one app to another.
 *
 * States added here will be automatically seen in the StateListFragment, in the same order
 * (so keep the order logical, and the names short and clear)
 */
public enum EngagementState {
    TRANSITION, // An exception, this one is not shown, it corresponds to switching between states.
    IDLE,
    ATTRACT,
    ENGAGED
}
