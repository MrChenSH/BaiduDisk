package com.csh.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CookieUtil {

    private static Logger logger = Logger.getLogger(CookieUtil.class);

    public static final List<String> cookies = new ArrayList<>();

    public static final Properties properties = new Properties();

    private static final File cookieFile = new File(System.getProperty("user.dir") + "/config/cookie");

    private static final String WARN = "#请不要随意更改文件内容，当然更改甚至删除也没事，重新登录就好！\r\n";

    static {
        FileInputStream stream = null;
        try {
            if (!cookieFile.exists()) {
                cookieFile.getParentFile().mkdir();
                cookieFile.createNewFile();
            }

            stream = new FileInputStream(cookieFile);

            properties.load(stream);

            if (!properties.isEmpty()) {
                properties.forEach((key, value) -> {
                    cookies.add(key + "=" + value);
                });

            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    private static void loadProperties() {
        FileInputStream stream = null;
        try {
            if (!cookieFile.exists()) {
                cookieFile.getParentFile().mkdir();
                cookieFile.createNewFile();
            }

            stream = new FileInputStream(cookieFile);

            properties.load(stream);

            if (!properties.isEmpty()) {
                properties.forEach((key, value) -> {
                    cookies.add(key + "=" + value);
                });

            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 将Cookie保存到属性文件中
     *
     * @param cookies
     */
    public static void setCookies(String... cookies) {
        FileWriter writer = null;

        try {
            writer = new FileWriter(cookieFile);
            if (cookies == null) {
                writer.write(WARN);
            } else {
                writer.write(WARN + StringUtils.join(cookies, "\r\n"));
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                    loadProperties();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public static void main(String[] args) {

//        setCookies("aa=11", "bb=22");

    }
}