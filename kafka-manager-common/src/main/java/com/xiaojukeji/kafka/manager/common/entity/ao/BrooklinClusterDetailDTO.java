package com.xiaojukeji.kafka.manager.common.entity.ao;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/4/23
 */
public class BrooklinClusterDetailDTO {
    private Long clusterId;

    private String clusterName;

    private String zookeeper;

    private String brooklinServers;

    private String idc;

    private Integer mode;

    private String jmxProperties;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;

    private Integer connectorNum;

    private Integer transportNum;

    private Integer datastreamNum;

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

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public String getJmxProperties() {
        return jmxProperties;
    }

    public void setJmxProperties(String jmxProperties) {
        this.jmxProperties = jmxProperties;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }

    public Integer getBrokerNum() {
        return connectorNum;
    }

    public void setServerNum(Integer serverNum) {
        this.connectorNum = serverNum;
    }

    public Integer getTransportNum() {
        return transportNum;
    }

    public void setTransportNum(Integer transportNum) {
        this.transportNum = transportNum;
    }

    public Integer getDatastreamNum() {
        return datastreamNum;
    }

    public void setDatastreamNum(Integer datastreamNum) {
        this.datastreamNum = datastreamNum;
    }


    @Override
    public String toString() {
        return "ClusterDetailDTO{" +
                "clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", zookeeper='" + zookeeper + '\'' +
                ", bootstrapServers='" + brooklinServers + '\'' +
                ", idc='" + idc + '\'' +
                ", mode=" + mode +
                ", jmxProperties='" + jmxProperties + '\'' +
                ", status=" + status +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                ", brokerNum=" + connectorNum +
                ", topicNum=" + transportNum +
                ", consumerGroupNum=" + datastreamNum +
                '}';
    }
}
