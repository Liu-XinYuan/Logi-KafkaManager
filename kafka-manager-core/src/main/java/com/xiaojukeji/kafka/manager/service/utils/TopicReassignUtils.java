package com.xiaojukeji.kafka.manager.service.utils;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkPathUtil;
import com.xiaojukeji.kafka.manager.service.cache.KafkaClientPool;
import kafka.admin.ReassignPartitionsCommand;
import kafka.zk.KafkaZkClient;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.security.JaasUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.JavaConverters;
import scala.collection.Map$;
import scala.collection.Seq;

import java.util.*;

/**
 * @author zengqiao
 * @date 20/9/21
 */
public class TopicReassignUtils {
    private final static Logger LOGGER = LoggerFactory.getLogger(TopicReassignUtils.class);

    private static final Integer DATA_VERSION_ONE = 1;

    public static String generateReassignmentJson(ClusterDO clusterDO,
                                                  String topicName,
                                                  List<Integer> partitionIdList,
                                                  List<Integer> brokerIdList) {
        try {
            KafkaZkClient kafkaZkClient = KafkaClientPool.getKafkaZkClient(clusterDO.getId());
            if (kafkaZkClient.pathExists(ZkPathUtil.REASSIGN_PARTITIONS_ROOT_NODE)) {
                // 任务已经存在, 不知道是谁弄的
                return null;
            }

            // 生成迁移JSON
            return generateReassignmentJson(kafkaZkClient, topicName, partitionIdList, brokerIdList);
        } catch (Throwable t) {
            LOGGER.error("generate assignment json failed, clusterId:{} topicName:{} partitions:{} brokers:{}."
                    , clusterDO.getId(), topicName, partitionIdList, brokerIdList, t);
        }
        return null;
    }

    private static String generateReassignmentJson(KafkaZkClient zkClient,
                                                   String topicName,
                                                   List<Integer> partitionIdList,
                                                   List<Integer> brokerIdList) {
        Map<TopicPartition, Seq<Object>> reassignMap = createReassignmentMap(
                zkClient,
                topicName,
                new ArrayList<>(brokerIdList)
        );

        if (!ValidateUtils.isEmptyList(partitionIdList)) {
            Iterator<Map.Entry<TopicPartition, Seq<Object>>> it = reassignMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<TopicPartition, Seq<Object>> entry = it.next();
                if (partitionIdList.contains(entry.getKey().partition())) {
                    continue;
                }
                // 移除不在迁移中的对象
                it.remove();
            }
        }

        return ReassignPartitionsCommand.formatAsReassignmentJson(JavaConverters.mapAsScalaMapConverter(reassignMap).asScala(), Map$.MODULE$.empty());
    }

    private static Map<TopicPartition, Seq<Object>> createReassignmentMap(KafkaZkClient zkClient,
                                                                          String topicName,
                                                                          List<Object> brokerIdList) {
        scala.collection.Map<TopicPartition, Seq<Object>> scalaReassignmentMap =
                ReassignPartitionsCommand.generateAssignment(
                        zkClient,
                        JavaConverters.asScalaIteratorConverter(brokerIdList.iterator()).asScala().toSeq(),
                        JSON.toJSONString(generateTopicMoveProperties(topicName)),
                        false)
                        ._1();
        return JavaConverters.mapAsJavaMapConverter(scalaReassignmentMap).asJava();
    }

    private static Properties generateTopicMoveProperties (String topicName) {
        Map<String, Object> topicNameMap = new HashMap<>(1);
        topicNameMap.put("topic", topicName);

        Properties properties = new Properties();
        properties.put("topics", Arrays.asList(
                topicNameMap
        ));
        properties.put("version", DATA_VERSION_ONE);
        return properties;
    }
}
