package com.softbankrobotics.interactionsample.utils;

import com.aldebaran.qi.Consumer;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.builder.EngageHumanBuilder;
import com.aldebaran.qi.sdk.object.human.Human;
import com.aldebaran.qi.sdk.object.humanawareness.EngageHuman;
import com.aldebaran.qi.sdk.object.humanawareness.HumanAwareness;

import java.util.Timer;
import java.util.TimerTask;


/*
 * A helper that engages the recommended human, and can notify on "interacting" state.
 *
 * This wraps standard practice for creating a "high priority" engagement with the human
 * recommended by QiSDK's HumanAwareness (i.e. the human standing in the right position that
 * looks interested in the robot.
 *
 * If the engaged human leaves but another one is recommended, that one will be engaged.
 *
 * An outside observer can add an "onInteracting" callback to be notified the robot is interacting,
 * meaning that either there is an engaged human, or there was one very recently (the goal is to
 * consider a quick sequence of humans as a "single interaction", to filter out noise caused by
 * some humans being temporarily not detected - we don't want to reset the interaction if that
 * happens).
 *
 * The timeout for how long the robot needs to be alone before the interaction is considered
 * "finished" is passed to the constructor.
 */

public class HumanEngager {

    private QiContext qiContext;
    private HumanAwareness awareness;

    // Inner working of engaging system
    private Boolean engaging = false;
    private Human queuedRecommendedHuman = null;
    private TimerTask disengageTimerTask = null;

    // Inner state, from which state is calculated
    private int unengageTimeMs;

    public Consumer<Boolean> onInteracting = null;

    public HumanEngager(QiContext qiContext, int unengageTimeMs) {
        this.qiContext = qiContext;
        this.unengageTimeMs = unengageTimeMs;
        awareness = qiContext.getHumanAwareness();
    }

    /* Internal; notify listener of "isInteracting" state.
     */
    private void setIsInteracting(Boolean isInteracting) {
        if (onInteracting != null) {
            try {
                onInteracting.consume(isInteracting);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    /* Internal; processes a recommended candidate for engagement by creating an Engage action.
     */
    private void tryToEngageHuman(Human human) {
        if (human != null) {
            engaging = true;
            //Log.i(TAG,"Building engage");
            EngageHuman engage = EngageHumanBuilder.with(qiContext).withHuman(human).build();
            engage.addOnHumanIsEngagedListener(() -> setIsInteracting(true));
            engage.async().run().thenConsume((fut) -> {
                engaging = false;
                // Try again with a new human
                tryToEngageHuman(queuedRecommendedHuman);
                queuedRecommendedHuman = null;
                // This listener could never be called any more, but leaving it risks a memory leak
                engage.removeAllOnHumanIsEngagedListeners();
            });
        } else {
            // No human to engage - BUT we give a timeout
            disengageTimerTask = new TimerTask() {
                public void run() {
                    setIsInteracting(false);
                }
            };
            new Timer("disengage").schedule(disengageTimerTask, unengageTimeMs);
        }
    }

    /* Start tracking and engaging humans.
     */
    public void start() {
        awareness.async().addOnRecommendedHumanToEngageChangedListener(recommendedHuman -> {
            if (!engaging) {
                tryToEngageHuman(recommendedHuman);
            } else {
                queuedRecommendedHuman = recommendedHuman;
            }
        });
        awareness.async().getRecommendedHumanToEngage().andThenConsume(this::tryToEngageHuman);
    }

    /* Start tracking and engaging humans.
     */
    public void stop() {
        awareness.removeAllOnRecommendedHumanToEngageChangedListeners();
        if (disengageTimerTask != null) {
            disengageTimerTask.cancel();
        }
    }

    // Internal API
}
