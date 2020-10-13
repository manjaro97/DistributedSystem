package se.miun.distsys.listeners;

import se.miun.distsys.messages.ActivesMessage;

public interface ActivesMessageListener {
    public void onIncomingActivesMessage(ActivesMessage activesMessage);
}
