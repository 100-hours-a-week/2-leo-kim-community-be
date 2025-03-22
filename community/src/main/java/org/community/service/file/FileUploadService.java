package org.community.service.file;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileUploadService {
    public final String PROFILE_IMAGE_PATH = System.getProperty("user.dir")+"/community/upload/profiles/";
    public final String PROFILE_IMAGE_WEB_PATH = "/upload/profiles/"; // 🔥 이걸 반환


    public String saveProfileImage(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String savedName = uuid + "_" + originalFilename;

            File dest = new File(PROFILE_IMAGE_PATH, savedName);
            file.transferTo(dest);

            return PROFILE_IMAGE_WEB_PATH + savedName; // DB에는 이 경로만 저장
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }
}
