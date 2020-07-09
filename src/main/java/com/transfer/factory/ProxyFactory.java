package com.transfer.factory;

import com.annotation.Autowired;
import com.annotation.Service;
import com.transfer.utils.TransactionManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Service
public class ProxyFactory {

    @Autowired
    private TransactionManager transactionManager;

//    public void setTransactionManager(TransactionManager transactionManager) {
//        this.transactionManager = transactionManager;
//    }

    /**
     * JDK动态代理
     * @param object
     * @return
     */
    public Object jdkProxy(Object object) {
        return Proxy.newProxyInstance(object.getClass().getClassLoader(), object.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object result = null;
                transactionManager.beginTransaction();
                try {
                    result = method.invoke(object, args);
                    transactionManager.commit();
                } catch (Exception e) {

                    e.printStackTrace();
                    transactionManager.rollback();
                    throw e;
                }
                return result;
            }
        });

    }
}
