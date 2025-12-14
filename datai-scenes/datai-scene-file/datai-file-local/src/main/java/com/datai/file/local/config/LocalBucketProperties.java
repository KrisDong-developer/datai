package com.datai.file.local.config;

import lombok.Data;

@Data
public class LocalBucketProperties {
    private String path;
    private String permission;
    private String api;
}
