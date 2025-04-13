package com.toelbox.chatbot.core;

import jakarta.servlet.http.HttpServletRequest;


public class IpAddress {
	public static String getClientIp(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-Forwarded-For");

		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
		}

		// If X-Forwarded-For contains multiple IPs (e.g., "client-ip, proxy1, proxy2"), take the first one
		if (ipAddress != null && ipAddress.contains(",")) {
			ipAddress = ipAddress.split(",")[0].trim();
		}

		return ipAddress;
	}
}
