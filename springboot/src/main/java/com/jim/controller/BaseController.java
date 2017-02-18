package com.jim.controller;

import com.jim.common.ErrorDef;
import com.jim.common.ErrorInfo;
import com.jim.common.ValueResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by jiang on 2016/12/5.
 */
public abstract class BaseController {

    protected Logger logger = LoggerFactory.getLogger(getClass().getName());

    private String getConstraintViolationsMsg(Set<ConstraintViolation<?>> constraintViolations){
        if(null==constraintViolations){
            return "";
        }
        StringBuffer stringBuffer=new StringBuffer();
        Iterator<ConstraintViolation<?>> iterable= constraintViolations.iterator();
        while (iterable.hasNext()){
            ConstraintViolation<?> constraintViolation= iterable.next();
            stringBuffer.append(constraintViolation.getMessage()+",");
        }
        return stringBuffer.toString();
    }

    private String getAllParametersInValidMsg(List<ObjectError> allErrors){
        if(null==allErrors){
            return "";
        }
        StringBuffer stringBuffer=new StringBuffer();
        for(ObjectError error:allErrors){
            if(null!=error) {
                stringBuffer.append(error.getDefaultMessage() + ",");
            }
        }
        return stringBuffer.toString();
    }

    public <T> ValueResult<T> returnValueSuccess(T value) {
        ValueResult<T> result = new ValueResult<T>();
        result.setResult(true);
        result.setValue(value);

        return result;
    }

    @ExceptionHandler
    public ValueResult exception(HttpServletRequest request, Exception ex) {

        ValueResult result=new ValueResult();
        if(ex instanceof ConstraintViolationException){
            ConstraintViolationException constraintViolationException=(ConstraintViolationException)ex;
            Set<ConstraintViolation<?>> constraintViolations= constraintViolationException.getConstraintViolations();
            String errorMsg=this.getConstraintViolationsMsg(constraintViolations);
            result.setError(new ErrorInfo(ErrorDef.InvalidParameters.getCode(),errorMsg));
            result.setResult(false);
        }
        else if(ex instanceof MethodArgumentNotValidException){
            MethodArgumentNotValidException methodArgumentNotValidException=(MethodArgumentNotValidException)ex;
            List<ObjectError> allErrors= methodArgumentNotValidException.getBindingResult().getAllErrors();
            String errorMsg=this.getAllParametersInValidMsg(allErrors);
            result.setError(new ErrorInfo(ErrorDef.InvalidParameters.getCode(),errorMsg));
        }
        else {

            logger.error("system error", ex);
            result.setError(new ErrorInfo(ErrorDef.ServerError.getCode(), ex.getMessage()));
            result.setResult(false);
        }
        return result;
    }
}
