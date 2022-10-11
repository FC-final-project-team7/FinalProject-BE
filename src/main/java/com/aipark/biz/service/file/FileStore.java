package com.aipark.biz.service.file;

import com.aipark.config.SecurityUtil;
import com.aipark.exception.AwsErrorResult;
import com.aipark.exception.AwsException;
import com.aipark.web.dto.ProjectDto;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileStore {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.project}")
    private String audioFileLocation;

    // S3의 파일 저장 경로
    public String getFullPath() {
        return bucket + audioFileLocation + SecurityUtil.getCurrentMemberName();
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
        amazonS3.putObject(new PutObjectRequest(getFullPath(), storeFileName, inputStream, objectMetadata));

        return new ProjectDto.UploadFileDto(originalFilename, storeFileName);
    }

    // S3에서 파일 삭제(텍스트 프로젝트)
    public boolean deleteFileByText(String oldAudio) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, extractFileName(oldAudio)));
        } catch (RuntimeException e) {
            throw new AwsException(AwsErrorResult.AWS_ERROR);
        }
        return true;
    }

    // S3에서 파일 삭제(오디오 프로젝트)
    public boolean deleteFileByAudio(String oldAudio, String username) {
        try {
            log.info("트라이 안");
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, addFileName(oldAudio, username)));
        } catch (RuntimeException e) {
            throw new AwsException(AwsErrorResult.AWS_ERROR);
        }
        return true;
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

    // S3의 파일 URL주소에서 파일 주소만 뽑아오기
    private String extractFileName(String fileName) {
        return fileName.split("net/")[1];
    }

    // 파일이름을 추가해주는 메솓드
    private String addFileName(String fileName, String username) {
        return "project/" + username + "/" + fileName;
    }
}
