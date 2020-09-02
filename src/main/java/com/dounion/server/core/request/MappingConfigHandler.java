package com.dounion.server.core.request;

import com.alibaba.fastjson.JSON;
import com.dounion.server.core.base.BaseException;
import com.dounion.server.core.base.Constant;
import com.dounion.server.core.exception.SystemException;
import com.dounion.server.core.helper.BeanHelper;
import com.dounion.server.core.helper.FileHelper;
import com.dounion.server.core.helper.StringHelper;
import com.dounion.server.core.request.annotation.RequestMapping;
import com.dounion.server.core.request.annotation.ResponseType;
import com.dounion.server.eum.ResponseTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static com.dounion.server.core.helper.SpringApp.getInstance;

/**
 * 请求路由处理器
 */
public class MappingConfigHandler {

    private final static Logger logger = LoggerFactory.getLogger(MappingConfigHandler.class);


    // 私有化构造方法
    private MappingConfigHandler(){

    }

    // 参数名称发现器
    private final static ParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    public MappingConfigHandler(Object bean, Method method) {
        this(bean, method, method.getName());
    }

    public MappingConfigHandler(Object bean, Method method, String desc){
        this.obj = bean;
        this.method = method;
        this.desc = desc;

        int count = this.method.getParameterTypes().length;
        this.methodParameters = new MethodParameter[count];
        for (int i = 0; i < count; i++) {
            this.methodParameters[i] = new MethodParameter(this.method, i);
            this.methodParameters[i].initParameterNameDiscovery(discoverer);
        }

        // 获取返回类型
        ResponseType responseAnnotation = method.getAnnotation(ResponseType.class);
        // 未配置按静态页面返回
        if(responseAnnotation == null){
            if(method.getReturnType().equals(String.class)){
                this.responseType = ResponseTypeEnum.HTML;
            }
        } else {
            this.responseType = responseAnnotation.value();
            switch (this.responseType) {
                case FILE:
                    if(!method.getReturnType().equals(File.class)){
                        throw new SystemException("Illegal class config of @ResponseType,it must be java.io.File");
                    }
                    break;
                case HTML:
                    if(!method.getReturnType().equals(String.class)){
                        throw new SystemException("Illegal class config of @ResponseType,it must be java.lang.String");
                    }
                    break;
                case JSON:
                    // it's all ok
                    break;
                default:
                    throw new SystemException("this exception should be never throw...");
            }
        }
    }


    private Object obj;
    private Method method;
    private String path;
    private String desc;
    private MethodParameter[] methodParameters;
    private ResponseTypeEnum responseType;

