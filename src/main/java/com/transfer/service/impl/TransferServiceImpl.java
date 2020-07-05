package com.transfer.service.impl;


import com.transfer.dao.AccountDao;
import com.transfer.dao.impl.JdbcAccountDaoImpl;
import com.transfer.factory.BeanFactory;
import com.transfer.model.Account;
import com.transfer.service.TransferService;
import com.transfer.utils.TransactionManager;

public class TransferServiceImpl implements TransferService {


    //    private AccountDao accountDao = (AccountDao) BeanFactory.getBean("accountDao");
    private AccountDao accountDao;

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

//    private TransactionManager transactionManager;
//
//    public void setTransactionManager(TransactionManager transactionManager) {
//        this.transactionManager = transactionManager;
//    }

    @Override
    public void transfer(String fromCardNo, String toCardNo, int money) throws Exception {
//
//        transactionManager.beginTransaction();
//
//        try {
//            AccountDao accountDao = new JdbcAccountDaoImpl();
            Account from = accountDao.queryAccountByCardNo(fromCardNo);
            Account to = accountDao.queryAccountByCardNo(toCardNo);

            from.setMoney(from.getMoney() - money);
            to.setMoney(to.getMoney() + money);

            accountDao.updateAccountByCardNo(to);
//            int a = 1/0;
            accountDao.updateAccountByCardNo(from);

//            transactionManager.commit();
//        } catch (Exception e) {
//
//            e.printStackTrace();
//            transactionManager.rollback();
//            throw new Exception("转账失败");
//        }

    }
}
