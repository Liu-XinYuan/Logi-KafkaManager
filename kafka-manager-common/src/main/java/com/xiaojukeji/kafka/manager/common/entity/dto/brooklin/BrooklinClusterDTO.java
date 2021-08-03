package com.xiaojukeji.kafka.manager.common.entity.dto.brooklin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/4/23
 */
@ApiModel(description = "brooklin集群接入&修改")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrooklinClusterDTO {
    @ApiModelProperty(value="集群Id, 修改时传")
    private Long clusterId;

    @ApiModelProperty(value="集群名称")
    private String clusterName;

    @ApiModelProperty(value="ZK地址, 不允许修改")
    private String zookeeper;

    @ApiModelProperty(value="bootstrap地址")
    private String brooklinServers;

    @ApiModelProperty(value="数据中心")
    private String idc;

    @ApiModelProperty(value="Jmx配置")
    private String jmxProperties;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(String zookeeper) {
        this.zookeeper = zookeeper;
    }

    public String getBrooklinServers() {
        return brooklinServers;
    }

    public void setBrooklinServers(String brooklinServers) {
        this.brooklinServers = brooklinServers;
    }

    public String getIdc() {
        return idc;
    }

    public void setIdc(String idc) {
        this.idc = idc;
    }

    public String getJmxProperties() {
        return jmxProperties;
    }

    public void setJmxProperties(String jmxProperties) {
        this.jmxProperties = jmxProperties;
    }

    @Override
    public String toString() {
        return "ClusterDTO{" +
                "clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", zookeeper='" + zookeeper + '\'' +
                ", brooklinServers='" + brooklinServers + '\'' +
                ", idc='" + idc + '\'' +
                ", jmxProperties='" + jmxProperties + '\'' +
                '}';
    }

    public boolean legal() {
        if (ValidateUtils.isNull(clusterName)
                || ValidateUtils.isNull(zookeeper)
                || ValidateUtils.isNull(idc)
                || ValidateUtils.isNull(brooklinServers)) {
            return false;
        }
        return true;
    }
}
