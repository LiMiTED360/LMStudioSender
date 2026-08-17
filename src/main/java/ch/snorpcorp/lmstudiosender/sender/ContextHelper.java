package ch.snorpcorp.lmstudiosender.sender;

import ch.snorpcorp.lmstudiosender.sender.messages.Message;
import java.util.List;

public class ContextHelper {
    private final float symbolsPerToken = 3.5f;
    private final int messageOverhead = 50;

    public void deleteOldContext(List<Message> context, Message systemPrompt, Message newMessage, int maxTokens) {
        deleteOldContext(context, getTokens(systemPrompt) + getTokens(newMessage), maxTokens);
    }

    public void deleteOldContext(List<Message> context, Message systemPrompt, int maxTokens) {
        deleteOldContext(context, getTokens(systemPrompt), maxTokens);
    }

    public void deleteOldContext(List<Message> context, int maxTokens) {
        deleteOldContext(context, 0, maxTokens);
    }

    private void deleteOldContext(List<Message> context, int  startingTokenCount, int maxTokens) {
        if (context.isEmpty()) return;
        deleteOldMessages(context, findOldestMessageToKeep(context, startingTokenCount, maxTokens));

    }

    private int findOldestMessageToKeep(List<Message> context, int  startingTokenCount, int maxTokens) {
        int oldestMessageToKeep = context.size();
        int totalTokenCount = startingTokenCount;

        for (int i = context.size() - 1; i >= 0; i--) {
            int tokens = getTokens(context.get(i));
            if (totalTokenCount + tokens > maxTokens) break;
            else {
                oldestMessageToKeep = i;
                totalTokenCount += tokens;
            }
        }

        return oldestMessageToKeep;
    }

    private void deleteOldMessages(List<Message> context, int oldestMessageToKeep) {
        if (oldestMessageToKeep > 0 && oldestMessageToKeep <= context.size()) {
            context.subList(0, oldestMessageToKeep).clear();
        }
    }

    private int getTokens(Message message) {
        return (int) ((message.content().length() + messageOverhead) / symbolsPerToken);
    }
}
