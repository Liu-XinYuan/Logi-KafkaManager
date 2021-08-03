package com.xiaojukeji.kafka.manager.dao;



import com.xiaojukeji.kafka.manager.common.entity.pojo.BrooklinClusterMetricsDO;

import java.util.Date;
import java.util.List;

public interface BrooklinClusterMetricsDao {
    int batchAdd(List<BrooklinClusterMetricsDO> clusterMetricsList);

    List<BrooklinClusterMetricsDO> getClusterMetrics(long clusterId, Date startTime, Date endTime);

    int deleteBeforeTime(Date endTime);
}
