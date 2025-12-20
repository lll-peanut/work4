package com.peanut.POJO;

/**
 * 通用异步任务结果类
 * @author: peanut
 * @date: 2025/12/20
 * @version:1.0
 */
public class AsyncTaskResult<T> {

    // 任务是否成功
    private boolean success;

    // 错误信息（失败时非空）
    private String errorMsg;

    // 业务数据（成功时非空，如视频ID）
    private T data;

    // 任务唯一标识（用于排查）
    private String taskId;

    // 静态构造方法
    public static <T> AsyncTaskResult<T> success(T data, String taskId) {
        AsyncTaskResult<T> result = new AsyncTaskResult<>();
        result.setSuccess(true);
        result.setData(data);
        result.setTaskId(taskId);
        return result;
    }

    public static <T> AsyncTaskResult<T> fail(String errorMsg, String taskId) {
        AsyncTaskResult<T> result = new AsyncTaskResult<>();
        result.setSuccess(false);
        result.setErrorMsg(errorMsg);
        result.setTaskId(taskId);
        return result;
    }
    public static <T> AsyncTaskResult<T> process(T data, String taskId) {
        AsyncTaskResult<T> result = new AsyncTaskResult<>();
        result.setSuccess(true);
        result.setData(data);
        result.setTaskId(taskId);
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}