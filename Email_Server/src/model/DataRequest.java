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

    private String methodName;
    private Object data;

    public DataRequest() {
    }

    public DataRequest(String methodName, Object data) {
        this.methodName = methodName;
        this.data = data;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DataRequest{" + "methodName=" + methodName + ", data=" + data + '}';
    }
}
