package com.datai.tfa.email.service;

import com.datai.auth.common.service.OauthVerificationCodeService;
import com.datai.auth.common.service.TfaService;

public interface IMailService extends OauthVerificationCodeService,TfaService {
}
