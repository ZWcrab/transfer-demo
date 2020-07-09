package com.transfer.service.impl;


import com.annotation.Autowired;
import com.annotation.Service;
import com.annotation.Transactional;
import com.transfer.dao.AccountDao;
import com.transfer.dao.impl.JdbcAccountDaoImpl;
import com.transfer.factory.BeanFactory;
import com.transfer.model.Account;
import com.transfer.service.TransferService;
import com.transfer.servlet.TransferServlet;

import java.lang.reflect.Field;

@Service(value = "transferService")
public class TransferServiceImpl implements TransferService {

    @Autowired
    private AccountDao accountDao;

    @Override
    @Transactional
    public void transfer(String fromCardNo, String toCardNo, int money) throws Exception {
            Account from = accountDao.queryAccountByCardNo(fromCardNo);
            Account to = accountDao.queryAccountByCardNo(toCardNo);

            from.setMoney(from.getMoney() - money);
            to.setMoney(to.getMoney() + money);

            accountDao.updateAccountByCardNo(to);
//            int a = 1/0;
            accountDao.updateAccountByCardNo(from);


    }
}
