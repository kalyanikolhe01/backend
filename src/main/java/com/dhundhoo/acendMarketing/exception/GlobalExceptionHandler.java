//package com.dhundhoo.acendMarketing.exception;
//
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    // Handle validation errors globally
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
//        Map<String, String> errors = new HashMap<>();
//        BindingResult bindingResult = ex.getBindingResult();
//
//        for (FieldError error : bindingResult.getFieldErrors()) {
//            errors.put(error.getField(), error.getDefaultMessage());
//        }
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
//    }
//
//    // Handle DuplicateFieldException (when email or mobile number already exists)
//    @ExceptionHandler(DuplicateFieldException.class)
//    public ResponseEntity<?> handleDuplicateFieldException(DuplicateFieldException ex) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
//    }
//
//    // Handle other general exceptions globally
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleGeneralException(Exception ex) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong.");
//    }
//}
