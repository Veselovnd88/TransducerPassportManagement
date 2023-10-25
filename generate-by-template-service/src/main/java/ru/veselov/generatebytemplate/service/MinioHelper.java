package ru.veselov.generatebytemplate.service;

import io.minio.GetObjectArgs;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.core.io.ByteArrayResource;

public interface MinioHelper {

    ByteArrayResource getByteArrayResource(GetObjectArgs getObjectArgs);

    void putObject(PutObjectArgs putObjectArgs);

    void deleteObject(RemoveObjectArgs removeObjectArgs);

}
