package playlist.service.cassandra;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import playlist.model.Artist;
import playlist.service.ArtistService;

//TODO handle prepared statements only once

@Component
public class CassandraArtistService implements ArtistService {

	@Autowired
	private CassandraOperations template;

	@Override
	public List<Artist> findArtistsByFirstLetter(String firstLetter) {
		Session session = template.getSession();

		String query = "SELECT * FROM artists_by_first_letter WHERE first_letter = ? ORDER BY artist DESC";
		
		PreparedStatement preparedStatement = session.prepare(query);
		BoundStatement boundStatement = preparedStatement.bind(firstLetter.toUpperCase());
		ResultSet results = session.execute(boundStatement);

		List<Artist> artists = new ArrayList<>();
		for (Row row : results) {
			Artist artist = new Artist();
			artist.setName(row.getString("artist"));
			artists.add(artist);
		}	

		return artists;
	}
	
	protected Session getSession() {
		return template.getSession();
	}

}
