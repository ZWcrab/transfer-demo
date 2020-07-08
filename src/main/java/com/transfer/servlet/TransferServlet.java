package com.transfer.servlet;

import com.alibaba.fastjson.JSON;
import com.annotation.Autowired;
import com.annotation.Service;
import com.transfer.factory.BeanFactory;
import com.transfer.factory.ProxyFactory;
import com.transfer.model.Result;
import com.transfer.service.TransferService;
import com.transfer.service.impl.TransferServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;



@WebServlet(name="transferServlet",urlPatterns = "/transferServlet")

public class TransferServlet extends HttpServlet {

    //    private TransferService transferService = (TransferService) BeanFactory.getBean("transferService");
    private ProxyFactory proxyFactory = (ProxyFactory) BeanFactory.getBean("proxyFactory");
    @Autowired
    private TransferService transferService;
//    private TransferService transferService = (TransferService) proxyFactory.jdkProxy(BeanFactory.getBean("transferService"));

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 设置请求体的字符编码
        req.setCharacterEncoding("UTF-8");

        String fromCardNo = req.getParameter("fromCardNo");
        String toCardNo = req.getParameter("toCardNo");
        String moneyStr = req.getParameter("money");
        int money = Integer.parseInt(moneyStr);

        Result result = new Result();

        try {

            // 2. 调用service层方法
//            TransferService transferService = new TransferServiceImpl();
            transferService.transfer(fromCardNo,toCardNo,money);
            result.setStatus("200");
        } catch (Exception e) {
            e.printStackTrace();
            result.setStatus("201");
            result.setMessage(e.toString());
        }

        // 响应
        resp.setContentType("application/json;charset=utf-8");
        resp.getWriter().print(JSON.toJSONString(result));
    }
}
