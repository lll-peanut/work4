package com.peanut.im.eneity;

/**
 * @author: peanut
 * @date: 2026/3/18
 * @version:1.0
 */
public class ConversationMeta {
    private Byte conversationType;
    private String conversationId;
    private String lastMsgId;
    private Long lastSeq;
    private Byte mute;
    private Byte pinned;

    public Byte getConversationType() {
        return conversationType;
    }

    public void setConversationType(Byte conversationType) {
        this.conversationType = conversationType;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getLastMsgId() {
        return lastMsgId;
    }

    public void setLastMsgId(String lastMsgId) {
        this.lastMsgId = lastMsgId;
    }

    public Long getLastSeq() {
        return lastSeq;
    }

    public void setLastSeq(Long lastSeq) {
        this.lastSeq = lastSeq;
    }

    public Byte getMute() {
        return mute;
    }

    public void setMute(Byte mute) {
        this.mute = mute;
    }

    public Byte getPinned() {
        return pinned;
    }

    public void setPinned(Byte pinned) {
        this.pinned = pinned;
    }
}