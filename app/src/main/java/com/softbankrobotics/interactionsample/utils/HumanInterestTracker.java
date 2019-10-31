package com.softbankrobotics.interactionsample.utils;

import android.util.Log;

import com.aldebaran.qi.Consumer;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.object.human.EngagementIntentionState;
import com.aldebaran.qi.sdk.object.human.Human;
import com.aldebaran.qi.sdk.object.humanawareness.HumanAwareness;

import java.util.ArrayList;
import java.util.List;


/* A helper to provide the human most interested in Pepper.
 *
 * It does this by watching all humans around, seeing if they're interested, and if they are,
 * notifying the callback.
 */
public class HumanInterestTracker {
    String TAG = "HumanInterestTracker";
    private HumanAwareness awareness;
    private ArrayList<Human> interestedHumans = new ArrayList<>();
    private ArrayList<Human> lastHumansAround = new ArrayList<>();
    private boolean running = false;

    /* Set this value to plug in the callback.
     */
    public Consumer<Human> onInterestedHuman = null;

    public HumanInterestTracker(QiContext qiContext) {
        awareness = qiContext.getHumanAwareness();
    }

    /* Start tracking humans
     */
    public void start() {
        running = true;
        awareness.async().addOnHumansAroundChangedListener(this::updateHumansAround);
        awareness.async().getHumansAround().andThenConsume(this::updateHumansAround);
        Log.i(TAG, "Started listening for humans.");
    }

    /* Stop tracking humans
     */
    public void stop() {
        awareness.removeAllOnHumansAroundChangedListeners();
        running = false;
        interestedHumans.clear();
        lastHumansAround.clear();
    }


    private void setInterestedHuman(Human human) {
        if ((onInterestedHuman != null) && (running)) {
            try {
                onInterestedHuman.consume(human);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private void handleHumanIntention(Human human, EngagementIntentionState engagementIntentionState) {
        switch (engagementIntentionState) {
            case NOT_INTERESTED:
                if (interestedHumans.contains(human)) {
                    interestedHumans.remove(human);
                    // NB: here we could find the nearest human instead, though the first one
                    // is also a decent heuristic
                    setInterestedHuman(interestedHumans.isEmpty() ? null : interestedHumans.get(0));
                }
                break;
            case INTERESTED:
            case SEEKING_ENGAGEMENT:
                if (!interestedHumans.contains(human)) {
                    interestedHumans.add(human);
                    // NB: as above, could be the nearest one
                    setInterestedHuman(interestedHumans.isEmpty() ? null : interestedHumans.get(0));
                }
                break;
            case UNKNOWN :
                break;
        }
    }

    private void updateHumansAround(List<Human> humansAround) {
        ArrayList<Human> tempLastHumansAround = new ArrayList<>(lastHumansAround);
        for (Human human : humansAround) {
            if (tempLastHumansAround.contains(human)) {
                tempLastHumansAround.remove(human); // Already being tracked
            } else {
                // This is a new guy, add him!
                human.addOnEngagementIntentionChangedListener((intention) -> {
                    handleHumanIntention(human, intention);
                });
                human.async().getEngagementIntention().andThenConsume((intention) -> {
                    handleHumanIntention(human, intention);
                });
            }
        }
        lastHumansAround = new ArrayList<>(humansAround);
        boolean removed = false;
        for (Human human : tempLastHumansAround) {
            removed = removed || interestedHumans.remove(human);
        }
        if (removed && (interestedHumans.size() == 0)) {
            setInterestedHuman(null);
        }
    }

}
