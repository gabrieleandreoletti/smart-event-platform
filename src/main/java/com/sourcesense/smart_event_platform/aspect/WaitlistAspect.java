package com.sourcesense.smart_event_platform.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class WaitlistAspect {

    @Pointcut("execution(* com.sourcesense.smart_event_platform.service.implementation.WaitlistService.addToWaitList(..))")
    public void addWaitlist() {
    }

    @Pointcut("execution(* com.sourcesense.smart_event_platform.service.implementation.WaitlistService.handlePromotion(..))")
    public void handlePromotion() {
    }

    @After(value = "waitlist()")
    public void logAfterAdding(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        log.info("{} Added to the Waitlist of the event {}", args[0], args[1]);
    }

    @Around(value = "handlePromotion()")
    public String logAftePromotion(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        String result = (String) joinPoint.proceed();

        log.info("{} he got the place for the event {}", result, args[0]);

        return result;
    }
}
