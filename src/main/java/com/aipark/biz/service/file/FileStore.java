package com.aipark.biz.service.file;

import com.aipark.web.dto.ProjectDto;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FileStore {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // S3의 파일 저장 경로
    public String getFullPath(String fileName) {
        return bucket + fileName;
    }

    // S3 에 저장
    public ProjectDto.UploadFileDto storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }
        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);
        InputStream inputStream = multipartFile.getInputStream();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        amazonS3.putObject(new PutObjectRequest(bucket, storeFileName, inputStream, objectMetadata));

        return new ProjectDto.UploadFileDto(originalFilename, storeFileName);
    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();

        return uuid + "." + ext;
    }

    // 확장자명 가져오기
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");

        return originalFilename.substring(pos + 1);
    }
}
