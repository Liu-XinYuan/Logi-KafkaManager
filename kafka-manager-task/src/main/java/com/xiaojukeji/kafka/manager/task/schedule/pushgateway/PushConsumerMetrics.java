package com.xiaojukeji.kafka.manager.task.schedule.pushgateway;

import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.common.entity.ao.consumer.ConsumeSummaryDTO;
import com.xiaojukeji.kafka.manager.service.cache.KafkaMetricsCache;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.utils.PushGatewayUtils;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PushConsumerMetrics {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_PUSH_TASK_LOGGER);
    private Map<Long, String> clusterMap = new HashMap<>();
    private Gauge lag = Gauge.build().name("lag")
            .help("lag")
            .labelNames("clusterName", "consumerGroup", "topic")
            .register();

    @Autowired
    private ClusterService clusterService;


    @Scheduled(cron = "0/5 * * * * ?")
    public void pushConsumerMetrics() {
        long startTime = System.currentTimeMillis();
        LOGGER.info("push consumer-metrics start.");
        PushGateway pushGateway = PushGatewayUtils.getPushGateway();
        CollectorRegistry defaultRegistry = CollectorRegistry.defaultRegistry;
        Map<Long, List<ConsumeSummaryDTO>> allConsumerMetricsToCache = KafkaMetricsCache.getAllConsumerMetricsToCache();
        recordMetrics(allConsumerMetricsToCache,defaultRegistry);
        try {
            pushGateway.pushAdd(defaultRegistry, "kafka-cdp-metrics");
        } catch (IOException e) {
            LOGGER.error("push consumer-metrics IO ERROR",e);
        }

        LOGGER.info("push consumer-metrics finished, costTime:{}.", System.currentTimeMillis() - startTime);
    }

    public void recordMetrics(Map<Long, List<ConsumeSummaryDTO>> allConsumerMetrics, CollectorRegistry defaultRegistry) {
        for (Long clusterId : allConsumerMetrics.keySet()) {
            List<ConsumeSummaryDTO> consumeSummaryDTOS = allConsumerMetrics.get(clusterId);
            for (ConsumeSummaryDTO consumeSummaryDTO : consumeSummaryDTOS) {
                String clusterName = clusterMap.getOrDefault(clusterId, clusterService.getById(clusterId).getClusterName());
                Gauge.Child child = lag.labels(clusterName, consumeSummaryDTO.getConsumerGroup(), consumeSummaryDTO.getTopicName());
                child.set(consumeSummaryDTO.getOffset() - consumeSummaryDTO.getConsumeOffset());
            }
        }
    }
}
