package com.thy.scrum_planning_be.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.UUID;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("@annotation(com.thy.scrum_planning_be.aspect.Loggable)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();

        // Traceability: Her işlem için unique bir ID oluştur veya var olanı al.
        String traceId = MDC.get("traceId");
        boolean isNewTrace = false;
        if (traceId == null) {
            traceId = UUID.randomUUID().toString().substring(0, 8);
            MDC.put("traceId", traceId);
            isNewTrace = true;
        }

        final StopWatch stopWatch = new StopWatch();

        // Giriş Logu
        log.info("▶ ENTRY [{}::{}] Args: {}", className, methodName, joinPoint.getArgs());

        Object result;
        try {
            stopWatch.start();
            result = joinPoint.proceed(); // Asıl metodu çalıştır
            stopWatch.stop();

            // Çıkış Logu (Başarılı)
            log.info("◀ EXIT  [{}::{}] Time: {} ms | Result: {}",
                    className, methodName, stopWatch.getTotalTimeMillis(), result);
        } catch (Exception e) {
            stopWatch.stop();
            // Hata Logu
            log.error("✖ FAIL  [{}::{}] Time: {} ms | Exception: {}",
                    className, methodName, stopWatch.getTotalTimeMillis(), e.getMessage());
            throw e; // Hatayı yutma, yukarı fırlat
        } finally {
            // Eğer trace'i biz başlattıysak, biz temizleriz. (Thread reuse kirliliğini önlemek için)
            if (isNewTrace) {
                MDC.remove("traceId");
            }
        }

        return result;
    }
}