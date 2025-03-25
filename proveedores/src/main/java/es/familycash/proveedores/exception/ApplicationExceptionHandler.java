package es.familycash.proveedores.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import es.familycash.proveedores.bean.ErrorBean;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorBean> ApplicationExceptionHandler(Exception ex) {
        ErrorBean oErrorBean = new ErrorBean();
        oErrorBean.setMessage(ex.getMessage());
        oErrorBean.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        oErrorBean.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(oErrorBean, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorBean> ResourceNotFoundException(ResourceNotFoundException exception) {
        ErrorBean oErrorBean = new ErrorBean();
        oErrorBean.setMessage(exception.getMessage());
        oErrorBean.setStatus(HttpStatus.NOT_FOUND.value());
        oErrorBean.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(oErrorBean, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ResourceNotModifiedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorBean> ResourceNotModifiedException(ResourceNotModifiedException exception) {
        ErrorBean oErrorBean = new ErrorBean();
        oErrorBean.setMessage(exception.getMessage());
        oErrorBean.setStatus(HttpStatus.NOT_FOUND.value());
        oErrorBean.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(oErrorBean, HttpStatus.NOT_MODIFIED);
    }

    @ExceptionHandler(value = NotAcceptableException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorBean> NotAcceptableException(NotAcceptableException exception) {
        ErrorBean oErrorBean = new ErrorBean();
        oErrorBean.setMessage(exception.getMessage());
        oErrorBean.setStatus(HttpStatus.NOT_FOUND.value());
        oErrorBean.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(oErrorBean, HttpStatus.NOT_ACCEPTABLE);
    }


}
