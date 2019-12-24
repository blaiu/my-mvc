package com.mvc.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mvc.annotation.Controller;
import com.mvc.annotation.Quatifier;
import com.mvc.annotation.RequestMapping;
import com.mvc.annotation.Service;
import com.mvc.controller.MvcController;

/**
 * @author bailu
 */
@WebServlet("/DispatcherServlet")
public class DispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = -2451704940400948111L;
	
	/** 存放Class */
	List<String> classNames = new ArrayList<String>();
	
	/** 存放Class 的实例 */
	Map<String, Object> instanceMap = new ConcurrentHashMap<String, Object>();
	
	/** 存放映射关系 */
	Map<String, Object> handerMap = new ConcurrentHashMap<String, Object>();
	
	public DispatcherServlet() {
		super();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		scanPackage("com.mvc");
		try {
			filterAndInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		handlerMap();
		ioc();
	}

	/**
	 * 扫描包下面的类 缓存至classNames
	 * @param Package
	 */
	private void scanPackage(String Package) {
		URL url = this.getClass().getClassLoader().getResource("/" + replaceTo(Package));
		if (url == null) {
			return;
		}
		String pathFile = url.getFile();
		File file = new File(pathFile);
		String fileList[] = file.list();
		
		for (String path : fileList) {
			File eachFile = new File(pathFile + path);
			if (eachFile.isDirectory()) {
				scanPackage(Package + "." + eachFile.getName());
			} else {
//				System.out.println("package: " + Package + "." + eachFile.getName());
				classNames.add(Package + "." + eachFile.getName());
			}
		}
	}
	
	private String replaceTo(String path) {
		return path.replaceAll("\\.", "/");
	}
	
	/**
	 * 实例化被 @Controller 和 @Service 注解的类 缓存至instanceMap
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private void filterAndInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (classNames.size() <= 0) {
			return;
		}
		
		for (String className : classNames) {
			Class<?> cName = Class.forName(className.replace(".class", ""));
			if (cName.isAnnotationPresent(Controller.class)) {
				//实例化
				Object instance = cName.newInstance();
				Controller controller = cName.getAnnotation(Controller.class);
				String key = controller.value();
				System.out.println("instance: {key: "+key+", value: "+instance.getClass().getName()+"}");
				instanceMap.put(key, instance);
			} else if(cName.isAnnotationPresent(Service.class)) {
				//实例化
				Object instance = cName.newInstance();
				Service service = cName.getAnnotation(Service.class);
				String key = service.value();
				System.out.println("instance: {key: "+key+", value: "+instance.getClass().getName()+"}");
				instanceMap.put(key, instance);
			} else {
				continue;
			}
		}
	}
	
	/**
	 * 方法和路径的映射关系
	 */
	private void handlerMap() {
		if (instanceMap.size() <= 0) {
			return;
		}
		
		for (Map.Entry<String, Object> entry : instanceMap.entrySet()) {
			if (entry.getValue().getClass().isAnnotationPresent(Controller.class)) {
				Controller controller = entry.getValue().getClass().getAnnotation(Controller.class);
				String ctvalue = controller.value();
				Method[] methods = entry.getValue().getClass().getMethods();
				
				for (Method method : methods) {
					if (method.isAnnotationPresent(RequestMapping.class)) {
						RequestMapping rm = method.getAnnotation(RequestMapping.class);
						String rmvalue = rm.value();
						System.out.println("handler: {key:"+"/" + ctvalue + "/" + rmvalue+", value: "+method.getName()+"}");
						handerMap.put("/" + ctvalue + "/" + rmvalue, method);
					} else {
						continue;
					}
				}
			} else {
				continue;
			}
		}
	}
	
	/**
	 * 声明的属性和对应的实现类转换
	 */
	private void ioc() {
		if (instanceMap.isEmpty()) {
			return;
		}
		
		for (Map.Entry<String, Object> entry : instanceMap.entrySet()) {
			Field fields[] = entry.getValue().getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				if (field.isAnnotationPresent(Quatifier.class)); {
					Quatifier quatifier = field.getAnnotation(Quatifier.class);
					String value = quatifier.value();
					field.setAccessible(true);
					
					try {
						System.out.println("field: {key:"+entry.getValue()+", value:"+instanceMap.get(value)+"}");
						field.set(entry.getValue(), instanceMap.get(value));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String url = req.getRequestURI();
		String context = req.getContextPath();
		String path = url.replace(context, "");
		Method method = (Method) handerMap.get(path);
		MvcController controller = (MvcController) instanceMap.get(path.split("/")[1]);
		
		try {
			method.invoke(controller, new Object[] {req, resp, null});
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
}
