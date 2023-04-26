package com.fy.baselibrary.eventbus;

/**
 * description 发送事件 实体类
 * Created by fangs on 2023/4/26 14:54.
 */
public class EventBean<T> {

    private String event;
    private T data;

    public EventBean() {
    }

    public EventBean(String event, T data) {
        this.event = event;
        this.data = data;
    }

    public String getEvent() {
        return event == null ? "" : event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
