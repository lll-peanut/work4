package com.peanut.utils;

public class SnowflakeIdGeneratorUtil {

    /**
     * 自定义的2025年12月1日0点的时间戮
     */
    private final long twepoch = 1764518400000L;

    /**
     * 当前节点ID,范围 0-1023
     */
    private final long nodeId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    /**
     * 节点id占用位数
     */
    private static final long nodeIdBits = 10L;

    /**
     * 最大2的nodeIdBits次方-1
     */
    private static final long maxNodeId = ~(-1L << nodeIdBits);
    /**
     * 节点ID左移位数（跳过序列号）
     */

    private static final long sequenceBits = 12L;
    private static final long nodeIdShift = sequenceBits;

    private static final long timestampLeftShift = sequenceBits + nodeIdBits;
    private static final long sequenceMask = ~(-1L << sequenceBits);

    public SnowflakeIdGeneratorUtil(long nodeId) {
        if (nodeId > maxNodeId || nodeId < 0) {
            throw new IllegalArgumentException(String.format("Node ID must be between 0 and %d", maxNodeId));
        }
        this.nodeId = nodeId;
    }

    public synchronized long generateId() {
        long timestamp = currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id.");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = waitUntilNextMillis(timestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << timestampLeftShift) | (nodeId << nodeIdShift) | sequence;
    }

    private long waitUntilNextMillis(long currentMillis) {
        long timestamp = currentTimeMillis();
        while (timestamp <= currentMillis) {
            timestamp = currentTimeMillis();
        }
        return timestamp;
    }

    private long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}