package SecurityAPI2.Service.Storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IStorageService {
    String uploadFile(MultipartFile file) throws IOException;
}
