package com.xiaojukeji.kafka.manager.task.schedule.metadata;

import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.common.entity.ConsumerMetadata;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.service.cache.ConsumerMetadataCache;
import com.xiaojukeji.kafka.manager.service.cache.KafkaClientPool;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.protocol.types.SchemaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import scala.collection.JavaConversions;

import java.util.*;

/**
 * @author zengqiao
 * @date 19/12/25
 */
@Component
public class FlushBKConsumerGroupMetadata {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    @Autowired
    private ClusterService clusterService;

    @Scheduled(cron="0/5 * * * * ?")
    public void schedule() {
        List<ClusterDO> doList = clusterService.list();

        for (ClusterDO clusterDO: doList) {
            LOGGER.info("flush broker cg start, clusterId:{}.", clusterDO.getId());
            long startTime = System.currentTimeMillis();
            try {
                flush(clusterDO.getId());
            } catch (Throwable t) {
                LOGGER.error("flush broker cg failed, clusterId:{}.", clusterDO.getId(), t);
            }
            LOGGER.info("flush broker cg finished, clusterId:{} costTime:{}.",
                    clusterDO.getId(), System.currentTimeMillis() - startTime);
        }
    }

    private void flush(Long clusterId) {
        // 获取消费组列表
        Set<String> consumerGroupSet = new HashSet<>();
        collectAndSaveConsumerGroup(clusterId, consumerGroupSet);

        // 获取消费组summary信息
        Map<String, Set<String>> topicNameConsumerGroupMap = new HashMap<>();
        Map<String, ConsumerGroupDescription> consumerGroupSummary =
                collectConsumerGroupSummary(clusterId, consumerGroupSet, topicNameConsumerGroupMap);

        // 获取Topic下的消费组
        topicNameConsumerGroupMap =
                collectTopicAndConsumerGroupMap(clusterId, consumerGroupSet, topicNameConsumerGroupMap);


        ConsumerMetadataCache.putConsumerMetadataInBK(clusterId,
                new ConsumerMetadata(
                        consumerGroupSet,
                        topicNameConsumerGroupMap,
                        consumerGroupSummary
                )
        );
    }

    private void collectAndSaveConsumerGroup(Long clusterId, Set<String> consumerGroupSet) {
        try {
            AdminClient adminClient = KafkaClientPool.getAdminClient(clusterId);
            Collection<ConsumerGroupListing> consumerGroupListings = adminClient.listConsumerGroups().all().get();
            for (ConsumerGroupListing consumerGroupListing : consumerGroupListings) {
                String consumerGroup = consumerGroupListing.groupId();
                if (consumerGroup != null && consumerGroup.contains("#")) {
                    String[] splitArray = consumerGroup.split("#");
                    consumerGroup = splitArray[splitArray.length - 1];
                }
                consumerGroupSet.add(consumerGroup);
            }

        } catch (Exception e) {
            LOGGER.error("collect consumerGroup failed, clusterId:{}.", clusterId, e);
        }
    }

    private Map<String, ConsumerGroupDescription> collectConsumerGroupSummary(Long clusterId,
                                                                                      Set<String> consumerGroupSet,
                                                                                      Map<String, Set<String>> topicNameConsumerGroupMap) {
        if (consumerGroupSet == null || consumerGroupSet.isEmpty()) {
            return new HashMap<>();
        }
        AdminClient adminClient = KafkaClientPool.getAdminClient(clusterId);

        Map<String, ConsumerGroupDescription> consumerGroupSummaryMap = new HashMap<>();
        for (String consumerGroup : consumerGroupSet) {
            try {
                Map<String, ConsumerGroupDescription> stringConsumerGroupDescriptionMap = adminClient.describeConsumerGroups(Collections.singleton(consumerGroup)).all().get();
                ConsumerGroupDescription consumerGroupDescription = stringConsumerGroupDescriptionMap.get(consumerGroup);
                if (consumerGroupDescription == null) {
                    continue;
                }
                consumerGroupSummaryMap.put(consumerGroup, consumerGroupDescription);

                java.util.Iterator<MemberDescription> it =
                        consumerGroupDescription.members().iterator();
                while (it.hasNext()) {
                    MemberDescription next = it.next();
                    Set<TopicPartition> topicPartitionSet = next.assignment().topicPartitions();
                    if (topicPartitionSet == null) {
                        continue;
                    }
                    for (TopicPartition topicPartition : topicPartitionSet) {
                        Set<String> groupSet = topicNameConsumerGroupMap.getOrDefault(topicPartition.topic(), new HashSet<>());
                        groupSet.add(consumerGroup);
                        topicNameConsumerGroupMap.put(topicPartition.topic(), groupSet);
                    }
                }

            } catch (SchemaException e) {
                LOGGER.error("schemaException exception, clusterId:{} consumerGroup:{}.", clusterId, consumerGroup, e);
            } catch (Exception e) {
                LOGGER.error("collect consumerGroupSummary failed, clusterId:{} consumerGroup:{}.", clusterId, consumerGroup, e);
            }
        }
        return consumerGroupSummaryMap;
    }

    private Map<String, Set<String>> collectTopicAndConsumerGroupMap(Long clusterId,
                                                                     Set<String> consumerGroupSet,
                                                                     Map<String, Set<String>> topicNameConsumerGroupMap) {
        if (ValidateUtils.isEmptySet(consumerGroupSet)) {
            return new HashMap<>(0);
        }
        AdminClient adminClient = KafkaClientPool.getAdminClient(clusterId);

        for (String consumerGroup: consumerGroupSet) {
            try {
                Map<TopicPartition, OffsetAndMetadata> topicPartitionAndOffsetMap = adminClient.listConsumerGroupOffsets(consumerGroup).partitionsToOffsetAndMetadata().get();
                for (Map.Entry<TopicPartition, OffsetAndMetadata> entry : topicPartitionAndOffsetMap.entrySet()) {
                    TopicPartition tp = entry.getKey();
                    Set<String> subConsumerGroupSet = topicNameConsumerGroupMap.getOrDefault(tp.topic(), new HashSet<>());
                    subConsumerGroupSet.add(consumerGroup);
                    topicNameConsumerGroupMap.put(tp.topic(), subConsumerGroupSet);
                }
            } catch (Exception e) {
                LOGGER.error("update consumer group failed, clusterId:{} consumerGroup:{}.", clusterId, consumerGroup, e);
            }
        }
        return topicNameConsumerGroupMap;
    }
}
