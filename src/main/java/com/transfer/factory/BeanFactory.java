package com.transfer.factory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务一：读取解析xml，通过反射技术实例化对象并且存储待用（map集合）
 * 任务二：对外提供获取实例对象的接口（根据id获取）
 */
public class BeanFactory {


    private static Map<String,Object> beanMap = new HashMap();

    // 任务一：读取解析xml，通过反射技术实例化对象存储在beanMap中
    static {

        // 将beans.xml读成流，存入内存中
        InputStream resourceAsStream = BeanFactory.class.getClassLoader().getResourceAsStream("beans.xml");
        // 使用dom4j解析流
        SAXReader saxReader = new SAXReader();
        try {
            // 读取到Document对象中
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            // 使用xpath解析所有bean
            List<Element> list = rootElement.selectNodes("//bean");
            for (Element element : list) {
                String id = element.attributeValue("id"); //accountDao
                String clazz = element.attributeValue("class"); //com.transfer.dao.impl.JdbcAccountDaoImpl
                //通过反射技术实例化对象
                Class<?> aClass = Class.forName(clazz);
                // 实例化之后的对象
                Object o = aClass.newInstance();

                //存储到map中
                beanMap.put(id, o);
            }

            // 解析所有property
            List<Element> propertyList = rootElement.selectNodes("//property");
            for (Element element : propertyList) {
                String name = element.attributeValue("name");
                String ref = element.attributeValue("ref");

                // 找到当前<property>标签的父元素<bean>
                Element parent = element.getParent();

                // 调用父元素对象的反射功能
                String parentId = parent.attributeValue("id");
                Object parentObject = beanMap.get(parentId);
                // 遍历父对象中所有的方法，找到"set"+name
                Method[] methods = parentObject.getClass().getMethods();
                for (Method method : methods) {
                    if (method.getName().equalsIgnoreCase("set" + name)) { //该方法就是 setAccountDao(AccountDao accountDao)
                        method.invoke(parentObject, beanMap.get(ref));
                    }
                }

                // 把处理后的parentObject重新放到map中
                beanMap.put(parentId, parentObject);

            }

        } catch (DocumentException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     *  任务二：对外提供后去实例对象的接口（根据id获取）
     * @param id
     * @return
     */
    public static Object getBean(String id){
        return beanMap.get(id);
    }
}
