package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.bizenum.TaskStatusReassignEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.TopicReassignActionEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.reassign.ReassignStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignExecDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignExecSubDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignTopicDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ReassignTaskDO;
import kafka.zk.KafkaZkClient;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.common.TopicPartition;

import java.util.List;
import java.util.Map;

/**
 * migrate topic service
 * @author zengqiao_cn@163.com
 * @date 19/4/16
 */
public interface ReassignService {
    ResultStatus createTask(List<ReassignTopicDTO> dtoList, String operator);

    ResultStatus modifyTask(ReassignExecDTO dto, TopicReassignActionEnum actionEnum);

    ResultStatus modifySubTask(ReassignExecSubDTO dto);

    List<ReassignTaskDO> getReassignTaskList();

    List<ReassignTaskDO> getTask(Long taskId);

    Result<List<ReassignStatus>> getReassignStatus(Long taskId);

    Map<TopicPartition, TaskStatusReassignEnum> verifyAssignment(Long clusterId, String reassignmentJson);

    Map<TopicPartition, TaskStatusReassignEnum> verifyAssignment(AdminClient adminClient, KafkaZkClient zkClient, String reassignmentJson);


}
