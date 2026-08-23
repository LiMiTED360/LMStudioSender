package ch.snorpcorp.lmstudiosender.sender;

import ch.snorpcorp.lmstudiosender.sender.messages.Message;
import ch.snorpcorp.lmstudiosender.sender.messages.MessageRoles;

import java.util.List;

public class ContextHelper {
    private final float symbolsPerToken = 3.5f;
    private final int messageOverhead = 50;

    public Integer deleteOldContext(List<Message> context, String systemPrompt, Message newMessage, int maxTokens, boolean strictContextAlternate) {
        return deleteOldContext(context,  (systemPrompt != null ? getTokens(systemPrompt) : 0) + getTokens(newMessage), maxTokens, strictContextAlternate);
    }

    public Integer deleteOldContext(List<Message> context, String systemPrompt, int maxTokens,  boolean strictContextAlternate) {
        return deleteOldContext(context, systemPrompt != null ? getTokens(systemPrompt) : 0, maxTokens, strictContextAlternate);
    }

    private Integer deleteOldContext(List<Message> context, int  startingTokenCount, int maxTokens,  boolean strictContextAlternate) {
        if (context.isEmpty()) return null;
        int[] output =  findOldestMessageToKeep(context, startingTokenCount, maxTokens, strictContextAlternate);

        deleteOldMessages(context, output[0]);
        return output[1];

    }

    private int[] findOldestMessageToKeep(List<Message> context, int startingTokenCount, int maxTokens, boolean strictContextAlternate) {
        int oldestMessageToKeep = context.size();
        int totalTokenCount = startingTokenCount;

        for (int i = context.size() - 1; i >= 0; i--) {
            int tokens = getTokens(context.get(i));
            if (totalTokenCount + tokens > maxTokens && !(strictContextAlternate && context.get(i).role() == MessageRoles.user)) break;
            oldestMessageToKeep = i;
            totalTokenCount += tokens;
        }

        if (strictContextAlternate) {
            while (oldestMessageToKeep < context.size() && context.get(oldestMessageToKeep).role() != MessageRoles.user) {
                oldestMessageToKeep++;
            }
        }

        return new int[]{oldestMessageToKeep, totalTokenCount};
    }

    private void deleteOldMessages(List<Message> context, int oldestMessageToKeep) {
        if (oldestMessageToKeep > 0 && oldestMessageToKeep <= context.size()) {
            context.subList(0, oldestMessageToKeep).clear();
        }
    }

    private int getTokens(Message message) {
        return (int) ((message.content().length() + messageOverhead) / symbolsPerToken);
    }

    private int getTokens(String message) {
        return (int) ((message.length() + messageOverhead) / symbolsPerToken);
    }
}
