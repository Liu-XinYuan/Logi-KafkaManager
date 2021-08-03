package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.pojo.BrooklinClusterDO;
import com.xiaojukeji.kafka.manager.dao.BrooklinClusterDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository("brooklinClusterDao")
public class BrooklinClusterDaoImpl implements BrooklinClusterDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }


    @Override
    public int insert(BrooklinClusterDO brooklinClusterDO) {
        return sqlSession.insert("BrooklinClusterDao.insert", brooklinClusterDO);
    }

    @Override
    public int deleteById(Long id) {
        return sqlSession.delete("BrooklinClusterDao.deleteById", id);
    }

    @Override
    public int updateById(BrooklinClusterDO brooklinClusterDO) {
        return sqlSession.update("BrooklinClusterDao.updateById", brooklinClusterDO);
    }

    @Override
    public BrooklinClusterDO getById(Long id) {
        return sqlSession.selectOne("BrooklinClusterDao.getById", id);
    }

    @Override
    public List<BrooklinClusterDO> list() {
        return sqlSession.selectList("BrooklinClusterDao.list");
    }

    @Override
    public List<BrooklinClusterDO> listAll() {
        return sqlSession.selectList("BrooklinClusterDao.listAll");
    }
}
