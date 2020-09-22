package com.kma.securechatapp.core.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreateGroup {
    @SerializedName("user_uuid")
    @Expose
    private String userUuid;
    @SerializedName("thread_name")
    @Expose
    private Object threadName;
    @SerializedName("create_at")
    @Expose
    private String createAt;
    @SerializedName("last_msg_at")
    @Expose
    private String lastMsgAt;
    @SerializedName("users")
    @Expose
    private List<UserInfo> users = null;

    public CreateGroup(String userUuid) {
        this.userUuid = userUuid;
    }

    public CreateGroup(String userUuid, Object threadName, String createAt, String lastMsgAt, List<UserInfo> users) {
        this.userUuid = userUuid;
        this.threadName = threadName;
        this.createAt = createAt;
        this.lastMsgAt = lastMsgAt;
        this.users = users;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public Object getThreadName() {
        return threadName;
    }

    public void setThreadName(Object threadName) {
        this.threadName = threadName;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getLastMsgAt() {
        return lastMsgAt;
    }

    public void setLastMsgAt(String lastMsgAt) {
        this.lastMsgAt = lastMsgAt;
    }

    public List<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfo> users) {
        this.users = users;
    }
}
