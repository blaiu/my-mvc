package com.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author bailu
 *
 * @Target 用于设定注解使用范围
 * @ElementType.METHOD 可用于方法上
 * @ElementType.TYPE 可用于类或者接口上
 * @ElementType.ANNOTATION_TYPE 可用于注解类型上（被@interface修饰的类型）
 * @ElementType.CONSTRUCTOR 可用于构造方法上
 * @ElementType.FIELD 可用于域上
 * @ElementType.LOCAL_VARIABLE 可用于局部变量上
 * @ElementType.PACKAGE 用于记录Java文件的package信息
 * @ElementType.PARAMETER 可用于参数上
 * 
 * @RetentionPolicy
 * 1.SOURCE:在源文件中有效（即源文件保留）
 * 2.CLASS:在class文件中有效（即class保留）
 * 3.RUNTIME:在运行时有效（即运行时保留）
 * 
 * @Documented
 * 用于描述其它类型的annotation应该被作为被标注的程序成员的公共API，因此可以被例如javadoc此类的工具文档化。Documented是一个标记注解，没有成员。
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Quatifier {

	String value() default "";
	
}
