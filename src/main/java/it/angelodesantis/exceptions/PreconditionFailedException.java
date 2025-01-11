package it.angelodesantis.exceptions;

import org.springframework.http.HttpStatus;

public class PreconditionFailedException extends CustomException{
	
	public PreconditionFailedException(final String resourceCode) {
		withHttpStatus(HttpStatus.CONFLICT).withErrorCodes("preconditionFailed."+resourceCode);
	}
	
	public PreconditionFailedException(final String resourceCode, String reason) {
		withHttpStatus(HttpStatus.CONFLICT).withErrorCodes("preconditionFailed."+resourceCode+"."+reason);
	}
	
	public PreconditionFailedException(final Class<?> resourceType) {
        this(resourceType.getSimpleName());
    }
}

