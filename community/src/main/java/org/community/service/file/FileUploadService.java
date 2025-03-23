package org.community.service.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class FileUploadService {
    public final String PROFILE_IMAGE_PATH = System.getProperty("user.dir")+"/community/upload/profiles/";
    public final String PROFILE_IMAGE_WEB_PATH = "/upload/profiles/";
    public final String POST_IMAGE_PATH = System.getProperty("user.dir")+"/community/upload/post/";
    public final String POST_IMAGE_WEB_PATH = "/upload/post/";

    public String saveImage(MultipartFile file, boolean isProfile) {
        log.info(file.getOriginalFilename());
        try {
            String originalFilename = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String savedName = (uuid + "_" + originalFilename).trim();

            File dest = new File(isProfile ? PROFILE_IMAGE_PATH : POST_IMAGE_PATH, savedName);
            file.transferTo(dest);

            return (isProfile ? PROFILE_IMAGE_WEB_PATH : POST_IMAGE_WEB_PATH) + savedName; // DB에는 이 경로만 저장
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }
}
