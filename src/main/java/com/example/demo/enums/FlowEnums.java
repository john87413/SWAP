package com.example.demo.enums;

import lombok.Getter;

@Getter
public class FlowEnums {

    /**
     * 流程實例狀態
     */
    public enum FlowInstanceStatus {
        RUNNING,    // 運行中
        COMPLETED,  // 已完成
        CANCELED,   // 已取消
        REJECTED    // 已拒絕
    }

    /**
     * 節點實例狀態
     */
    public enum NodeInstanceStatus {
        RUNNING,    // 運行中
        COMPLETED,  // 已完成
        SKIPPED     // 已跳過
    }

    /**
     * 任務狀態
     */
    public enum TaskStatus {
        TODO,       // 待辦
        COMPLETED,  // 已完成 (批准/拒絕)
        CANCELED    // 已取消
    }

    /**
     * 任務操作 (用於 TaskService)
     */
    public enum TaskAction {
        APPROVE,    // 批准
        REJECT      // 拒絕
    }
}