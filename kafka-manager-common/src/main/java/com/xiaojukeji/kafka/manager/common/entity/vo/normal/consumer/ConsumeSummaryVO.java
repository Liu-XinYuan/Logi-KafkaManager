package com.xiaojukeji.kafka.manager.common.entity.vo.normal.consumer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.HashMap;
import java.util.Map;
@ApiModel(value = "消费组消费Topic聚合信息")
public class ConsumeSummaryVO  implements Comparable<ConsumeSummaryVO> {
    @ApiModelProperty(value = "消费组名称")
    private String consumerGroup;
    @ApiModelProperty(value = "lag")
    private Long lag = 0L;
    @ApiModelProperty(value = "topic lag")
    private Map<String, Long> topicLags = new HashMap<>();

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public Long getLag() {
        return lag;
    }

    public void setLag(Long lag) {
        this.lag = lag;
    }

    public Map<String, Long> getTopicLags() {
        return topicLags;
    }

    public void setTopicLags(Map<String, Long> topicLags) {
        this.topicLags = topicLags;
    }

    @Override
    public int compareTo(ConsumeSummaryVO o) {
        return (int)(o.getLag() - lag);
    }
}
