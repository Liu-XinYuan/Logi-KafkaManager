package com.xiaojukeji.kafka.manager.common.entity.ao.consumer;



public class ConsumeSummaryDTO {
    private Long clusterId;
    private String consumerGroup;
    private String topicName;
    private Long consumeOffset = 0L;
    private Long offset = 0L;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Long getConsumeOffset() {
        return consumeOffset;
    }

    public void setConsumeOffset(Long consumeOffset) {
        this.consumeOffset = consumeOffset;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }
}
