package com.xiaojukeji.kafka.manager.common.entity;

import org.apache.kafka.clients.admin.ConsumerGroupDescription;

import java.util.*;

/**
 * @author zengqiao
 * @date 19/5/14
 */
public class ConsumerMetadata {
    private Set<String> consumerGroupSet = new HashSet<>();

    private Map<String, Set<String>> topicNameConsumerGroupMap = new HashMap<>();

    private Map<String, ConsumerGroupDescription> consumerGroupSummaryMap = new HashMap<>();

    public ConsumerMetadata(Set<String> consumerGroupSet,
                            Map<String, Set<String>> topicNameConsumerGroupMap,
                            Map<String, ConsumerGroupDescription> consumerGroupSummaryMap) {
        this.consumerGroupSet = consumerGroupSet;
        this.topicNameConsumerGroupMap = topicNameConsumerGroupMap;
        this.consumerGroupSummaryMap = consumerGroupSummaryMap;
    }

    public Set<String> getConsumerGroupSet() {
        return consumerGroupSet;
    }

    public Map<String, Set<String>> getTopicNameConsumerGroupMap() {
        return topicNameConsumerGroupMap;
    }

    public Map<String, ConsumerGroupDescription> getConsumerGroupSummaryMap() {
        return consumerGroupSummaryMap;
    }
}
