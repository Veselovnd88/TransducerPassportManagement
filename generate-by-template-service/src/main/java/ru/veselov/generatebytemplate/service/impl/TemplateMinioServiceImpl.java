package ru.veselov.generatebytemplate.service.impl;

import io.minio.GetObjectArgs;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ru.veselov.generatebytemplate.exception.CommonMinioException;
import ru.veselov.generatebytemplate.model.Template;
import ru.veselov.generatebytemplate.service.MinioHelper;
import ru.veselov.generatebytemplate.service.TemplateMinioService;

import java.io.IOException;

/**
 * Operations with templates with MinIO storage
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateMinioServiceImpl implements TemplateMinioService {

    @Value("${minio.buckets.template}")
    private String templateBucket;

    private final MinioHelper minioHelper;

    @Override
    public ByteArrayResource getTemplateByName(String filename) {
        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(templateBucket).object(filename).build();
        return minioHelper.getByteArrayResource(objectArgs);
    }

    @Override
    public void saveTemplate(Resource resource, Template template) {
        try {
            PutObjectArgs saveArgs = createSaveArgs(resource, template);
            minioHelper.putObject(saveArgs);
            log.info("Template saved to MinIO storage: [bucket: {}, filename: {}]",
                    saveArgs.bucket(), saveArgs.object());
        } catch (IOException e) {
            log.error("Something went wrong with reading object for saving: {}", e.getMessage());
            throw new CommonMinioException(e.getMessage(), e);
        }
    }

    @Override
    public void updateTemplate(Resource resource, Template template) {
        try {
            PutObjectArgs saveArgs = createSaveArgs(resource, template);
            minioHelper.putObject(saveArgs);
            log.info("Template updated in MinIO storage: [bucket: {}, filename: {}]",
                    saveArgs.bucket(), saveArgs.object());
        } catch (IOException e) {
            log.error("Something went wrong with reading object for updating: {}", e.getMessage());
            throw new CommonMinioException(e.getMessage(), e);
        }
    }

    @Override
    public void deleteTemplate(String filename) {
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().object(filename).bucket(templateBucket).build();
        minioHelper.deleteObject(removeObjectArgs);
    }

    private PutObjectArgs createSaveArgs(Resource resource, Template template) throws IOException {
        return PutObjectArgs.builder().bucket(template.getBucket())
                .object(template.getFilename())
                .stream(resource.getInputStream(), resource.contentLength(), 0)
                .build();
    }

}
