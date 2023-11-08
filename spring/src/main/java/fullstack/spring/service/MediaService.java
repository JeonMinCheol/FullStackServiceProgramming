package fullstack.spring.service;

import fullstack.spring.entity.ImageType;
import fullstack.spring.repository.ProfileRepo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Slf4j
@Service
public class MediaService {
    private final ProfileRepo profileRepo;
    private final String MAIN_DIR_NAME = System.getProperty("user.dir") + "\\" + "demo" + "\\" + "src" + "\\" + "main" + "\\" + "resources";
    private final String SUB_DIR_NAME = "\\" + "static";

    enum MediaType{
        IMAGE, RAW;
    }

    public MediaType findMediaType(String fileName) {

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

        if (extension.equals("png") || extension.equals("jpeg") || extension.equals("gif") || extension.equals("jpg") || extension.equals("PNG") || extension.equals("JPEG") || extension.equals("GIF") || extension.equals("JPG")) {
            return MediaType.IMAGE;
        } else {
            return MediaType.RAW;
        }
    }

    public String uploadImg(MultipartFile media, ImageType type) throws Exception {
        String path = type.name();
        try {
            File folder = new File(MAIN_DIR_NAME + SUB_DIR_NAME +  "/" + path);

            if (!folder.exists()) {
                folder.mkdirs();
            }

            String fileName = media.getOriginalFilename();
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            String generateFileName;
            MediaType mediaType = findMediaType(extension);
            if (mediaType.equals(MediaType.IMAGE)) {
                generateFileName = UUID.randomUUID().toString() + "." + extension;
            } else {
                throw new Exception();
            }
            String mediaURL = "\\" + path + "\\" + generateFileName;
            String destinationPath = MAIN_DIR_NAME +  SUB_DIR_NAME + mediaURL;
            log.info(destinationPath);
            File destination = new File(destinationPath);
            media.transferTo(destination);
            return mediaURL;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


    public String getPathURL(String fileUrl){
        List<String> url = Arrays.stream(fileUrl.split("/")).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        StringBuilder stringBuilder = new StringBuilder();
        for(String s : url){
            stringBuilder.append("/");
            stringBuilder.append(s);
        }
        return stringBuilder.toString();
    }

    public ResponseEntity<?> responseProfile(String imageUrl, ImageType type) throws IOException {
        InputStream imageStream = new FileInputStream(MAIN_DIR_NAME + SUB_DIR_NAME +  "/" + type.name() + "/" + imageUrl);
        byte[] imageByteArray = imageStream.readAllBytes();
        imageStream.close();

        return ResponseEntity.ok(imageByteArray);
    }
}
