package com.bankapplication.controller;

import com.bankapplication.dto.*;
import com.bankapplication.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name= "User Account Management APIs")
public class UserController {

    @Autowired
    UserService userService;

    @Operation(
            summary="Create new User Account",
            description = "Creating a new user and assigning an Account ID"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Creating a new user and assigning an Account ID"
    )

    @PostMapping
    public bankResponse createAccount(@RequestBody userRequest userrequest) {
        return userService.createAccount(userrequest);
    }
    @Operation(
            summary="Balance Enquiry",
            description = "Given an Account Number, Check how much the user has"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )




    @GetMapping("balanceEnquiry")
    public bankResponse balanceEnquiry(@RequestBody EnquiryRequest request) {
        return userService.balanceEnquiry(request);
    }

    @GetMapping("nameEnquiry")
    public  String nameEnquiry(@RequestBody EnquiryRequest request) {
        return  userService.nameEnquiry(request);
    }

    @PostMapping("credit")
    public bankResponse creditAccount(@RequestBody CreditDebitRequest request) {
        return userService.creditAccount(request);
    }

    @PostMapping("debit")
    public bankResponse debitAccount(@RequestBody CreditDebitRequest request ) {
        return userService.debitAccount(request);
    }

    @PostMapping("transfer")
    public bankResponse transfer(@RequestBody TransferRequest request) {
        return userService.transfer(request);
    }
}
