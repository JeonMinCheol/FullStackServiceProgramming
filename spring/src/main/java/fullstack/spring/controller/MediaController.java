package fullstack.spring.controller;

import fullstack.spring.entity.ImageType;
import fullstack.spring.security.service.JwtService;
import fullstack.spring.service.MediaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/image")
public class MediaController {
    @Autowired
    private MediaService mediaService;
    @GetMapping(value = "/{imageType}/{imageUrl}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<?> getImage(@PathVariable String imageType, @PathVariable String imageUrl) throws IOException {
        if(imageType.equals(ImageType.profileImg.name()))
            return mediaService.responseProfile(imageUrl, ImageType.profileImg);

        return mediaService.responseProfile(imageUrl, ImageType.commentImg);
    }

}
