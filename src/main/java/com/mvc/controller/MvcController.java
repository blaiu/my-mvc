package com.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mvc.annotation.Controller;
import com.mvc.annotation.Quatifier;
import com.mvc.annotation.RequestMapping;
import com.mvc.service.MyService;
import com.mvc.service.SpringmvcService;

/**
 * @author bailu
 */
@Controller("mvc")
public class MvcController {

	@Quatifier("MyServiceImpl")
	MyService myService;

	@Quatifier("SpringmvcServiceImpl")
	SpringmvcService smService;
	
	@RequestMapping("insert")
	public String insert(HttpServletRequest request, HttpServletResponse response, String param) {
		myService.insert(null);
		smService.insert(null);
		return null;
	}
	
	@RequestMapping("delete")
	public String delete(HttpServletRequest request, HttpServletResponse response, String param) {
		myService.delete(null);
		smService.delete(null);
		return null;
	}
	
	@RequestMapping("update")
	public String update(HttpServletRequest request, HttpServletResponse response, String param) {
		myService.update(null);
		smService.update(null);
		return null;
	}
	
	@RequestMapping("select")
	public String select(HttpServletRequest request, HttpServletResponse response, String param) {
		myService.select(null);
		smService.select(null);
		return null;
	}
	
}
