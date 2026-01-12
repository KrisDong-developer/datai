package com.salesforce.multicloudj.common.exceptions;

public class ExceptionHandler {
  public static void handleAndPropagate(Class<? extends SubstrateSdkException> exceptionClass, Throwable t) throws SubstrateSdkException {
    if (exceptionClass == null)
      throw new UnknownException(t); 
    if (ResourceAlreadyExistsException.class.isAssignableFrom(exceptionClass))
      throw new ResourceAlreadyExistsException(t); 
    if (ResourceNotFoundException.class.isAssignableFrom(exceptionClass))
      throw new ResourceNotFoundException(t); 
    if (ResourceConflictException.class.isAssignableFrom(exceptionClass))
      throw new ResourceConflictException(t); 
    if (UnAuthorizedException.class.isAssignableFrom(exceptionClass))
      throw new UnAuthorizedException(t); 
    if (ResourceExhaustedException.class.isAssignableFrom(exceptionClass))
      throw new ResourceExhaustedException(t); 
    if (InvalidArgumentException.class.isAssignableFrom(exceptionClass))
      throw new InvalidArgumentException(t); 
    if (FailedPreconditionException.class.isAssignableFrom(exceptionClass))
      throw new FailedPreconditionException(t); 
    if (TransactionFailedException.class.isAssignableFrom(exceptionClass))
      throw new TransactionFailedException(t); 
    if (UnknownException.class.isAssignableFrom(exceptionClass))
      throw new UnknownException(t); 
    if (SubstrateSdkException.class.isAssignableFrom(exceptionClass))
      throw (SubstrateSdkException)t; 
    throw new UnknownException(t);
  }
}
