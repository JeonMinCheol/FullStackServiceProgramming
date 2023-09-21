package fullstack.spring.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.util.HashMap;

@Slf4j
@Service
public class TranslateService {
    @Value("${papago.url.detect}")
    private String detectUrl;

    @Value("${papago.url.translate}")
    private String translateUrl;

    @Value("${papago.client.id}")
    private String clientId;

    @Value("${papago.client.secret}")
    private String clientSecret;

    public String detect(String text) throws JsonProcessingException {
        //Spring restTemplate
        HashMap<String, Object> result = new HashMap<String, Object>();
        String jsonInString = "";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders header = new HttpHeaders();
        header.add("X-Naver-Client-Id", clientId);
        header.add("X-Naver-Client-Secret", clientSecret);
        header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        HttpEntity<?> entity = new HttpEntity<>("query=" + URLEncoder.encode(text), header);

        UriComponents uri = UriComponentsBuilder.fromHttpUrl(detectUrl).build();

        ResponseEntity<?> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.POST, entity, Object.class);
        result.put("statusCode", resultMap.getStatusCodeValue()); //http status code를 확인
        result.put("header", resultMap.getHeaders()); //헤더 정보 확인
        result.put("body", resultMap.getBody()); //실제 데이터 정보 확인

        //데이터를 제대로 전달 받았는지 확인 string형태로 파싱해줌
        ObjectMapper mapper = new ObjectMapper();
        jsonInString = mapper.writeValueAsString(resultMap.getBody());

        return jsonInString;
    }

    public String translate(String text, String source) throws JsonProcessingException {
        //Spring restTemplate
        HashMap<String, Object> result = new HashMap<String, Object>();
        String jsonInString = "";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders header = new HttpHeaders();
        header.add("X-Naver-Client-Id", clientId);
        header.add("X-Naver-Client-Secret", clientSecret);
        header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        String data = "source=" + source + "&target=ko&text=" + text;
        log.info(data);

        HttpEntity<?> entity = new HttpEntity<>(data, header);

        UriComponents uri = UriComponentsBuilder.fromHttpUrl(translateUrl).build();

        ResponseEntity<?> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.POST, entity, Object.class);
        result.put("statusCode", resultMap.getStatusCodeValue()); //http status code를 확인
        result.put("header", resultMap.getHeaders()); //헤더 정보 확인
        result.put("body", resultMap.getBody()); //실제 데이터 정보 확인

        //데이터를 제대로 전달 받았는지 확인 string형태로 파싱해줌
        ObjectMapper mapper = new ObjectMapper();
        jsonInString = mapper.writeValueAsString(resultMap.getBody());
        log.info("result : " + jsonInString);
        return jsonInString;
    }

}
