package com.softbankrobotics.interactionsample.behaviors.common;

import android.util.Log;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;

/* An abstract base behavior that implements the most common utilities for QiSDK.
 *
 * Includes:
 *  - Having a qiContext
 *  - returning a future by default (this is often set in start())
 *  - a logger aimed to be plugged after a future
 */
public abstract class SimpleBehavior implements Behavior {
    protected QiContext qiContext;
    protected Future<Void> mainFuture = null;

    public SimpleBehavior(QiContext qiContext) {
        this.qiContext = qiContext;
    }

    /**
     * Helper function to make logging easier
     */
    protected void logFutureError(Future<Void> future) {
        if (future.hasError()) {
            Log.i(getClass().getSimpleName(), "Error: " + future.getErrorMessage());
        }
    }

    @Override
    public Future<Void> stop() {
        return mainFuture;
    }

}
