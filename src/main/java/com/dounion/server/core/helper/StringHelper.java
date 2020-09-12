package com.dounion.server.core.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
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
     * url 格式化
     * @param path
     * @return
     */
    public static String urlFormat(String path){
        if(path == null){
            return null;
        }

        path = path.trim().replaceAll("/{2,}", "/");
        if(path.endsWith("/")){
            path = StringUtils.substring(path, 0, path.length()-1);
        }
        return path;
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

        path = StringUtils.replace(path, "/", File.separator);
        path = StringUtils.replace(path, "\\", File.separator);

        int separatorIndex = path.lastIndexOf(File.separator);
        return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
    }

    /**
     * 消息格式化
     * @param openToken
     * @param closeToken
     * @param text
     * @param args
     * @return
     */
    public static String parse(String openToken, String closeToken, String text, Object... args) {
        if (args == null || args.length <= 0) {
            return text;
        }
        int argsIndex = 0;

        if (text == null || text.isEmpty()) {
            return "";
        }
        char[] src = text.toCharArray();
        int offset = 0;
        // search open token
        int start = text.indexOf(openToken, offset);
        if (start == -1) {
            return text;
        }
        final StringBuilder builder = new StringBuilder();
        StringBuilder expression = null;
        while (start > -1) {
            if (start > 0 && src[start - 1] == '\\') {
                // this open token is escaped. remove the backslash and continue.
                builder.append(src, offset, start - offset - 1).append(openToken);
                offset = start + openToken.length();
            } else {
                // found open token. let's search close token.
                if (expression == null) {
                    expression = new StringBuilder();
                } else {
                    expression.setLength(0);
                }
                builder.append(src, offset, start - offset);
                offset = start + openToken.length();
                int end = text.indexOf(closeToken, offset);
                while (end > -1) {
                    if (end > offset && src[end - 1] == '\\') {
                        // this close token is escaped. remove the backslash and continue.
                        expression.append(src, offset, end - offset - 1).append(closeToken);
                        offset = end + closeToken.length();
                        end = text.indexOf(closeToken, offset);
                    } else {
                        expression.append(src, offset, end - offset);
                        offset = end + closeToken.length();
                        break;
                    }
                }
                if (end == -1) {
                    // close token was not found.
                    builder.append(src, start, src.length - start);
                    offset = src.length;
                } else {
                    String value = (argsIndex <= args.length - 1) ?
                            (args[argsIndex] == null ? "" : args[argsIndex].toString()) : expression.toString();
                    builder.append(value);
                    offset = end + closeToken.length();
                    argsIndex++;
                }
            }
            start = text.indexOf(openToken, offset);
        }
        if (offset < src.length) {
            builder.append(src, offset, src.length - offset);
        }
        return builder.toString();
    }

    /**
     * ${}替换
     * @param text
     * @param args
     * @return
     */
    public static String parse0(String text, Object... args) {
        return parse("${", "}", text, args);
    }


    /**
     * {}替换
     * @param text
     * @param args
     * @return
     */
    public static String parse1(String text, Object... args) {
        return parse("{", "}", text, args);
    }


    /**
     * 格式化json输出
     * @param json
     * @return
     */
    public static String jsonFormatString(Object json){
        return JSON.toJSONString(json, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.DisableCircularReferenceDetect);
    }

}
