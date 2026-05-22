package com.peanut.im.pojo.dto;

import com.peanut.im.enumPackage.ConversationType;
import com.peanut.im.enumPackage.ImWsEnvelopeType;

/**
 * @author: peanut
 * @date: 2026/5/19
 * @version:1.0
 */
public class ImWsEnvelope<T> {
    private ImWsEnvelopeType type;            // MSG / SERVER_ACK / DELIVER_ACK / DELIVERED_NOTICE / SYNC_REQ / SYNC_RESP / ERROR
    private String conversationId;
    private ConversationType conversationType; // USER / GROUP
    private String msgId;
    private String clientMsgId;
    private Long sequence;

    private Long maxSeq;            // DELIVER_ACK 用：接收端已收到的最大 seq
    private Long fromSeqExclusive;  // SYNC_REQ 用：从哪个 seq 之后补拉

    private T payload;

    public ImWsEnvelope() {}

    public ImWsEnvelope(ImWsEnvelopeType type, String conversationId, ConversationType conversationType, String msgId, String clientMsgId, Long sequence, Long maxSeq, Long fromSeqExclusive, T payload) {
        this.type = type;
        this.conversationId = conversationId;
        this.conversationType = conversationType;
        this.msgId = msgId;
        this.clientMsgId = clientMsgId;
        this.sequence = sequence;
        this.maxSeq = maxSeq;
        this.fromSeqExclusive = fromSeqExclusive;
        this.payload = payload;
    }

    public ImWsEnvelopeType getType() {
        return type;
    }

    public void setType(ImWsEnvelopeType type) {
        this.type = type;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public ConversationType getConversationType() {
        return conversationType;
    }

    public void setConversationType(ConversationType conversationType) {
        this.conversationType = conversationType;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getClientMsgId() {
        return clientMsgId;
    }

    public void setClientMsgId(String clientMsgId) {
        this.clientMsgId = clientMsgId;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public Long getMaxSeq() {
        return maxSeq;
    }

    public void setMaxSeq(Long maxSeq) {
        this.maxSeq = maxSeq;
    }

    public Long getFromSeqExclusive() {
        return fromSeqExclusive;
    }

    public void setFromSeqExclusive(Long fromSeqExclusive) {
        this.fromSeqExclusive = fromSeqExclusive;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}