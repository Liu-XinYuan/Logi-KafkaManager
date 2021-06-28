package com.xiaojukeji.kafka.manager.service.cache;

import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumeSummaryDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存Metrics数据
 * @author zengqiao
 * @date 2019-04-30
 */
public class KafkaMetricsCache {
    /**
     * <clusterId, Metrics List>
     */
    private static Map<Long, Map<String, TopicMetrics>> TopicMetricsMap = new ConcurrentHashMap<>();
    private static Map<Long, List<ConsumeSummaryDTO>> ConsumerMetricsMap = new HashMap<>();

    public static void putTopicMetricsToCache(Long clusterId, List<TopicMetrics> dataList) {
        if (clusterId == null || dataList == null) {
            return;
        }
        Map<String, TopicMetrics> subMetricsMap = new HashMap<>(dataList.size());
        for (TopicMetrics topicMetrics : dataList) {
            subMetricsMap.put(topicMetrics.getTopicName(), topicMetrics);
        }
        TopicMetricsMap.put(clusterId, subMetricsMap);
    }

    public static void putConsumerMetricsToCache(Long clusterId, List<ConsumeSummaryDTO> dataList) {
        if (clusterId == null || dataList == null) {
            return;
        }
        ConsumerMetricsMap.put(clusterId, dataList);
    }

    public static Map<Long, List<ConsumeSummaryDTO>> getAllConsumerMetricsToCache() {
        return ConsumerMetricsMap;
    }

    public static  List<ConsumeSummaryDTO> getAllConsumerMetricsToCache(Long clusterId) {
        return ConsumerMetricsMap.get(clusterId);
    }

    public static Map<String, TopicMetrics> getTopicMetricsFromCache(Long clusterId) {
        return TopicMetricsMap.getOrDefault(clusterId, Collections.emptyMap());
    }

    public static Map<Long, Map<String, TopicMetrics>> getAllTopicMetricsFromCache() {
        return TopicMetricsMap;
    }

    public static TopicMetrics getTopicMetricsFromCache(Long clusterId, String topicName) {
        if (clusterId == null || topicName == null) {
            return null;
        }
        Map<String, TopicMetrics> subMap = TopicMetricsMap.getOrDefault(clusterId, Collections.emptyMap());
        return subMap.get(topicName);
    }
}
