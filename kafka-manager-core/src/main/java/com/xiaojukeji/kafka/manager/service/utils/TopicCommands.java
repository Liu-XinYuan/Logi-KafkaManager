package com.xiaojukeji.kafka.manager.service.utils;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.service.cache.KafkaClientPool;
import kafka.admin.AdminOperationException;
import kafka.admin.AdminUtils;
import kafka.admin.BrokerMetadata;
import kafka.controller.ReplicaAssignment;
import kafka.zk.AdminZkClient;
import kafka.zk.KafkaZkClient;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.*;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Int;
import scala.Option;
import scala.Tuple2;
import scala.collection.JavaConversions;
import scala.collection.JavaConverters;
import scala.collection.Seq;
import scala.collection.convert.Decorators;

import java.util.*;

/**
 * @author zengqiao
 * @date 20/4/22
 */
public class TopicCommands {
    private static final Logger LOGGER = LoggerFactory.getLogger(TopicCommands.class);


    public static ResultStatus createTopic(ClusterDO clusterDO,
                                           String topicName,
                                           Integer partitionNum,
                                           Integer replicaNum,
                                           List<Integer> brokerIdList,
                                           Properties config) {
        try {
            AdminZkClient adminZkClient = KafkaClientPool.getAdminZkClient(clusterDO.getId());
            // 生成分配策略
            scala.collection.Map<Object, Seq<Object>> objectSeqMap = AdminUtils.assignReplicasToBrokers(
                    convert2BrokerMetadataSeq(brokerIdList),
                    partitionNum,
                    replicaNum,
                    randomFixedStartIndex(),
                    -1);
            adminZkClient.createTopicWithAssignment(topicName, config, objectSeqMap);
        } catch (NullPointerException e) {
            LOGGER.error("class=TopicCommands||method=createTopic||errMsg={}||clusterDO={}||topicName={}||partitionNum={}||replicaNum={}||brokerIdList={}||config={}",
                    e.getMessage(), clusterDO, topicName, partitionNum, replicaNum, JSON.toJSONString(brokerIdList), config, e);
            return ResultStatus.TOPIC_OPERATION_PARAM_NULL_POINTER;
        } catch (InvalidPartitionsException e) {
            LOGGER.error("class=TopicCommands||method=createTopic||errMsg={}||clusterDO={}||topicName={}||partitionNum={}||replicaNum={}||brokerIdList={}||config={}",
                    e.getMessage(), clusterDO, topicName,partitionNum,replicaNum,JSON.toJSONString(brokerIdList),config, e);
            return ResultStatus.TOPIC_OPERATION_PARTITION_NUM_ILLEGAL;
        } catch (InvalidReplicationFactorException e) {
            LOGGER.error("class=TopicCommands||method=createTopic||errMsg={}||clusterDO={}||topicName={}||partitionNum={}||replicaNum={}||brokerIdList={}||config={}",
                    e.getMessage(), clusterDO, topicName,partitionNum,replicaNum,JSON.toJSONString(brokerIdList),config, e);
            return ResultStatus.BROKER_NUM_NOT_ENOUGH;
        } catch (TopicExistsException e) {
            LOGGER.error("class=TopicCommands||method=createTopic||errMsg={}||clusterDO={}||topicName={}||partitionNum={}||replicaNum={}||brokerIdList={}||config={}",
                    e.getMessage(), clusterDO, topicName,partitionNum,replicaNum,JSON.toJSONString(brokerIdList),config, e);
            return ResultStatus.TOPIC_OPERATION_TOPIC_EXISTED;
        } catch (InvalidTopicException e) {
            LOGGER.error("class=TopicCommands||method=createTopic||errMsg={}||clusterDO={}||topicName={}||partitionNum={}||replicaNum={}||brokerIdList={}||config={}",
                    e.getMessage(), clusterDO, topicName,partitionNum,replicaNum,JSON.toJSONString(brokerIdList),config, e);
            return ResultStatus.TOPIC_OPERATION_TOPIC_NAME_ILLEGAL;
        } catch (Throwable t) {
            LOGGER.error("class=TopicCommands||method=createTopic||errMsg={}||clusterDO={}||topicName={}||partitionNum={}||replicaNum={}||brokerIdList={}||config={}",
                    t.getMessage(), clusterDO, topicName,partitionNum,replicaNum,JSON.toJSONString(brokerIdList),config, t);
            return ResultStatus.TOPIC_OPERATION_UNKNOWN_ERROR;
        }
        return ResultStatus.SUCCESS;
    }

    public static ResultStatus deleteTopic(ClusterDO clusterDO, String topicName) {
        try {
            AdminZkClient adminZkClient = KafkaClientPool.getAdminZkClient(clusterDO.getId());
            adminZkClient.deleteTopic(topicName);
        } catch (UnknownTopicOrPartitionException e) {
            LOGGER.error("class=TopicCommands||method=deleteTopic||errMsg={}||clusterDO={}||topicName={}", e.getMessage(), clusterDO, topicName, e);
            return ResultStatus.TOPIC_OPERATION_UNKNOWN_TOPIC_PARTITION;
        } catch (Throwable t) {
            LOGGER.error("class=TopicCommands||method=deleteTopic||errMsg={}||clusterDO={}||topicName={}", t.getMessage(), clusterDO, topicName, t);
            return ResultStatus.TOPIC_OPERATION_UNKNOWN_ERROR;
        }
        return ResultStatus.SUCCESS;
    }

