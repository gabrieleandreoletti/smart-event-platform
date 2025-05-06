package com.sourcesense.smart_event_platform.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggerAspect {

    @Pointcut("execution(* com.sourcesense.smart_event_platform.controller.implementation.*.*(..))")
    public void controllerMethods() {
    }

    @Before(value = "controllerMethods()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Calling method : {} | Args: {}", joinPoint.getSignature(), Arrays.toString(joinPoint.getArgs()));
    }

    @Around(value = "controllerMethods()")
    public Object checkPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object o = joinPoint.proceed();
        long endTime = System.currentTimeMillis() - startTime;

        log.info("Performance monitor : {}ms", endTime);

        return o;
    }

    @AfterReturning(value = "controllerMethods()", returning = "dto")
    public void logAfter(JoinPoint joinPoint, Object dto) {
        log.info("Result of method : {} , |{}|", joinPoint.getSignature(), dto);
    }
}
