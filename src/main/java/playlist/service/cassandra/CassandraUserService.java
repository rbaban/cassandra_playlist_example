package playlist.service.cassandra;

import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import playlist.model.User;
import playlist.service.UserExistsException;
import playlist.service.UserService;

@Component
public class CassandraUserService implements UserService {

	@Autowired
	private CassandraOperations template;

	@Override
	public void save(User user) {
		String queryText = "INSERT INTO users (username) values (?) IF NOT EXISTS";

		PreparedStatement preparedStatement = getSession().prepare(queryText);

		// Because we use an IF NOT EXISTS clause, we get back a result set with
		// 1 row containing 1 boolean column called "[applied]"
		ResultSet resultSet = getSession().execute(preparedStatement.bind(user.getUsername()));

		// Determine if the user was inserted. If not, throw an exception.
		boolean userGotInserted = resultSet.one().getBool("[applied]");

		if (!userGotInserted) {
			String message = String.format("User %s already exists", user.getUsername());
			throw new UserExistsException(message);
		}
	}

	@Override
	public void delete(User user) {
		String queryText = "DELETE FROM users where username = ?";
		PreparedStatement preparedStatement = getSession().prepare(queryText);
		BoundStatement boundStatement = preparedStatement.bind(user.getUsername());

		// Delete users with CL = Quorum
		boundStatement.setConsistencyLevel(ConsistencyLevel.QUORUM);
		getSession().execute(boundStatement);
	}

	@Override
	public User findByUsername(String username) {
		String queryText = "SELECT * FROM users where username = ?";
		PreparedStatement preparedStatement = getSession().prepare(queryText);
		BoundStatement boundStatement = preparedStatement.bind(username);

		Row userRow = getSession().execute(boundStatement).one();

		if (userRow == null) {
			return null;
		}

		return createUserFromRow(userRow);
	}

	private User createUserFromRow(Row row) {
		User user = new User();
		user.setUsername(row.getString("username"));

		// We do this because we want a sorted set, and Cassandra only returns a
		// regular set
		// the driver gives us a HashLinkedSet. We need to choose our
		// implementation.
		TreeSet<Object> playlists = new TreeSet<>();
		playlists.addAll(row.getSet("playlist_names", String.class));
		
		return user;
	}

	protected Session getSession() {
		return template.getSession();
	}

}