    public static ResultStatus modifyTopicConfig(ClusterDO clusterDO, String topicName, Properties config) {
        try {
            AdminZkClient adminZkClient = KafkaClientPool.getAdminZkClient(clusterDO.getId());
            adminZkClient.changeTopicConfig(topicName, config);
        } catch (AdminOperationException e) {
            LOGGER.error("class=TopicCommands||method=modifyTopicConfig||errMsg={}||clusterDO={}||topicName={}||config={}", e.getMessage(), clusterDO, topicName,config, e);
            return ResultStatus.TOPIC_OPERATION_UNKNOWN_TOPIC_PARTITION;
        } catch (InvalidConfigurationException e) {
            LOGGER.error("class=TopicCommands||method=modifyTopicConfig||errMsg={}||clusterDO={}||topicName={}||config={}", e.getMessage(), clusterDO, topicName,config, e);
            return ResultStatus.TOPIC_OPERATION_TOPIC_CONFIG_ILLEGAL;
        } catch (Throwable t) {
            LOGGER.error("class=TopicCommands||method=modifyTopicConfig||errMsg={}||clusterDO={}||topicName={}||config={}", t.getMessage(), clusterDO, topicName,config, t);
            return ResultStatus.TOPIC_OPERATION_UNKNOWN_ERROR;
        }

        return ResultStatus.SUCCESS;
    }

    public static ResultStatus expandTopic(ClusterDO clusterDO,
                                           String topicName,
                                           Integer partitionNum,
                                           List<Integer> brokerIdList) {

        try {
            AdminZkClient adminZkClient = KafkaClientPool.getAdminZkClient(clusterDO.getId());
            KafkaZkClient kafkaZkClient = KafkaClientPool.getKafkaZkClient(clusterDO.getId());


            // 已有分区的分配策略
            scala.collection.Map<TopicPartition, ReplicaAssignment> existingAssignScalaMap = kafkaZkClient.getFullReplicaAssignmentForTopics(JavaConversions.asScalaBuffer(Arrays.asList(topicName)).<String>toSet());
            // 新增分区的分配策略
            ReplicaAssignment sample = existingAssignScalaMap.head()._2();

            Map<Object, Seq<Object>> newAssigments = JavaConverters.mapAsJavaMapConverter(AdminUtils.assignReplicasToBrokers(
                    convert2BrokerMetadataSeq(brokerIdList),
                    partitionNum,
                    sample.replicas().size(),
                    randomFixedStartIndex(),
                    existingAssignScalaMap.size()
            )).asJava();


            Decorators.AsJava<Map<TopicPartition, ReplicaAssignment>> existingAssignJavaMap =
                    JavaConverters.mapAsJavaMapConverter(existingAssignScalaMap);

            //构造增量assigment 和存量 assigment参数
            scala.collection.mutable.HashMap<Object, ReplicaAssignment> replicaExistsAssignmentMap = new scala.collection.mutable.HashMap<Object, ReplicaAssignment>();

            for (Map.Entry<TopicPartition, ReplicaAssignment> entry : existingAssignJavaMap.asJava().entrySet()) {
                newAssigments.remove(entry.getKey().partition());
                replicaExistsAssignmentMap.put(entry.getKey().partition(), entry.getValue());
            }
            scala.collection.Map<Object, scala.collection.Seq<Object>> replicaAddAssignmentMap = JavaConverters.mapAsScalaMapConverter(newAssigments).asScala();



            // 更新ZK上的assign
            adminZkClient.addPartitions(topicName, replicaExistsAssignmentMap, convert2BrokerMetadataSeq(brokerIdList), partitionNum + existingAssignJavaMap.asJava().size(), Option.apply(replicaAddAssignmentMap), false);
        } catch (Throwable t) {
            LOGGER.error("class=TopicCommands||method=expandTopic||errMsg={}||clusterDO={}||topicName={}||partitionNum={}||brokerIdList={}"
                    , t.getMessage(), clusterDO, topicName, partitionNum, JSON.toJSONString(brokerIdList), t);
            return ResultStatus.TOPIC_OPERATION_UNKNOWN_ERROR;
        }

        return ResultStatus.SUCCESS;
    }

    private static Seq<BrokerMetadata> convert2BrokerMetadataSeq(List<Integer> brokerIdList) {
        List<BrokerMetadata> brokerMetadataList = new ArrayList<>();
        for (Integer brokerId: brokerIdList) {
            brokerMetadataList.add(new BrokerMetadata(brokerId, Option.<String>empty()));
        }
        return JavaConversions.asScalaBuffer(brokerMetadataList).toSeq();
    }

    /**
     * 生成一个伪随机数, 即随机选择一个起始位置的Broker
     */
    private static int randomFixedStartIndex() {
        return (int) System.currentTimeMillis() % 1013;
    }
}
