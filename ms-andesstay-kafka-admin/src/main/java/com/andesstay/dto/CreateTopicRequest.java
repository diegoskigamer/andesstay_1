package com.andesstay.dto;

public class CreateTopicRequest {
    private String name;
    private Integer partitions = 3;
    private Short replicas = 1;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getPartitions() { return partitions; }
    public void setPartitions(Integer partitions) { this.partitions = partitions; }
    public Short getReplicas() { return replicas; }
    public void setReplicas(Short replicas) { this.replicas = replicas; }
}
