package fullstack.spring.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.lang.model.type.ErrorType;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Objects;

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

    private JSONParser parser = new JSONParser();

    private static void createResult(HashMap<String, Object> result, ResponseEntity<?> resultMap) {
        result.put("statusCode", resultMap.getStatusCodeValue()); //http status code를 확인
        result.put("header", resultMap.getHeaders()); //헤더 정보 확인
        result.put("body", resultMap.getBody()); //실제 데이터 정보 확인
    }

    private void addHeader(HttpHeaders header) {
        header.add("X-Naver-Client-Id", clientId);
        header.add("X-Naver-Client-Secret", clientSecret);
        header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE+";charset=utf-8");
    }

    public String detect(String text) throws JsonProcessingException, ParseException {
        HashMap<String, Object> result = new HashMap<String, Object>();
        String jsonInString = "";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders header = new HttpHeaders();
        addHeader(header);

        HttpEntity<?> entity = new HttpEntity<>("query=" + URLEncoder.encode(text), header);

        UriComponents uri = UriComponentsBuilder.fromHttpUrl(detectUrl).build();

        ResponseEntity<?> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.POST, entity, Object.class);
        createResult(result, resultMap);

        //데이터를 제대로 전달 받았는지 확인 string형태로 파싱해줌
        ObjectMapper mapper = new ObjectMapper();
        jsonInString = mapper.writeValueAsString(resultMap.getBody());

        JSONObject object = (JSONObject) parser.parse(jsonInString);
        String langCode = (String) object.get("langCode");

        return langCode;
    }

    public String translate(String text, String source, String target) throws JsonProcessingException, ParseException {
        // if(Objects.equals(source, "ko") || Objects.equals(source, "vi")) return "";

        HashMap<String, Object> result = new HashMap<String, Object>();
        String jsonInString = "";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders header = new HttpHeaders();
        addHeader(header);

        if(source == "ko") source = "ko";
        String data = "source=" + source + "&target=" + target + "&text=" + text;

        log.info(data);

        HttpEntity<?> entity = new HttpEntity<>(data, header);

        UriComponents uri = UriComponentsBuilder.fromHttpUrl(translateUrl).build();

        ResponseEntity<?> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.POST, entity, Object.class);
        createResult(result, resultMap);

        //데이터를 제대로 전달 받았는지 확인 string형태로 파싱해줌
        ObjectMapper mapper = new ObjectMapper();
        jsonInString = mapper.writeValueAsString(resultMap.getBody());

        log.info(jsonInString);

        JSONObject object = (JSONObject) parser.parse(jsonInString);
        JSONObject ret1 = (JSONObject) object.get("message");
        JSONObject ret2 = (JSONObject) ret1.get("result");
        String translatedText = (String) ret2.get("translatedText");
        log.info(translatedText);

        return translatedText;
    }

}
