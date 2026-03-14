package org.api.jobassist.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerConfig.class);

    @Pointcut("execution(* org.api.jobassist..*(..))")
    public void allMethod() {
        // This will handle pointcut of all the method execution
    }

    @Around("allMethod()")
    public Object logAround(ProceedingJoinPoint jointpoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            String className = jointpoint.getSignature().getDeclaringTypeName();
            String methodName = jointpoint.getSignature().getName();
            LOGGER.info("Entering method {}.{}()", className, methodName);

            String reqParams = Arrays.toString(jointpoint.getArgs());
            LOGGER.debug("Arguments passed : {}", reqParams);

            Object result = jointpoint.proceed();
            long elapsedTime = System.currentTimeMillis() - startTime;
            LOGGER.info("Exiting method {}.{}() execution time: {} ms", className, methodName, elapsedTime);

            return result;
        } catch (IllegalArgumentException e) {
            LOGGER.error("Illegal argument error in {}()", jointpoint.getSignature().getName());
            throw e;
        }

    }

}
