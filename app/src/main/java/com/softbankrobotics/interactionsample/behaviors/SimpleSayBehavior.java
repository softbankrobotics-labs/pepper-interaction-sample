package com.softbankrobotics.interactionsample.behaviors;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.softbankrobotics.interactionsample.behaviors.common.Behavior;
import com.softbankrobotics.interactionsample.behaviors.common.SimpleBehavior;

/* A Behavior that just says "Hey, You.
 *
 * Note that it will not exit until the say is done.
 *
 * Intended mostly as an example of a minimal behavior.
 */
public class SimpleSayBehavior extends SimpleBehavior implements Behavior {
    private Say say;
    public SimpleSayBehavior(QiContext qiContext, String phrase) {
        super(qiContext);
        SayBuilder.with(qiContext).withText(phrase).buildAsync()
                .andThenConsume(say -> this.say = say);
    }

    @Override
    public void start() {
        if (say != null) {
            mainFuture = say.async().run();
        }

    }
}
