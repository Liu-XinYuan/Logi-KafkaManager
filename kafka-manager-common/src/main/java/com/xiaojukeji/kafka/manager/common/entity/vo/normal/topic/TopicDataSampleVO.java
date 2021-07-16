package com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 19/4/3
 */
@ApiModel(value = "Topic采样数据")
public class TopicDataSampleVO {
    @ApiModelProperty(value = "Topic数据")
    private String value;
    @ApiModelProperty(value = "创建时间")
    private String createDate;
    @ApiModelProperty(value = "offset")
    private long offset;
    @ApiModelProperty(value = "msgKey")
    private String msgKey;

    @Override
    public String toString() {
        return "TopicDataSampleVO{" +
                "value='" + value + '\'' +
                ", createDate='" + createDate + '\'' +
                ", offset=" + offset +
                ", key='" + msgKey + '\'' +
                '}';
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public String getMsgKey() {
        return msgKey;
    }

    public void setMsgKey(String msgKey) {
        this.msgKey = msgKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