    public <T> T invoke(Map<String, Object> params) throws Exception {

        if(this.obj == null || this.method == null){
            return null;
        }

        Object[] args = new Object[this.methodParameters.length];
        if(this.methodParameters.length > 0){
            for(int i=0; i<this.methodParameters.length; i++){
                MethodParameter parameter = this.methodParameters[i];
                // 字符串类型/文件类型特殊处理
                if(parameter.getParameterType().equals(String.class) ||
                        parameter.getParameterType().equals(File.class)){
                    args[i] = params.get(parameter.getParameterName());
                    continue;
                }
                // int 类型
                if(parameter.getParameterType().equals(int.class)){
                    args[i] = Integer.parseInt(String.valueOf(params.get(parameter.getParameterName())));
                    continue;
                }
                // java.util.Map
                if(parameter.getParameterType().equals(Map.class) ||
                        parameter.getParameterType().isAssignableFrom(Map.class)){
                    args[i] = params;
                    continue;
                }

                // 引用类型对象转换
                args[i] = BeanHelper.mapToObject(params, parameter.getParameterType());
            }
        }

        return (T) this.method.invoke(this.obj, args);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.obj).append(".").append(this.method);
        return sb.toString();
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public MethodParameter[] getMethodParameters(){
        return this.methodParameters;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ResponseTypeEnum getResponseType() {
        return responseType;
    }

    // ================================ static method ============================================

    public static Map<URI, MappingConfigHandler> mapping = new HashMap<>();

    public static void initialization(){
        Map<String, Object> map = getInstance().getObjectByAnnotationType(Controller.class);
        for(String key : map.keySet()){
            Object o = map.get(key);

            // 遍历方法
            Method[] methods = o.getClass().getDeclaredMethods();
            if(methods == null || methods.length==0){
                continue;
            }
            String classUrlMapping = "", classDesc = "";
            if(o.getClass().isAnnotationPresent(RequestMapping.class)){
                classUrlMapping = o.getClass().getAnnotation(RequestMapping.class).value();
                classDesc = o.getClass().getAnnotation(RequestMapping.class).name();
            }

            URI uri;
            MappingConfigHandler config;
            StringBuffer urlBuffer = new StringBuffer();
            StringBuffer nameBuffer = new StringBuffer();
            for(Method method : methods){

                // 初始化
                urlBuffer.setLength(0);
                urlBuffer.append(classUrlMapping);
                nameBuffer.setLength(0);
                nameBuffer.append(classDesc);
                if(StringUtils.isNotBlank(classDesc)){
                    nameBuffer.append("-");
                }

                if(!method.isAnnotationPresent(RequestMapping.class)){
                    continue;
                }
                RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                urlBuffer.append(StringHelper.urlAppend(null, annotation.value()));
                nameBuffer.append(StringUtils.isBlank(annotation.name()) ? method : annotation.name());
                uri = URI.create(urlBuffer.toString());
                config = new MappingConfigHandler(o, method, nameBuffer.toString());
                config.setPath(uri.getPath());
                mapping.put(uri, config);

                logger.debug("url:【{}】,method:【{}】", uri.getPath(), method);
            }
        }
    }

    /**
     * 是否是已注册的请求地址
     * @param url
     * @return
     */
    public static Boolean isMapping(String url){
        URI request;
        try {
            request = new URI(url);
        } catch (URISyntaxException e) {
            return false;
        }
        return isMapping(request);
    }

    /**
     * 是否是已注册的请求地址
     * @param request
     * @return
     */
    public static Boolean isMapping(URI request){

        boolean flag = false;
        for(URI uri : mapping.keySet()){
            if(StringUtils.equals(uri.getPath(),request.getPath())){
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 对应业务处理
     * @param path
     * @param params
     * @return
     * @throws Exception
     */
    public static FullHttpResponse handlerMethod(String path, Map<String, Object> params) throws Exception {

        URI requestUri = URI.create(path);

        // 默认返回 response
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.EMPTY_BUFFER);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        // 未找到匹配的urlMapping
        if(!isMapping(requestUri)){
            response.setStatus(HttpResponseStatus.NOT_FOUND);
            HttpUtil.setContentLength(response, 0);
            return response;
        }

        MappingConfigHandler config = null;
        ByteBuf buf = Unpooled.EMPTY_BUFFER;
        Exception error = null;
        Object result;
        try {
            config = mapping.get(requestUri);
            if(config==null || config.responseType == null){
                response.setStatus(HttpResponseStatus.NOT_FOUND);
                HttpUtil.setContentLength(response, 0);
                return response;
            }

            result = config.invoke(params);
            if(result == null){
                return response;
            }

            switch (config.responseType){
                case JSON: // json
                    buf = Unpooled.copiedBuffer(JSON.toJSONString(result), CharsetUtil.UTF_8);
                    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
                    break;
                case HTML: // html
                    String filePath = Constant.PATH_PAGE + result;
                    if(!new File(filePath).exists()){
                        response.setStatus(HttpResponseStatus.NOT_FOUND);
                        HttpUtil.setContentLength(response, 0);
                        return response;
                    }
                    byte[] fileBytes = FileHelper.getFile(filePath);
                    buf = Unpooled.copiedBuffer(fileBytes);
                    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
                    break;
                case FILE: // 文件下载
                    File file = (File) result;
                    buf = Unpooled.copiedBuffer(Files.readAllBytes(file.toPath()));
                    response.headers().set(HttpHeaderNames.CONTENT_DISPOSITION, "attachment;filename=" + file.getName());
                    response.headers().set(HttpHeaderNames.CONTENT_TYPE, Constant.CONTENT_TYPE_FILE + "; charset=UTF-8");
                    break;
                default:
                    break;
            }

        } catch (BaseException e) {
            String message = JSON.toJSONString(ResponseBuilder.buildError(e.getCode(), e.getMsg()));
            buf = Unpooled.copiedBuffer(message, CharsetUtil.UTF_8);
            logger.error("{} fail,{}", config, e);
        } catch (Exception e) {
            logger.error("{} error,{}", config, e);
            error = e;
        }

        if(error != null){
            response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            buf = Unpooled.copiedBuffer(error.getMessage(), CharsetUtil.UTF_8);
        }

        HttpUtil.setContentLength(response, buf.readableBytes());

        return response.replace(buf);
    }
}
