package com.example.crypto.klineservice.config;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;


@Component
@Aspect
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    /**
     * Help to track how long our program take to insert n kline data
     * to the database
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(public * com.example.crypto.klineservice.service.KlineService.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        // log as error if too slow
        if (executionTime > 10000) {
            logger.error("{} took too long: {} ms", joinPoint.getSignature(), executionTime);
        } else {
            logger.info("{} executed in {} ms", joinPoint.getSignature(), executionTime);
        }
        return proceed;
    }

}
