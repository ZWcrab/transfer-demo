package com.transfer.utils;

import java.sql.SQLException;

public class TransactionManager {

    private ConnectionUtils connectionUtils;

    public void setConnectionUtils(ConnectionUtils connectionUtils) {
        this.connectionUtils = connectionUtils;
    }

//    private static TransactionManager transactionManager = new TransactionManager();
//
//    public TransactionManager() {
//    }
//
//    public static TransactionManager getInstance() {
//        return transactionManager;
//    }

    // 手动开启事务（实际就是设置自动提交为false）
    public void beginTransaction() throws SQLException {
        connectionUtils.getCurrentConnection().setAutoCommit(false);
    }

    // 提交
    public void commit() throws SQLException {
        connectionUtils.getCurrentConnection().commit();
    }

    // 回滚
    public void rollback() throws SQLException {
        connectionUtils.getCurrentConnection().rollback();
    }


}
