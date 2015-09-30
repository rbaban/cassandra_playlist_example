package playlist.service;

import playlist.model.User;

public interface UserService {

	void save(User user);

	void delete(User user);
	
	User findByUsername(String username);
}
