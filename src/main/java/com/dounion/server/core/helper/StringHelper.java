package com.dounion.server.core.helper;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.regex.Pattern;

public class StringHelper {

    public static final Pattern PATTER_HTML = Pattern.compile(".+\\.(html)", Pattern.CASE_INSENSITIVE);
    public static final Pattern PATTER_JS = Pattern.compile(".+\\.(js)", Pattern.CASE_INSENSITIVE);
    public static final Pattern PATTER_CSS = Pattern.compile(".+\\.(css)", Pattern.CASE_INSENSITIVE);
    public static final Pattern PATTER_IMAGES = Pattern.compile(".+\\.((png|ico|gif|jpg|jpeg|bmp|swf|swf))", Pattern.CASE_INSENSITIVE);

    public static boolean isStaticRequest(String url) {
        return PATTER_HTML.matcher(url).matches() ||
                    PATTER_JS.matcher(url).matches() ||
                    PATTER_CSS.matcher(url).matches() ||
                    PATTER_IMAGES.matcher(url).matches();
    }


    /**
     * 组装url
     * @return
     */
    public static String urlAppend(String sourcePath, String appendPath){

        if(StringUtils.isBlank(sourcePath)){
            sourcePath = "";
        }
        if(StringUtils.isBlank(appendPath)){
            appendPath = "";
        }

        appendPath = StringUtils.trim(appendPath);
        if(!StringUtils.startsWith(appendPath, "/")){
            appendPath = "/" + appendPath;
        }

        return StringUtils.trim(sourcePath) + appendPath;
    }

    /**
     * 获取请求路径
     *      如: http://www.baidu.com?wd=aaa&cd=123
     *      结果: http://www.baidu.com
     * @param uri
     * @return
     */
    public static String getRealPath(String uri){

        if(StringUtils.isBlank(uri)){
            return uri;
        }

        int inx = StringUtils.indexOf(uri, "?");
        if(inx != -1){
            uri = StringUtils.substring(uri, 0, inx);
        }

        if(StringUtils.endsWith(uri, "/")){
            uri = StringUtils.substring(uri, 0, uri.length()-1);
        }

        return uri;
    }


    /**
     * 根据文件路径获取文件名称
     * @param path
     * @return
     */
    public static String getFileName(String path){
        if(path  == null){
            return null;
        }

        int separatorIndex = path.lastIndexOf(File.separator);
        return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
    }

}
