package com.supermarket.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.zip.GZIPOutputStream;

/**
 * API优化工具类
 * 提供响应压缩、分页、批量操作、异步处理等功能
 * 
 * @author SupermarketSystem
 * @version 1.0
 */
public class APIOptimizer {
    
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);
    
    /**
     * 分页响应包装类
     */
    public static class PagedResponse<T> {
        private List<T> data;
        private int page;
        private int size;
        private long total;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;
        
        public PagedResponse(List<T> data, int page, int size, long total) {
            this.data = data;
            this.page = page;
            this.size = size;
            this.total = total;
            this.totalPages = (int) Math.ceil((double) total / size);
            this.hasNext = page < totalPages;
            this.hasPrevious = page > 1;
        }
        
        // Getters and setters
        public List<T> getData() { return data; }
        public void setData(List<T> data) { this.data = data; }
        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }
        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
        public boolean isHasNext() { return hasNext; }
        public void setHasNext(boolean hasNext) { this.hasNext = hasNext; }
        public boolean isHasPrevious() { return hasPrevious; }
        public void setHasPrevious(boolean hasPrevious) { this.hasPrevious = hasPrevious; }
    }
    
    /**
     * API响应包装类
     */
    public static class APIResponse<T> {
        private boolean success;
        private String message;
        private T data;
        private String error;
        private long timestamp;
        
        public APIResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }
        
        public APIResponse(boolean success, String message, T data, String error) {
            this(success, message, data);
            this.error = error;
        }
        
        public static <T> APIResponse<T> success(T data) {
            return new APIResponse<>(true, "操作成功", data);
        }
        
        public static <T> APIResponse<T> success(String message, T data) {
            return new APIResponse<>(true, message, data);
        }
        
        public static <T> APIResponse<T> error(String message) {
            return new APIResponse<>(false, message, null);
        }
        
        public static <T> APIResponse<T> error(String message, String error) {
            return new APIResponse<>(false, message, null, error);
        }
        
        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
    
    /**
     * 发送JSON响应（支持GZIP压缩）
     */
    public static void sendJSONResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        
        String json;
        if (data instanceof APIResponse) {
            APIResponse<?> apiResponse = (APIResponse<?>) data;
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", apiResponse.isSuccess());
            responseMap.put("message", apiResponse.getMessage());
            responseMap.put("data", apiResponse.getData());
            responseMap.put("error", apiResponse.getError());
            responseMap.put("timestamp", apiResponse.getTimestamp());
            json = JsonUtil.mapToJson(responseMap);
        } else if (data instanceof Map) {
            json = JsonUtil.mapToJson((Map<String, Object>) data);
        } else {
            Map<String, Object> wrapper = new HashMap<>();
            wrapper.put("data", data);
            json = JsonUtil.mapToJson(wrapper);
        }
        
        // 如果响应数据较大，使用GZIP压缩
        if (json.length() > 1024) {
            response.setHeader("Content-Encoding", "gzip");
            try (GZIPOutputStream gzipOut = new GZIPOutputStream(response.getOutputStream())) {
                gzipOut.write(json.getBytes("UTF-8"));
            }
        } else {
            try (PrintWriter out = response.getWriter()) {
                out.print(json);
            }
        }
    }
    
    /**
     * 发送成功响应
     */
    public static void sendSuccessResponse(HttpServletResponse response, Object data) throws IOException {
        sendJSONResponse(response, APIResponse.success(data));
    }
    
    /**
     * 发送成功响应（带消息）
     */
    public static void sendSuccessResponse(HttpServletResponse response, String message, Object data) throws IOException {
        sendJSONResponse(response, APIResponse.success(message, data));
    }
    
    /**
     * 发送错误响应（简单版本）
     */
    public static void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        sendJSONResponse(response, APIResponse.error(message));
    }
    
    /**
     * 发送错误响应（带HTTP状态码）
     */
    public static void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        sendJSONResponse(response, APIResponse.error(message));
    }
    
    /**
     * 发送错误响应（带详细错误信息）
     */
    public static void sendErrorResponse(HttpServletResponse response, String message, String error) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        sendJSONResponse(response, APIResponse.error(message, error));
    }
    
    /**
     * 发送错误响应（带HTTP状态码和详细错误信息）
     */
    public static void sendErrorResponse(HttpServletResponse response, int statusCode, String message, String error) throws IOException {
        response.setStatus(statusCode);
        sendJSONResponse(response, APIResponse.error(message, error));
    }
    
    /**
     * 发送分页响应
     */
    public static <T> void sendPagedResponse(HttpServletResponse response, List<T> data, 
                                           int page, int size, long total) throws IOException {
        PagedResponse<T> pagedResponse = new PagedResponse<>(data, page, size, total);
        sendSuccessResponse(response, pagedResponse);
    }
    
    /**
     * 从请求中获取分页参数
     */
    public static Map<String, Integer> getPageParams(HttpServletRequest request) {
        Map<String, Integer> params = new HashMap<>();
        
        try {
            int page = Integer.parseInt(request.getParameter("page") != null ? 
                                      request.getParameter("page") : "1");
            int size = Integer.parseInt(request.getParameter("size") != null ? 
                                      request.getParameter("size") : "10");
            
            // 限制分页大小
            page = Math.max(1, page);
            size = Math.min(Math.max(1, size), 100);
            
            params.put("page", page);
            params.put("size", size);
            params.put("offset", (page - 1) * size);
            
        } catch (NumberFormatException e) {
            params.put("page", 1);
            params.put("size", 10);
            params.put("offset", 0);
        }
        
        return params;
    }
    
    /**
     * 异步执行任务
     */
    public static <T> CompletableFuture<T> executeAsync(Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, executorService);
    }
    
    /**
     * 批量操作结果
     */
    public static class BatchResult {
        private int total;
        private int success;
        private int failed;
        private List<String> errors;
        
        public BatchResult(int total, int success, int failed, List<String> errors) {
            this.total = total;
            this.success = success;
            this.failed = failed;
            this.errors = errors;
        }
        
        // Getters and setters
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
        public int getSuccess() { return success; }
        public void setSuccess(int success) { this.success = success; }
        public int getFailed() { return failed; }
        public void setFailed(int failed) { this.failed = failed; }
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
    }
    
    /**
     * 设置缓存头
     */
    public static void setCacheHeaders(HttpServletResponse response, int maxAgeSeconds) {
        response.setHeader("Cache-Control", "public, max-age=" + maxAgeSeconds);
        response.setDateHeader("Expires", System.currentTimeMillis() + (maxAgeSeconds * 1000L));
    }
    
    /**
     * 设置无缓存头
     */
    public static void setNoCacheHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
    
    /**
     * 检查请求是否支持GZIP
     */
    public static boolean supportsGzip(HttpServletRequest request) {
        String acceptEncoding = request.getHeader("Accept-Encoding");
        return acceptEncoding != null && acceptEncoding.contains("gzip");
    }
    
    /**
     * 获取客户端IP地址
     */
    public static String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty() && !"unknown".equalsIgnoreCase(xRealIP)) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * 关闭线程池
     */
    public static void shutdown() {
        executorService.shutdown();
    }
}