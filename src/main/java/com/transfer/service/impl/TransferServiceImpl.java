package com.transfer.service.impl;


import com.transfer.dao.AccountDao;
import com.transfer.dao.impl.JdbcAccountDaoImpl;
import com.transfer.model.Account;
import com.transfer.service.TransferService;

public class TransferServiceImpl implements TransferService {

    @Override
    public void transfer(String fromCardNo, String toCardNo, int money) throws Exception {

            AccountDao accountDao = new JdbcAccountDaoImpl();
            Account from = accountDao.queryAccountByCardNo(fromCardNo);
            Account to = accountDao.queryAccountByCardNo(toCardNo);

            from.setMoney(from.getMoney()-money);
            to.setMoney(to.getMoney()+money);

            accountDao.updateAccountByCardNo(to);
            accountDao.updateAccountByCardNo(from);





    }
}
