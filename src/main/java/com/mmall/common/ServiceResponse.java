package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//保证序列化Json时,如果是null对象,key也会消失
public class ServiceResponse<T> implements Serializable{
    private int status;
    private String msg;
    private T data;
    private ServiceResponse(int status){
        this.status = status;
    }
    private ServiceResponse(int status, String msg){
        this.status = status;
        this.msg = msg;
    }
    private ServiceResponse(int status, String msg, T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
    private ServiceResponse(int status,T data){
        this.data = data;
        this.status = status;
    }
    @JsonIgnore
    //使之不在Json序列化结果当中
    public boolean isSuccess(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }
    public int getStatus(){
        return status;
    }
    public String getMsg(){
        return msg;
    }
    public T getData(){
        return data;
    }
    public static <T> ServiceResponse<T> createBySuccess(){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode());
    }
    public static <T> ServiceResponse<T> createBySuccessMsg(String msg){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }
    //当data数据是String类型时，因为参数是T类型所以会调用private ServiceResponse(int status,T data)方法，
    //而不会调用private ServiceResponse(int status, String msg)
    public static <T> ServiceResponse<T> createBySuccess(T data){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }
    public static <T> ServiceResponse<T> createBySuccess(String message,T data){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getCode(),message,data);
    }
    //错误响应
    public static <T> ServiceResponse<T> createByError(){
        //返回错误代码和普通错误响应消息"error"
        return new ServiceResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }
    //自定义响应消息
    public static <T> ServiceResponse<T> createByErrorMessage(String errorMessage){
        return new ServiceResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }
    public static <T> ServiceResponse<T> createByErrorCodeMessage(int errorCode, String errorMessage){
        return new ServiceResponse<T>(errorCode,errorMessage);
    }

}
