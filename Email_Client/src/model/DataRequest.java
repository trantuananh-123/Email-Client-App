/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author tom18
 */
public class DataRequest implements Serializable {

    private static final long serialVersionUID = 2L;

    private String methodName;
    private User user;
    private Object data;
    private Integer type;
    private Integer page = 0;
    private Integer size = 20;

    public DataRequest() {
    }

    public DataRequest(String methodName, User user, Object data) {
        this.methodName = methodName;
        this.data = data;
        this.user = user;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "DataRequest{" + "methodName=" + methodName + ", data=" + data + '}';
    }

}
