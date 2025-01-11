package it.angelodesantis.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends CustomException{

	public NotFoundException(final String resourceCode) {
		withHttpStatus(HttpStatus.NOT_FOUND).withErrorCodes("service.notFound."+resourceCode);
	}
	
	public NotFoundException(final Class<?> resourceType) {
        this(resourceType.getSimpleName());
    }
}