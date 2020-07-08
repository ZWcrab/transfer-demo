package com.transfer.dao;


import com.annotation.Service;
import com.transfer.model.Account;


public interface AccountDao {

    Account queryAccountByCardNo(String cardNo) throws Exception;

    int updateAccountByCardNo(Account account) throws Exception;
}
