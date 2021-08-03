package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.bizenum.ModuleEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.OperateEnum;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.BrooklinClusterDetailDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.*;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.dao.*;
import com.xiaojukeji.kafka.manager.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * BrooklinClusterServiceImpl
 * @author liuxinyuan
 * @date 19/4/3
 */
@Service("BrooklinClusterService")
public class BrooklinClusterServiceImpl implements BrooklinClusterService {
    private final static Logger LOGGER = LoggerFactory.getLogger(BrooklinClusterServiceImpl.class);
    @Autowired
    private BrooklinClusterDao brooklinClusterDao;

    @Autowired
    private OperateRecordService operateRecordService;

    @Autowired
    private BrooklinClusterMetricsDao brooklinClusterMetricsDao;

    @Override
    public BrooklinClusterDO getById(Long clusterId) {
        if (ValidateUtils.isNull(clusterId)) {
            return null;
        }
        try {
            return brooklinClusterDao.getById(clusterId);
        } catch (Exception e) {
            LOGGER.error("get cluster failed, clusterId:{}.", clusterId, e);
        }
        return null;
    }

    @Override
    public ResultStatus addNew(BrooklinClusterDO clusterDO, String operator) {
        if (ValidateUtils.isNull(clusterDO) || ValidateUtils.isNull(operator)) {
            return ResultStatus.PARAM_ILLEGAL;
        }
        try {
            Map<String, String> content = new HashMap<>();
            content.put("zk address", clusterDO.getZookeeper());
            content.put("brooklin servers", clusterDO.getBrooklinServers());
            content.put("jmx properties", clusterDO.getJmxProperties());
            operateRecordService.insert(operator, ModuleEnum.BROOKLIN_CLUSTER, clusterDO.getClusterName(), OperateEnum.ADD, content);
            if (brooklinClusterDao.insert(clusterDO) <= 0) {
                LOGGER.error("add new cluster failed, clusterDO:{}.", clusterDO);
                return ResultStatus.MYSQL_ERROR;
            }
        } catch (DuplicateKeyException e) {
            LOGGER.error("add new brooklin cluster failed, cluster already existed, clusterDO:{}.", clusterDO, e);
            return ResultStatus.RESOURCE_ALREADY_EXISTED;
        } catch (Exception e) {
            LOGGER.error("add new brooklin cluster failed, operate mysql failed, clusterDO:{}.", clusterDO, e);
            return ResultStatus.MYSQL_ERROR;
        }
        return ResultStatus.SUCCESS;
    }

    @Override
    public ResultStatus updateById(BrooklinClusterDO clusterDO, String operator) {
        if (ValidateUtils.isNull(clusterDO) || ValidateUtils.isNull(operator)) {
            return ResultStatus.PARAM_ILLEGAL;
        }

        BrooklinClusterDO originClusterDO = this.getById(clusterDO.getId());
        if (ValidateUtils.isNull(originClusterDO)) {
            return ResultStatus.CLUSTER_NOT_EXIST;
        }

        Map<String, String> content = new HashMap<>();
        content.put("brooklin cluster id", clusterDO.getId().toString());
        content.put("jmx properties", clusterDO.getJmxProperties());
        operateRecordService.insert(operator, ModuleEnum.BROOKLIN_CLUSTER, clusterDO.getClusterName(), OperateEnum.EDIT, content);

        clusterDO.setStatus(originClusterDO.getStatus());
        return updateById(clusterDO);
    }

    @Override
    public ResultStatus modifyStatus(Long clusterId, Integer status, String operator) {
        if (ValidateUtils.isNull(clusterId) || ValidateUtils.isNull(status)) {
            return ResultStatus.PARAM_ILLEGAL;
        }

        BrooklinClusterDO clusterDO = this.getById(clusterId);
        if (ValidateUtils.isNull(clusterDO)) {
            return ResultStatus.CLUSTER_NOT_EXIST;
        }
        clusterDO.setStatus(status);
        return updateById(clusterDO);
    }

    @Override
    public List<BrooklinClusterDO> list() {
        try {
            return brooklinClusterDao.list();
        } catch (Exception e) {
            LOGGER.error("list cluster failed.", e);
        }
        return new ArrayList<>();
    }

    @Override
    public Map<Long, BrooklinClusterDO> listMap() {
        List<BrooklinClusterDO> doList = this.list();
        Map<Long, BrooklinClusterDO> doMap = new HashMap<>();
        for (BrooklinClusterDO elem: doList) {
            doMap.put(elem.getId(), elem);
        }
        return doMap;
    }

    @Override
    public List<BrooklinClusterDO> listAll() {
        try {
            return brooklinClusterDao.listAll();
        } catch (Exception e) {
            LOGGER.error("list cluster failed.", e);
        }
        return new ArrayList<>();
    }

    @Override
    public List<BrooklinClusterMetricsDO> getClusterMetricsFromDB(Long clusterId, Date startTime, Date endTime) {
        return brooklinClusterMetricsDao.getClusterMetrics(clusterId, startTime, endTime);
    }

    @Override
    public BrooklinClusterDetailDTO getClusterDetailDTO(Long clusterId, Boolean needDetail) {
        BrooklinClusterDO clusterDO = this.getById(clusterId);
        if (ValidateUtils.isNull(clusterDO)) {
            return null;
        }
        return getClusterDetailDTO(clusterDO, needDetail);
    }

    @Override
    public List<BrooklinClusterDetailDTO> getClusterDetailDTOList(Boolean needDetail) {
        return null;
    }

    @Override
    public String getClusterName(Long logicClusterId) {
        return null;
    }

    @Override
    public ResultStatus deleteById(Long clusterId, String operator) {
        return null;
    }

    private ResultStatus updateById(BrooklinClusterDO clusterDO) {
        try {
            if (brooklinClusterDao.updateById(clusterDO) <= 0) {
                LOGGER.error("update cluster failed, clusterDO:{}.", clusterDO);
                return ResultStatus.MYSQL_ERROR;
            }
        } catch (Exception e) {
            LOGGER.error("update cluster failed, clusterDO:{}.", clusterDO, e);
            return ResultStatus.MYSQL_ERROR;
        }
        return ResultStatus.SUCCESS;
    }

    private BrooklinClusterDetailDTO getClusterDetailDTO(BrooklinClusterDO clusterDO, Boolean needDetail) {
        if (ValidateUtils.isNull(clusterDO)) {
            return new BrooklinClusterDetailDTO();
        }

        BrooklinClusterDetailDTO dto = new BrooklinClusterDetailDTO();
        dto.setClusterId(clusterDO.getId());
        dto.setClusterName(clusterDO.getClusterName());
        dto.setZookeeper(clusterDO.getZookeeper());
        dto.setBrooklinServers(clusterDO.getBrooklinServers());
        dto.setJmxProperties(clusterDO.getJmxProperties());
        dto.setStatus(clusterDO.getStatus());
        dto.setGmtCreate(clusterDO.getGmtCreate());
        dto.setGmtModify(clusterDO.getGmtModify());
        if (ValidateUtils.isNull(needDetail) || !needDetail) {
            return dto;
        }
        return dto;
    }
}
