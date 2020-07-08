package com.transfer.dao.impl;


import com.alibaba.fastjson.JSON;
import com.annotation.Service;
import com.transfer.dao.AccountDao;
import com.transfer.model.Account;
import com.transfer.utils.ConnectionUtils;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


@Service(value = "accountDao")
public class JdbcAccountDaoImpl implements AccountDao {

    private ConnectionUtils connectionUtils;

    public void setConnectionUtils(ConnectionUtils connectionUtils) {
        this.connectionUtils = connectionUtils;
    }

    @Override
    public Account queryAccountByCardNo(String cardNo) throws Exception {
        //从连接池获取连接
//         Connection con = DruidUtils.getInstance().getConnection();
//        Connection con = ConnectionUtils.getInstance().getCurrentConnection();
        Connection con = connectionUtils.getCurrentConnection();
        String sql = "select * from account where cardNo=?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1,cardNo);
        ResultSet resultSet = preparedStatement.executeQuery();

        Account account = new Account();
        while(resultSet.next()) {
            account.setCardNo(resultSet.getString("cardNo"));
            account.setName(resultSet.getString("name"));
            account.setMoney(resultSet.getInt("money"));
        }

        resultSet.close();
        preparedStatement.close();
//        con.close();

        return account;
    }

    public static void main(String[] args) {
            URL systemResource = ClassLoader.getSystemResource("com.listener.BeanContextFactory");
            System.out.println(JSON.toJSONString(systemResource));
    }

    @Override
    public int updateAccountByCardNo(Account account) throws Exception {

        // 从连接池获取连接
        // 改造为：从当前线程当中获取绑定的connection连接
//        Connection con = DruidUtils.getInstance().getConnection();
//        Connection con = ConnectionUtils.getInstance().getCurrentConnection();
        Connection con = connectionUtils.getCurrentConnection();
        String sql = "update account set money=? where cardNo=?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1,account.getMoney());
        preparedStatement.setString(2,account.getCardNo());
        int i = preparedStatement.executeUpdate();

        preparedStatement.close();
//        con.close();
        return i;
    }
}
