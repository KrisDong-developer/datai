package com.datai.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserAgentUtils {

    private static final Logger logger = LoggerFactory.getLogger(UserAgentUtils.class);

    /**
     * 定义内部类，解决 'Cannot resolve symbol' 问题
     */
    public static class UserAgentInfo {
        private String osType;
        private String browserType;
        private String deviceType;
        private String fullBrowserInfo;

        public UserAgentInfo(String osType, String browserType, String deviceType, String fullBrowserInfo) {
            this.osType = osType;
            this.browserType = browserType;
            this.deviceType = deviceType;
            this.fullBrowserInfo = fullBrowserInfo;
        }

        // Getter 和 Setter
        public String getOsType() { return osType; }
        public String getBrowserType() { return browserType; }
        public String getDeviceType() { return deviceType; }
        public String getFullBrowserInfo() { return fullBrowserInfo; }
    }

    // 预编译正则，提升性能
    private static final Pattern OS_PATTERN = Pattern.compile(
            "(Windows NT 10\\.0|Windows NT 6\\.3|Windows NT 6\\.2|Windows NT 6\\.1|Mac OS X|Linux|Android|iPhone|iPad)",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 核心解析方法
     */
    public static UserAgentInfo parseUserAgent(String userAgent) {
        // 使用原生判断，避免依赖外部 StringUtils
        if (userAgent == null || userAgent.trim().isEmpty()) {
            return new UserAgentInfo("Unknown", "Unknown", "Desktop", "Empty UA");
        }

        try {
            String osType = parseOsType(userAgent);
            String deviceType = parseDeviceType(userAgent);
            String browserType = parseBrowserType(userAgent);
            String fullInfo = String.format("%s / %s / %s", osType, browserType, deviceType);

            return new UserAgentInfo(osType, browserType, deviceType, fullInfo);
        } catch (Exception e) {
            logger.warn("解析User-Agent失败: {}", userAgent);
            return new UserAgentInfo("Unknown", "Unknown", "Desktop", userAgent);
        }
    }

    private static String parseOsType(String ua) {
        Matcher matcher = OS_PATTERN.matcher(ua);
        if (matcher.find()) {
            String os = matcher.group(1).toLowerCase();
            if (os.contains("windows nt 10.0")) return "Windows 10/11";
            if (os.contains("mac os x")) return "macOS";
            if (os.contains("android")) return "Android";
            if (os.contains("iphone") || os.contains("ipad")) return "iOS";
            return os;
        }
        return "Unknown";
    }

    private static String parseBrowserType(String ua) {
        // 关键：按照优先级检查，防止 Chrome 误判为 Safari
        if (ua.contains("Edg/")) return extractVersion(ua, "Edg", "Edge");
        if (ua.contains("OPR/") || ua.contains("Opera/")) return extractVersion(ua, "OPR", "Opera");
        if (ua.contains("Firefox/")) return extractVersion(ua, "Firefox", "Firefox");
        if (ua.contains("Chrome/")) return extractVersion(ua, "Chrome", "Chrome");
        if (ua.contains("Safari/") && !ua.contains("Chrome/")) return extractVersion(ua, "Version", "Safari");
        if (ua.contains("MSIE") || ua.contains("Trident/")) return "Internet Explorer";

        return "Unknown";
    }

    private static String extractVersion(String ua, String key, String brandName) {
        Pattern pattern = Pattern.compile(key + "/(\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(ua);
        if (matcher.find()) {
            return brandName + " " + matcher.group(1);
        }
        return brandName;
    }

    private static String parseDeviceType(String ua) {
        String uaLower = ua.toLowerCase();
        if (uaLower.contains("ipad") || uaLower.contains("tablet")) return "Tablet";
        if (uaLower.contains("mobile") || uaLower.contains("android") || uaLower.contains("iphone")) return "Mobile";
        return "Desktop";
    }
}