package com.nbc.newsfeeds.common.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LogAop {
	@Pointcut("execution(* com.nbc.newsfeeds.domain..*Controller.*(..))")
	private void logPointcut() {
	}

	@Before("logPointcut()")
	public void logBefore(JoinPoint joinPoint) {
		Method method = getMethod(joinPoint);
		log.info("메서드 실행 전");
		getMethodInfo(method);

		Object[] args = joinPoint.getArgs();
		if (args.length == 0) {
			log.info("no parameters");
		} else {
			getParameterInfo(method, args);
		}
	}

	@AfterReturning(value = "logPointcut()", returning = "returnObject")
	public void logAfterReturning(JoinPoint joinPoint, Object returnObject) {
		Method method = getMethod(joinPoint);
		log.info("메서드 실행 후");
		getMethodInfo(method);
		log.info("return type : {}", returnObject.getClass().getSimpleName());
	}

	private Method getMethod(JoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();

		return signature.getMethod();
	}

	private void getMethodInfo(Method method) {
		log.info("Method name = {}", method.getName());
	}

	private void getParameterInfo(Method method, Object[] args) {
		Parameter[] parameters = method.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			String parameterName = parameters[i].getName();
			Object arg = (args != null && args[i] != null) ? args[i] : null;

			if (arg == null) {
				log.info("parameter {} is null", parameterName);
			} else {
				log.info("parameter {} : {}", arg.getClass().getSimpleName(), parameterName);
			}
		}
	}

}
