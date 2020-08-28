package com.dounion.server.core.request;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回结果构造器
 */
public class ResponseBuilder {

    private final static int SUCCESS_CODE = 0;
    private final static String SUCCESS_MESSAGE = "成功";
    private final static int FAIL_CODE = -1;
    private final static String FAIL_MESSAGE = "失败";

    /**
     * 构建返回结果
     * @param code
     * @param message
     * @param data
     * @return
     */
    public static Map<String, Object> buildResponse(int code, String message, Object data){
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("message", message);
        result.put("data", data);
        return result;
    }

    /**
     * 简易成功信息
     * @param message
     * @param data
     * @return
     */
    public static Map<String, Object> buildSuccess(String message, Object data){
        return buildResponse(SUCCESS_CODE, message, data);
    }

    /**
     * 简易成功信息
     * @param data
     * @return
     */
    public static Map<String, Object> buildSuccess(Object data){
        return buildSuccess(SUCCESS_MESSAGE, data);
    }

    /**
     * 简易成功信息
     * @param message
     * @return
     */
    public static Map<String, Object> buildSuccess(String message){
        return buildSuccess(message, null);
    }


    /**
     * 简易成功信息
     * @return
     */
    public static Map<String, Object> buildSuccess(){
        return buildSuccess(SUCCESS_MESSAGE, null);
    }



    /**
     * 简易失败信息
     * @param code
     * @param message
     * @return
     */
    public static Map<String, Object> buildError(int code, String message){
        return buildResponse(code, message, null);
    }


    /**
     * 简易失败信息
     * @param message
     * @param data
     * @return
     */
    public static Map<String, Object> buildError(String message, Object data){
        return buildResponse(FAIL_CODE, message, data);
    }

    /**
     * 简易失败信息
     * @param message
     * @return
     */
    public static Map<String, Object> buildError(String message){
        return buildError(message, null);
    }

    /**
     * 简易失败信息
     * @return
     */
    public static Map<String, Object> buildError(){
        return buildError(FAIL_MESSAGE, null);
    }
}
