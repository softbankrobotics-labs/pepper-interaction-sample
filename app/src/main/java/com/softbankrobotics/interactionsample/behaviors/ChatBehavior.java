package com.softbankrobotics.interactionsample.behaviors;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Topic;
import com.softbankrobotics.interactionsample.behaviors.common.Behavior;


/* A Behavior that wraps a QiChatbot, that is started along the behavior.
 */
public class ChatBehavior implements Behavior {
    private Chat chat;
    private Future<Void> currentDialogFuture = null;

    public ChatBehavior(QiContext qiContext, int topicResourceId) {
        Topic topic = TopicBuilder.with(qiContext).withResource(topicResourceId).build();
        QiChatbot qiChatbot = QiChatbotBuilder.with(qiContext).withTopic(topic).build();
        chat = ChatBuilder.with(qiContext).withChatbot(qiChatbot).build();
    }

    @Override
    public void start() {
        currentDialogFuture = chat.async().run();
    }

    @Override
    public Future<Void> stop() {
        if (currentDialogFuture != null) {
            currentDialogFuture.requestCancellation();
        }
        return currentDialogFuture;
    }

}
