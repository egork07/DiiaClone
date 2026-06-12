package org.example.diiaclone.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* org.example.diiaclone.controller..*(..))")
    public void controllerMethods() {}

    @Pointcut("execution(* org.example.diiaclone.service..*(..))")
    public void serviceMethods() {}

    @Before("controllerMethods()")
    public void logRequest(JoinPoint joinPoint) {
        log.info("[REQUEST] {}.{}() args={}",
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logResponse(JoinPoint joinPoint, Object result) {
        log.info("[RESPONSE] {}.{}() returned={}",
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName(),
                result);
    }

    @Around("serviceMethods()")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        String className  = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;

            log.debug("[SERVICE] {}.{}() completed in {} ms",
                    className, methodName, elapsed);

            return result;

        } catch (Exception ex) {
            long elapsed = System.currentTimeMillis() - start;

            log.warn("[SERVICE] {}.{}() failed after {} ms — {}",
                    className, methodName, elapsed, ex.getMessage());

            throw ex;
        }
    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "ex")
    public void logControllerException(JoinPoint joinPoint, Exception ex) {
        log.error("[EXCEPTION] {}.{}() threw {}: {}",
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName(),
                ex.getClass().getSimpleName(),
                ex.getMessage());
    }
}
