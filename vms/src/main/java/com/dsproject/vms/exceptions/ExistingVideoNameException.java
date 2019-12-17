package com.dsproject.vms.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST,  reason = "Name of video already taken")
public class ExistingVideoNameException extends RuntimeException {
}
