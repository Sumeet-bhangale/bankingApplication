package com.bankapplication.service.impl;

import com.bankapplication.dto.*;
import com.bankapplication.entity.User;
import com.bankapplication.repository.UserRepo;
import com.bankapplication.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    EmailService emailService;

    @Autowired
    TransactionService transactionService;

    @Override
    public bankResponse createAccount(userRequest userrequest) {

        if (userRepo.existsByEmail(userrequest.getEmail())) {

            return bankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();


        }

        User newUser = User.builder()
                .firstName(userrequest.getFirstName())
                .lastName(userrequest.getLastName())
                .otherName(userrequest.getOtherName())
                .gender(userrequest.getGender())
                .address(userrequest.getAddress())
                .stateOfOrigin(userrequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userrequest.getEmail())
                .alternatePhoneNumber(userrequest.getAlternatePhoneNumber())
                .status("ACTIVE")
                .build();

        User savedUser = userRepo.save(newUser);

        // Send Email Alert
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("Account Creation")
                .messageBody("Congratulations!! Your Account has Been Created. \n Your Account Details: \n" +
                        "Account Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName() + "\nAccount Number: " + savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);

        return bankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName())
                        .build())
                .build();

    }

    @Override
    public bankResponse balanceEnquiry(EnquiryRequest request) {

        // Check if the Provided Account Number Exists In DB
        boolean isAccountExists = userRepo.existsByAccountNumber(request.getAccountNumber());

        if (!isAccountExists) {
            return bankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User foundUser = userRepo.findByAccountNumber(request.getAccountNumber());
        return bankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExists = userRepo.existsByAccountNumber(request.getAccountNumber());

        if (!isAccountExists) {
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User foundUser = userRepo.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();

    }

    @Override
    public bankResponse creditAccount(CreditDebitRequest request) {
        boolean isAccountExists = userRepo.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExists) {
            return bankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToCredit = userRepo.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepo.save(userToCredit);

        //Save transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();

        transactionService.saveTransaction(transactionDto);

        return bankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getOtherName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .build())
                .build();
    }




    @Override
    public bankResponse debitAccount(CreditDebitRequest request) {
        // Check if the Account Exists
        // Check if the Amount you intend to Withdraw is not more than the Current Amount balance

        boolean isAccountExists = userRepo.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExists) {
            return bankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToDebit = userRepo.findByAccountNumber(request.getAccountNumber());
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();
                if(availableBalance.intValue() < debitAmount.intValue()) {
                    return bankResponse.builder()
                            .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                            .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                            .accountInfo(null)
                            .build();
                }



                else {
                    userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
                    userRepo.save(userToDebit);

                    TransactionDto transactionDto = TransactionDto.builder()
                            .accountNumber(userToDebit.getAccountNumber())
                            .transactionType("CREDIT")
                            .amount(request.getAmount())
                            .build();

                    transactionService.saveTransaction(transactionDto);


                    return bankResponse.builder()
                            .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                            .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                            .accountInfo(AccountInfo.builder()
                                    .accountNumber(request.getAccountNumber())
                                    .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
                                    .accountBalance(userToDebit.getAccountBalance())
                                    .build())
                            .build();
                }
}

    @Override
    public bankResponse transfer(TransferRequest request) {
        // get the Account to Debit (Check if it Exists)
        // Check if the amount i'm Debiting is not more than the Current Balance
        // Debit the Amount
        // get the Account to Credit
        //credit the Account


        boolean isDestinationAccountExist = userRepo.existsByAccountNumber(request.getDestinationAccountNumber());
        if(!isDestinationAccountExist) {
            return bankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User sourceAccountUser = userRepo.findByAccountNumber(request.getSourceAccountNumber());
        if(request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0) {
                return bankResponse.builder()
                        .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                        .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                        .accountInfo(null)
                        .build();
        }

        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        String sourceUserName = sourceAccountUser.getFirstName() + " " +sourceAccountUser.getLastName() +" " + sourceAccountUser.getOtherName();
        userRepo.save(sourceAccountUser);

        EmailDetails debitAlert = EmailDetails.builder()
                .subject("DEBIT Alert")
                .recipient(sourceAccountUser.getEmail())
                .messageBody("The Sum of "+ request.getAmount() + " has been Deducted from your Account !! Your Current Balance is "+ sourceAccountUser.getAccountBalance())
                .build();

        emailService.sendEmailAlert(debitAlert);



        User destinationAccountUser = userRepo.findByAccountNumber(request.getDestinationAccountNumber());
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
       // String recipientUsername = destinationAccountUser.getFirstName() + " " + destinationAccountUser.getLastName() + " " + destinationAccountUser.getOtherName();
        userRepo.save(destinationAccountUser);
// Credit

        EmailDetails creditAlert = EmailDetails.builder()
                .subject("CREDIT ALERT")
                .recipient(sourceAccountUser.getEmail())
                .messageBody("The Sum of "+ request.getAmount() + " has been Sent to your Account from "+  sourceUserName + "Your Current Balance is " + sourceAccountUser.getAccountBalance())
                .build();

        emailService.sendEmailAlert(creditAlert);


        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(destinationAccountUser.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();

        transactionService.saveTransaction(transactionDto);

        return bankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(null)
                .build();

    }


}