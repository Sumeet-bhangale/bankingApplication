package com.bankapplication.service.impl;

import com.bankapplication.dto.*;
import org.springframework.stereotype.Service;


public interface UserService {

    bankResponse createAccount(userRequest userrequest);

    bankResponse balanceEnquiry(EnquiryRequest request);

    String nameEnquiry(EnquiryRequest request);

    bankResponse creditAccount(CreditDebitRequest request);

    bankResponse debitAccount(CreditDebitRequest request);

    bankResponse transfer(TransferRequest request);


}
