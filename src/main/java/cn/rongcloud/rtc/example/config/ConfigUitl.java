package cn.rongcloud.rtc.example.config;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigUitl {

	private static Logger logger = LoggerFactory.getLogger(ConfigUitl.class);

	public static final void initLocalConfig(Object instance, String path) throws Exception {
		try (FileInputStream in = new FileInputStream(path)) {

			Properties p = new Properties();
			p.load(in);

			Enumeration<?> names = p.propertyNames();
			while (names.hasMoreElements()) {
				String key = (String) names.nextElement();

				String value = p.getProperty(key);
				if (value == null) {
					logger.info("property " + key + " is null");
					continue;
				}
				value = value.trim();

				if (!ConfigUitl.setValue(instance, key.trim(), value)) {
					logger.info("set parameter " + key + " fail");
				}
			}
		}
	}

	public static final boolean setValue(Object instance, String name, String value) throws Exception {

		Class<?> clazz = instance.getClass();
		Field f;
		try {
			f = clazz.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			return false;
		}

		if (Modifier.isStatic(f.getModifiers())) {
			return false;
		}

		f.setAccessible(true);

		if (f.getType().equals(String.class)) {
			f.set(instance, value);
		} else if (f.getType().equals(int.class) || f.getType().equals(Integer.class)) {
			f.set(instance, Integer.parseInt(value));
		} else if (f.getType().equals(long.class) || f.getType().equals(Long.class)) {
			f.set(instance, Long.parseLong(value));
		} else if (f.getType().equals(boolean.class) || f.getType().equals(boolean.class)) {
			boolean b = (value.equals("true") || value.equals("1")) ? true : false;
			f.set(instance, b);
		} else {
			String fieldName = f.getName();
			String setMethodName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
			Method method = clazz.getDeclaredMethod(setMethodName, String.class);
			method.invoke(instance, value);
		}

		return true;
	}

	public static final void checkRequiredConfig(Object instance) throws Exception {

		Class<?> clazz = instance.getClass();

		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields) {
			f.setAccessible(true);
			if (f.getAnnotation(RequiredConfig.class) != null) {
				if (f.get(instance) == null) {
					throw new RuntimeException("Required Config " + f.getName() + " must be set, after config init");
				}
			}
			logger.info(f.getName() + "=" + f.get(instance));
		}

	}

	public static final void checkDefaultConfig(Object instance) throws Exception {

		Class<?> clazz = instance.getClass();

		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields) {
			if (f.getAnnotation(RequiredConfig.class) != null) {
				f.setAccessible(true);
				if (f.get(instance) != null) {
					throw new RuntimeException("Required Config " + f.getName() + " must not set default value");
				}
			}
		}
	}
}
