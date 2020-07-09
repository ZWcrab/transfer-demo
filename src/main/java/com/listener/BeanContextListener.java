package com.listener;

import com.alibaba.fastjson.JSON;
import com.annotation.Autowired;
import com.annotation.Service;
import com.transfer.dao.impl.JdbcAccountDaoImpl;
import com.transfer.utils.ScanPackageUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanContextListener implements ServletContextListener {

    private static Map<String, Object> beanMap = new HashMap();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("======随服务启动==========");


        // 处理@Service注解
        ScanPackageUtils scanPackageUtils = ScanPackageUtils.getInstance();
        scanPackageUtils.addClass("com.transfer");
        List<Class<?>> eleStrategyList = scanPackageUtils.getEleStrategyList();
        for (Class<?> aClass : eleStrategyList) {
            // 判断类上是否有@Service注解
            boolean annotationPresent = aClass.isAnnotationPresent(Service.class);
            if (annotationPresent) {
                String id = getAnnotationId(aClass);

                try {
                    beanMap.put(id, aClass.newInstance());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }

        // 处理@Autowired注解
        for (Class<?> aClass : eleStrategyList) {
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                boolean annotationPresent = declaredField.isAnnotationPresent(Autowired.class);
                if (annotationPresent) {
                    System.out.println(declaredField.getName());
                    try {
                        declaredField.setAccessible(true);
                        Object value = beanMap.get(declaredField.getName());
                        String id  = getAnnotationId(aClass);
                        Object key = beanMap.get(id);

                        declaredField.set(key,value);

                        beanMap.put(id, key);


//                        Proxy.newProxyInstance(aClass.getClassLoader(), aClass.getInterfaces(),
//                                new InvocationHandler() {
//                            @Override
//                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                                Object invoke = method.invoke(aClass.newInstance(), beanMap.get(declaredField.getName()));
//                                return invoke;
//                            }
//                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private String getAnnotationId(Class<?> aClass) {
        Service annotation = aClass.getAnnotation(Service.class);
        // 获取id
        String id;
        if (!"".equals(annotation.value())) {
            id = annotation.value();
        } else {
            String[] items = aClass.getName().split("\\.");
            id = toLowerCaseFirstOne(items[items.length - 1]);
        }
        return id;
    }


    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

    /**
     * 首字母转小写
     *
     * @param s
     * @return
     */
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }
}
