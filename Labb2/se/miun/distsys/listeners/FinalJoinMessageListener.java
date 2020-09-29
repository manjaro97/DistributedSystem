package se.miun.distsys.listeners;

import se.miun.distsys.messages.FinalJoinMessage;

public interface FinalJoinMessageListener {
    public void onIncomingFinalJoinMessage(FinalJoinMessage finaljoinMessage);
}
