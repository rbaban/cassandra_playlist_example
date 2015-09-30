package playlist.service;

public class UserExistsException extends PersistenceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8807720465738408911L;

	public UserExistsException(String message) {
		super(message);
	}

}
