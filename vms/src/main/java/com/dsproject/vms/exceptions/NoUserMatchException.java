package com.dsproject.vms.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST,  reason = "You are not the video's owner")
public class NoUserMatchException extends RuntimeException {
}
