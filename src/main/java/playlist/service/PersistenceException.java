package playlist.service;

public class PersistenceException extends RuntimeException {

	private static final long serialVersionUID = 830133560105580005L;

	public PersistenceException(String message) {
		super(message);
	}

	public PersistenceException(String message, Throwable cause) {
		super(message, cause);
	}

}
