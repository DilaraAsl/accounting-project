package com.cydeo.aspect;

import com.cydeo.dto.CompanyDto;
import com.cydeo.service.CompanyService;
import com.cydeo.service.SecurityService;
import com.cydeo.dto.ClientVendorDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Aspect
@Component
@Slf4j
@AllArgsConstructor
public class LoggingAspect {
    @Pointcut("execution(* com.cydeo.service..*(..)) || execution(* com.cydeo.repository..*(..))")
    private void anyRuntimeException() {
    }

    @AfterThrowing(pointcut = "anyRuntimeException()", throwing = "exception")
    public void afterThrowingControllerAdvice(JoinPoint joinPoint, RuntimeException exception){
        log.info("After Throwing -> Method: {} - Exception: {} - Message: {}", joinPoint.getSignature().toShortString(), exception.getClass().getSimpleName(), exception.getMessage());
    }

   private final CompanyService companyService;

     SecurityService securityService;

    private String getUserName(){
        return securityService.getLoggedInUser().getUsername();

    }
    private String getFullName(){
        return securityService.getLoggedInUser().getFirstname()+ " "+ securityService.getLoggedInUser().getLastname();
    }
    @Pointcut( "execution(* com.cydeo.service.impl.CompanyServiceImpl.activateCompany(*))|| execution(* com.cydeo.service.impl.CompanyServiceImpl.deactivateCompany(*))")
    public void activateDeactivateCompany(){

    }
    @AfterReturning(value = "activateDeactivateCompany()" )
     public void afterActivateDeactivateCompany(JoinPoint joinPoint){
        Long id=((Long) Arrays.stream(joinPoint.getArgs()).findFirst().get()).longValue();

        log.info("After> Method: {}, User: {}, Name & LastName: {}, Argument: {}",
                joinPoint.getSignature().toShortString()
                ,getUserName()
                ,getFullName()
                ,companyService.findById(id).getTitle());

    }


}
