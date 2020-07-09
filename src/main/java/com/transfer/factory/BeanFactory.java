package com.transfer.factory;

import com.annotation.Autowired;
import com.annotation.Service;
import com.annotation.Transactional;
import com.transfer.utils.ScanPackageUtils;
import com.transfer.utils.TransactionManager;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;


public class BeanFactory {


    private static Map<String, Object> beanMap = new HashMap();

    static {

        // 扫描指定路径下的所有class
        ScanPackageUtils scanPackageUtils = ScanPackageUtils.getInstance();
        scanPackageUtils.addClass("com.transfer");
        List<Class<?>> eleStrategyList = scanPackageUtils.getEleStrategyList();

        // 处理@Service注解
        doServiceAnnotation(eleStrategyList);

        // 处理@Autowired注解
        doAutowiredAnnotation(eleStrategyList);

        // 处理@Transactional注解
        doTranscationalAnnotation(eleStrategyList);

    }

    private static void doServiceAnnotation(List<Class<?>> eleStrategyList) {
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
    }

    private static void doAutowiredAnnotation(List<Class<?>> eleStrategyList) {
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

                    } catch (IllegalAccessException  e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void doTranscationalAnnotation(List<Class<?>> eleStrategyList) {
        for (Class<?> aClass : eleStrategyList) {
            Method[] methods = aClass.getMethods();
            for (Method method : methods) {
                Transactional annotation = method.getAnnotation(Transactional.class);
                if (annotation != null) {
                    TransactionManager transactionManager = (TransactionManager) beanMap.get("transactionManager");
                    String annotationId = getAnnotationId(aClass);
                    Object obj = beanMap.get(annotationId);
                    Object proxyObject = Proxy.newProxyInstance(aClass.getClassLoader(), aClass.getInterfaces(),
                            new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            Object result = null;
                            transactionManager.beginTransaction();
                            try {
                                result = method.invoke(obj, args);
                                transactionManager.commit();
                            } catch (Exception e) {

                                e.printStackTrace();
                                transactionManager.rollback();
                                throw e;
                            }
                            return result;
                        }
                    });
                    beanMap.put(annotationId, proxyObject);
                }
            }
        }
    }


    private static String getAnnotationId(Class<?> aClass) {
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

    /**
     * 任务二：对外提供后去实例对象的接口（根据id获取）
     *
     * @param id
     * @return
     */
    public static Object getBean(String id) {
        return beanMap.get(id);
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
