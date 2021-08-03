package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.pojo.BrooklinClusterMetricsDO;
import com.xiaojukeji.kafka.manager.dao.BrooklinClusterMetricsDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("brooklinClusterMetricsDao")
public class BrooklinClusterMetricsDaoImpl implements BrooklinClusterMetricsDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int batchAdd(List<BrooklinClusterMetricsDO> clusterMetricsList) {
        return sqlSession.insert("BrooklinClusterMetricsDao.batchAdd", clusterMetricsList);
    }

    @Override
    public List<BrooklinClusterMetricsDO> getClusterMetrics(long clusterId, Date startTime, Date endTime) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("clusterId", clusterId);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        return sqlSession.selectList("BrooklinClusterMetricsDao.getClusterMetrics", map);
    }

    @Override
    public int deleteBeforeTime(Date endTime) {
        return sqlSession.delete("BrooklinClusterMetricsDao.deleteBeforeTime", endTime);
    }
}
