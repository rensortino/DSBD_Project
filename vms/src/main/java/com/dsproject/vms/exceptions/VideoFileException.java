package com.dsproject.vms.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR,  reason = "Error saving video")
public class VideoFileException extends RuntimeException {
}
