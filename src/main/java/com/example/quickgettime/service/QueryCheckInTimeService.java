package com.example.quickgettime.service;

import com.example.quickgettime.config.AppConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


/**
 * @author Eric.Lee
 * Date: 2025/6/16
 */
@Service("queryCheckInTimeService")
public class QueryCheckInTimeService {
    private final AppConfig appConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public QueryCheckInTimeService(AppConfig appConfig, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.appConfig = appConfig;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public Optional<String> fetchAttendanceInfo(String token) {
        String url = appConfig.getSystemDomain() + "/promiseExecRule";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", token);
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("rule_func_name", "getWebHR_API");
        body.add("data[param][prgID]", "HR02C00000");
        body.add("data[param][funcID]", "1020");
        body.add("data[param][begin_date]", today);
        body.add("data[param][end_date]", today);
        body.add("data[param][dept_code]", "1330");
        body.add("data[param][pers_code]", "a23034");
        body.add("data[param][punch_type]", "ALL");
        body.add("data[param][sort_type]", "ASC");
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            System.out.println("API 回應:" + response.getBody());
            JsonNode root = objectMapper.readTree(response.getBody());

            JsonNode punchItems = root.path("data").path("RETN-DATA").path("punchRecordItems");
            if (!punchItems.isArray() || punchItems.isEmpty()) {
                System.err.println("找不到 punchRecordItems");
                return Optional.empty();
            }

            JsonNode punchRecords = punchItems.get(0).path("punchRecords");
            if (!punchRecords.isArray()) {
                System.err.println("punchRecords 不是陣列");
                return Optional.empty();
            }

            StringBuilder sb = new StringBuilder();
            for (JsonNode record : punchRecords) {
                String date = record.path("attnd_date").asText();
                String time = record.path("attnd_time").asText();
                String type = record.path("attnd_type_name").asText();
                sb.append(String.format("%s %s - %s%n", date, time, type));
            }

            return Optional.of(sb.toString());

        } catch (HttpStatusCodeException e) {
            System.err.println("API 錯誤回應：" + e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            System.err.println("解析 JSON 時發生錯誤：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
