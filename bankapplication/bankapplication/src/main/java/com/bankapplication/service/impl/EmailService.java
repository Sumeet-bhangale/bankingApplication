package com.bankapplication.service.impl;

import com.bankapplication.dto.EmailDetails;

public interface EmailService {

    void sendEmailAlert(EmailDetails emailDetails);
}
