package com.peanut.utils;

import com.peanut.im.enumPackage.ImWsEnvelopeType;
import com.peanut.im.pojo.dto.ImWsEnvelope;

/**
 * @author: peanut
 * @date: 2026/5/19
 * @version:1.0
 */
public class CheckUtil {

    /**
     * 检查ImWsEnvelope是否合法
     *
     * @param envelope 需要校验的ImWsEnvelope
     * @return 1-合法，0-不合法
     */
    public static int checkImWsEnvelope(ImWsEnvelope<?> envelope) {
        if (envelope == null) return 0;
        if (isEmpty(envelope.getConversationId())
                || envelope.getConversationType() == null
                || isEmpty(envelope.getClientMsgId())) {
            return 0;
        }
        // 检查type是否合法
        ImWsEnvelopeType type = envelope.getType();
        if (type == null) return 0;
        switch (type) {
            case MSG:
                // 发消息，必须有 msgId, conversationId, conversationType, payload
                if (isEmpty(envelope.getConversationId())
                        || envelope.getConversationType() == null
                        || envelope.getPayload() == null
                        || isEmpty(envelope.getClientMsgId())) {
                    return 0;
                }
                break;
            case DELIVER_ACK:
                // 回执，必须有 msgId, conversationId, conversationType, maxSeq
                if (isEmpty(envelope.getMsgId())
                        || isEmpty(envelope.getConversationId())
                        || envelope.getMaxSeq() == null) {
                    return 0;
                }
                break;
            case DELIVERED_NOTICE:
                // 投递通知，必须有 msgId, conversationId, conversationType
                if (isEmpty(envelope.getMsgId())
                        || isEmpty(envelope.getConversationId())
                        || envelope.getConversationType() == null) {
                    return 0;
                }
                break;
            case SERVER_ACK:
                // 服务器 ACK，必须有 msgId
                if (isEmpty(envelope.getMsgId())) {
                    return 0;
                }
                break;
            case SYNC_REQ:
                // 补拉，一般需要 conversationId, conversationType, fromSeqExclusive
                if (isEmpty(envelope.getConversationId())
                        || envelope.getConversationType() == null
                        || envelope.getFromSeqExclusive() == null) {
                    return 0;
                }
                break;
            case SYNC_RESP:
                // 补拉响应，要有 conversationId, conversationType, payload(消息列表), maxSeq
                if (isEmpty(envelope.getConversationId())
                        || envelope.getConversationType() == null
                        || envelope.getPayload() == null
                        || envelope.getMaxSeq() == null) {
                    return 0;
                }
                break;
            case ERROR:
                // error 至少要有 type
                break;
            default:
                // 未知类型非法
                return 0;
        }
        return 1;
    }

    private static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
