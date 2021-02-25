package cz4013.shared.serialize;

public class SerializingException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public SerializingException() {
    }
  
    public SerializingException(String message) {
      super(message);
    }
  
    public SerializingException(String message, Throwable cause) {
      super(message, cause);
    }
  
    public SerializingException(Throwable cause) {
      super(cause);
    }
  
    public SerializingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
    }
  }

