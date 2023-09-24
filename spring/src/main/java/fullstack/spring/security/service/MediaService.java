package fullstack.spring.security.service;

import fullstack.spring.repository.ProfileRepo;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Service
public class MediaService {
    private final ProfileRepo profileRepo;
    private final String MAIN_DIR_NAME = System.getProperty("user.dir") + File.separator + "demo" + File.separator + "src" + File.separator + "main" + File.separator + "resources";
    private final String SUB_DIR_NAME = File.separator + "static";

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

    public String uploadProfileImg(MultipartFile media) throws Exception {
        try {
            File folder = new File(MAIN_DIR_NAME + SUB_DIR_NAME +  File.separator + "profileImg");

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
            String mediaURL = File.separator + "profileImg" + File.separator + generateFileName;
            String destinationPath = MAIN_DIR_NAME +  SUB_DIR_NAME + mediaURL;
            File destination = new File(destinationPath);
            media.transferTo(destination);

            return mediaURL;
        } catch (Exception e) {
            throw new Exception("사진 형식이 아님");
        }
    }

    public String getPathURL(String fileUrl){
        List<String> url = Arrays.stream(fileUrl.split("/")).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        StringBuilder stringBuilder = new StringBuilder();
        for(String s : url){
            stringBuilder.append(File.separator);
            stringBuilder.append(s);
        }
        return stringBuilder.toString();
    }
}
