package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.pojo.BrooklinClusterDO;

import java.util.List;

public interface BrooklinClusterDao {
    int insert(BrooklinClusterDO brooklinClusterDO);

    int deleteById(Long id);

    int updateById(BrooklinClusterDO brooklinClusterDO);

    BrooklinClusterDO getById(Long id);

    List<BrooklinClusterDO> list();

    List<BrooklinClusterDO> listAll();
}
