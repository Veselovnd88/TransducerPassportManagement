package ru.veselov.generatebytemplate.service.impl;

import io.minio.GetObjectArgs;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ru.veselov.generatebytemplate.exception.CommonMinioException;
import ru.veselov.generatebytemplate.model.GeneratedResultFile;
import ru.veselov.generatebytemplate.service.MinioHelper;
import ru.veselov.generatebytemplate.service.ResultFileMinioService;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResultFileMinioServiceImpl implements ResultFileMinioService {

    @Value("${minio.bucket.results}")
    private String resultBucket;

    private final MinioHelper minioHelper;

    @Override
    public String saveResult(Resource resource, GeneratedResultFile resultFile) {
        try {
            PutObjectArgs saveArgs = PutObjectArgs.builder().bucket(resultBucket)
                    .object(resultFile.getFilename())
                    .stream(resource.getInputStream(), resource.contentLength(), 0)
                    .build();
            minioHelper.putObject(saveArgs);
            log.info("Template saved to MinIO storage: [bucket: {}, filename: {}]",
                    saveArgs.bucket(), saveArgs.object());
            return saveArgs.object();
        } catch (IOException e) {
            log.error("Something went wrong with reading object for saving: {}", e.getMessage());
            throw new CommonMinioException(e.getMessage(), e);
        }
    }

    @Override
    public ByteArrayResource getResultFile(GeneratedResultFile resultFile) {
        String filename = resultFile.getFilename();
        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(resultBucket).object(filename).build();
        return minioHelper.getByteArrayResource(objectArgs);
    }

}
