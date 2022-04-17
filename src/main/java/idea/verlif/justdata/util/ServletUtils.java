package idea.verlif.justdata.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import idea.verlif.justdata.base.result.BaseResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servlet 工具类
 */
public class ServletUtils {

    private static final Pattern AGENT_INFO = Pattern.compile("(\\([^(]*\\))");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 获取 HttpServlet获取请求中的对象
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes == null ? null : attributes.getRequest();
    }

    /**
     * 获取 HttpServletResponse 对象
     *
     * @return HttpServletResponse
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes == null ? null : attributes.getResponse();
    }

    /**
     * 获取 HttpServletSession 对象
     *
     * @return HttpServletSession
     */
    public static HttpSession getSession() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes == null ? null : attributes.getRequest().getSession();
    }

    /**
     * 获取 获取请求中的请求参数
     *
     * @param paramName 参数名称
     * @return 参数内容
     */
    public static String getParameter(String paramName) {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        } else {
            return request.getParameter(paramName);
        }
    }

    /**
     * 获取 获取请求中的Body 请求参数
     *
     * @return 请求中的Body转换为的JSON对象
     */
    public static JsonNode getBodyParameters() {
        try {
            HttpServletRequest request = getRequest();
            if (request == null) {
                return MAPPER.nullNode();
            } else {
                InputStreamReader reader = new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8);
                char[] buff = new char[1024];
                int length;
                String body = null;
                while ((length = reader.read(buff)) != -1) {
                    body = new String(buff, 0, length);
                }
                return MAPPER.readTree(body);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Response 对象写出数据
     *
     * @param msg 消息数据
     */
    public static void write(String msg) throws IOException {
        HttpServletResponse response = getResponse();
        if (response == null) {
            return;
        }
        response.setHeader("Content-type", "application/json;charset=" + StandardCharsets.UTF_8);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(msg);
    }

    /**
     * Response 对象写出 JSON 数据
     *
     * @param data 消息数据
     */
    public static void writeJson(Object data) throws IOException {
        write(MAPPER.valueToTree(data).toString());
    }

    /**
     * 获取请求中的请求参数
     *
     * @return 请求参数字符串
     */
    public static String getQueryParam() {
        return getRequest().getQueryString();
    }

    /**
     * 获取请求中的请求地址
     *
     * @return 请求的URI路径
     */
    public static String getRequestURI() {
        return getRequest().getRequestURI();
    }

    /**
     * 获取请求中的客户端地址
     *
     * @return 请求来源地址
     */
    public static String getRemoteHost() {
        String remoteHost = getRequest().getRemoteHost();
        if ("0:0:0:0:0:0:0:1".equals(remoteHost)) {
            remoteHost = "127.0.0.1";
        }
        return remoteHost;
    }

    /**
     * 获取请求中的请求方法
     *
     * @return 请求方法
     */
    public static String getMethod() {
        return getRequest().getMethod();
    }

    /**
     * 获取请求中的请求头
     *
     * @param name 请求头参数名
     * @return 请求头参数内容；可能为null
     */
    public static String getHeader(String name) {
        return getRequest().getHeader(name);
    }

    /**
     * 获取请求中的Agent
     *
     * @return agent代理
     */
    public static String getAgent() {
        return getHeader("User-Agent");
    }

    /**
     * 获取浏览器信息
     *
     * @return 浏览器名及版本；可能为null
     */
    public static String getBrowser() {
        String userAgent = getAgent();
        String[] split = userAgent.split("\\(.*\\)");
        if (split.length > 1) {
            return split[split.length - 1].trim();
        }
        return null;
    }

    /**
     * 获取访问来源系统信息
     *
     * @return 操作系统及版本；可能为null
     */
    public static String getSystem() {
        String userAgent = getAgent();
        Matcher m = AGENT_INFO.matcher(userAgent);
        if (m.find() && m.groupCount() > 0) {
            String system = m.group(1);
            return system.substring(1, system.length() - 1);
        }
        return null;
    }

    /**
     * 将字符串渲染到客户端
     *
     * @param response 渲染对象
     * @param string   待渲染的字符串
     */
    public static void renderString(HttpServletResponse response, String string) {
        try {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向客户端发送结果响应
     *
     * @param response 请求响应
     * @param result   结果对象
     */
    public static void sendResult(HttpServletResponse response, BaseResult<?> result) {
        renderString(response, MAPPER.valueToTree(result).toString());
    }
}
