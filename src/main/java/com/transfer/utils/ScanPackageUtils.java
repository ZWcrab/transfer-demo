package com.transfer.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ScanPackageUtils {
    private Class<?> superStrategy = String.class;//接口类class 用于过滤 可以不要

    private List<Class<? extends String>> eleStrategyList = new ArrayList<Class<? extends String>>();

    private ClassLoader classLoader = ScanPackageUtils.class.getClassLoader();//默认使用的类加载器

    private static final String STARATEGY_PATH = "com.MyUtils.file";//需要扫描的策略包名

    public static void main(String[] args) {
        ScanPackageUtils s = new ScanPackageUtils();
        s.addClass("com.transfer");
    }

    /**
     * 获取包下所有实现了superStrategy的类并加入list
     */
    private void addClass(String packageName){
        URL url = classLoader.getResource(packageName.replace(".", "/"));
        String protocol = url.getProtocol();
        if ("file".equals(protocol)) {
            // 本地自己可见的代码
            findClassLocal(packageName);
        } else if ("jar".equals(protocol)) {
            // 引用jar包的代码
            findClassJar(packageName);
        }
    }

    /**
     * 本地查找
     * @param packName
     */
    private void findClassLocal(final String packName){
        URI url = null ;
        try {
            url = classLoader.getResource(packName.replace(".", "/")).toURI();
        } catch (URISyntaxException e1) {
            throw new RuntimeException("未找到策略资源");
        }

        File file = new File(url);
        file.listFiles(new FileFilter() {

            public boolean accept(File chiFile) {
                if(chiFile.isDirectory()){
                    findClassLocal(packName+"."+chiFile.getName());
                }
                if(chiFile.getName().endsWith(".class")){
                    Class<?> clazz = null;
                    try {
                        String url = packName + "." + chiFile.getName().replace(".class", "");
                        clazz = classLoader.loadClass(url);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    System.out.println(clazz);
                    if(superStrategy.isAssignableFrom(clazz)){
                        eleStrategyList.add((Class<? extends String>) clazz);
                    }
                    return true;
                }
                return false;
            }
        });

    }

    /**
     * jar包查找
     * @param packName
     */
    private void findClassJar(final String packName){
        String pathName = packName.replace(".", "/");
        JarFile jarFile  = null;
        try {
            URL url = classLoader.getResource(pathName);
            JarURLConnection jarURLConnection  = (JarURLConnection )url.openConnection();
            jarFile = jarURLConnection.getJarFile();
        } catch (IOException e) {
            throw new RuntimeException("未找到策略资源");
        }

        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarEntryName = jarEntry.getName();

            if(jarEntryName.contains(pathName) && !jarEntryName.equals(pathName+"/")){
                //递归遍历子目录
                if(jarEntry.isDirectory()){
                    String clazzName = jarEntry.getName().replace("/", ".");
                    int endIndex = clazzName.lastIndexOf(".");
                    String prefix = null;
                    if (endIndex > 0) {
                        prefix = clazzName.substring(0, endIndex);
                    }
                    findClassJar(prefix);
                }
                if(jarEntry.getName().endsWith(".class")){
                    Class<?> clazz = null;
                    try {
                        clazz = classLoader.loadClass(jarEntry.getName().replace("/", ".").replace(".class", ""));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if(superStrategy.isAssignableFrom(clazz)){
                        eleStrategyList.add((Class<? extends String>) clazz);
                    }
                }
            }

        }

    }
}
