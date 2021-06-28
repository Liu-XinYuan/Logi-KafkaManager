package com.xiaojukeji.kafka.manager.task.schedule;


import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumeSummaryDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.cache.KafkaMetricsCache;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.ConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlushConsumerMetrics {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);
    @Autowired
    private ClusterService clusterService;
    @Autowired
    private ConsumerService consumerService;

    @Scheduled(cron = "0/5 * * * * ?")
    public void flushConsumerMetrics() {
        long startTime = System.currentTimeMillis();
        LOGGER.info("flush consumer-metrics start.");

        List<ClusterDO> clusterDOList = clusterService.list();
        for (ClusterDO clusterDO : clusterDOList) {
            try {
                flushConsumerMetrics(clusterDO.getId());
            } catch (Exception e) {
                LOGGER.error("flush consumer-metrics failed, clusterId:{}.", clusterDO.getId(), e);
            }
        }
        LOGGER.info("flush consumer-metrics finished, costTime:{}.", System.currentTimeMillis() - startTime);
    }

    private void flushConsumerMetrics(Long clusterId) {
        ClusterDO clusterDO = clusterService.getById(clusterId);
        if (ValidateUtils.isNull(clusterDO)) {
            return;
        }
        try {
            List<ConsumeSummaryDTO> consumeDetailDTOList =
                    consumerService.getConsumeDetail(clusterDO);
            KafkaMetricsCache.putConsumerMetricsToCache(clusterId, consumeDetailDTOList);
        } catch (Exception e) {
            LOGGER.error("get consume detail failed, consumerGroup:all.", e);
        }
    }
}
