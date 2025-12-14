package com.datai.file.minio.domain;

import com.datai.common.core.text.Convert;
import com.datai.file.storage.StorageEntity;

import io.minio.GetObjectResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import okhttp3.Headers;

@Data
@EqualsAndHashCode(callSuper = true)
public class MinioEntityVO extends StorageEntity {
    private String object;
    private Headers headers;
    private String bucket;
    private String region;

    public MinioEntityVO(GetObjectResponse response, String filePath) {
        this.setFilePath(filePath);
        this.setInputStream(response);
        this.setByteCount(Convert.toLong(response.headers().get("Content-Length"), null));
        this.setObject(response.object());
        this.setRegion(response.region());
        this.setBucket(response.bucket());
        this.setHeaders(response.headers());
    }

}
