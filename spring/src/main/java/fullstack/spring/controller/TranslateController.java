package fullstack.spring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fullstack.spring.dto.TranslateDataDTO;
import fullstack.spring.service.TranslateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
public class TranslateController {
    @Autowired
    private TranslateService translateService;

    @PostMapping("/translate")
    ResponseEntity<?> translateText(@RequestBody TranslateDataDTO request) throws JsonProcessingException {
        try{
            String source =  translateService.detect(request.getText()).replaceAll("\"","").replace("{langCode:","").replace("}","");
            HttpHeaders resHeaders = new HttpHeaders();
            resHeaders.add("Content-Type", "application/json;charset=UTF-8");

            return new ResponseEntity<>(translateService.translate(request.getText(), source),resHeaders, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(403));
        }
    }


}
