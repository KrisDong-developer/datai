package com.datai.common.exception.file;

import com.datai.common.exception.base.BaseException;

/**
 * 文件异常类
 * 
 * @author datai
 */
public class FileException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args)
    {
        super("file", code, args, null);
    }

}
