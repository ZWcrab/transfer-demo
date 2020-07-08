package com.listener;

import com.alibaba.fastjson.JSON;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public class BeanContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("======随服务启动==========");
        try {
            Enumeration<URL> systemResources = ClassLoader.getSystemResources("com.transfer.factory");
            System.out.println(JSON.toJSONString(systemResources));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
