package com.datai.common.service.datasource;

import java.util.Properties;

import javax.sql.CommonDataSource;

public interface CreateDataSource {
    CommonDataSource createDataSource(String name, Properties prop);
}
