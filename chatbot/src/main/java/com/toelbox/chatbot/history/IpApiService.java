package com.toelbox.chatbot.history;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class IpApiService {
    private final RestClient restClient;
    private static final String BASE_URL = "http://ip-api.com/json/";

    public IpApiService() {
        this.restClient = RestClient.create();
    }

    public IpApiResponse getIpInfo(String ipAddress) {
        return restClient.get()
                .uri(BASE_URL + ipAddress)
                .retrieve()
                .body(IpApiResponse.class);
    }
}