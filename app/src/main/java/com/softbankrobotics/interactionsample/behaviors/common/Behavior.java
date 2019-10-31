package com.softbankrobotics.interactionsample.behaviors.common;

import com.aldebaran.qi.Future;

/* Abstract interface for a "Behavior", a simple object that can be started and stopped.
 */
public interface Behavior {

    /* Start the behavior. A behavior should not be started unless the previous one is finished.
     */
    void start();

    /* Requests to stop the behavior. Returns a future that indicates if the behavior is finished.
     */
    Future<Void> stop();
}
