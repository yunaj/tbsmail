package com.hs.mail.webmail.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.hs.mail.webmail.exception.WmaException;

@ControllerAdvice
public class ExceptionController {

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(WmaException.class)
	public ModelAndView handleException(WmaException ex) {
		ModelAndView mav = new ModelAndView("error/werror");
		mav.addObject("error", ex);
		return mav;
	}

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public ModelAndView handleException(Exception ex) {
		ModelAndView mav = new ModelAndView("error/error");
		mav.addObject("error", ex);
		return mav;
	}
	
}
