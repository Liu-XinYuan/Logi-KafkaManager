package com.xiaojukeji.kafka.manager.service.service;


import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.BrooklinClusterDetailDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.BrooklinClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.BrooklinClusterMetricsDO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Cluster Service
 * @author liuxinyuan
 * @date 19/4/3
 */
public interface BrooklinClusterService {
    BrooklinClusterDO getById(Long clusterId);

    ResultStatus addNew(BrooklinClusterDO clusterDO, String operator);

    ResultStatus updateById(BrooklinClusterDO clusterDO, String operator);

    ResultStatus modifyStatus(Long clusterId, Integer status, String operator);

    List<BrooklinClusterDO> list();

    Map<Long, BrooklinClusterDO> listMap();

    List<BrooklinClusterDO> listAll();

    List<BrooklinClusterMetricsDO> getClusterMetricsFromDB(Long clusterId, Date startTime, Date endTime);

    BrooklinClusterDetailDTO getClusterDetailDTO(Long clusterId, Boolean needDetail);

    List<BrooklinClusterDetailDTO> getClusterDetailDTOList(Boolean needDetail);

    String getClusterName(Long logicClusterId);

    ResultStatus deleteById(Long clusterId, String operator);

}
