package com.ims.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "work_items")
public class WorkItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String componentId;
    private String status;

    private String rca;

    private Long createdTime;
    private Long resolvedTime;
    private Long mttr;

    public WorkItem() {}

    public Long getId() {
        return id;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRca() {
        return rca;
    }

    //  FIXED
    public void setRca(String rca) {
        this.rca = rca;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public Long getResolvedTime() {
        return resolvedTime;
    }

    public void setResolvedTime(Long resolvedTime) {
        this.resolvedTime = resolvedTime;
    }

    public Long getMttr() {
        return mttr;
    }

    public void setMttr(Long mttr) {
        this.mttr = mttr;
    }
}