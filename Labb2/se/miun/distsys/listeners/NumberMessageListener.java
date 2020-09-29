package se.miun.distsys.listeners;

import se.miun.distsys.messages.NumberMessage;

public interface NumberMessageListener {
    public void onIncomingNumberMessage(NumberMessage numberMessage);
}
