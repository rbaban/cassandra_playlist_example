package playlist.service.cassandra;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import playlist.model.Track;
import playlist.service.TrackService;

//TODO handle prepared statements only once

@Component
public class CassandraTrackService implements TrackService {

	@Autowired
	private CassandraOperations template;

	@Override
	public List<Track> listTrackByArtist(String artistName) {
		
		String queryText = "SELECT * FROM track_by_artist WHERE artist = ?";

		PreparedStatement preparedStatement = getSession().prepare(queryText);
		BoundStatement boundStatement = preparedStatement.bind(artistName);
		ResultSet results = getSession().execute(boundStatement);

		List<Track> tracks = new ArrayList<>();
		for (Row row : results) {
			tracks.add(createStarredTrackFromRow(row));
		}

		return tracks;
	}

	@Override
	public List<Track> listTrackByGenre(String genre, int numberOfTracks) {
		String queryText = "SELECT * FROM track_by_genre WHERE genre = ? LIMIT ?";

		PreparedStatement preparedStatement = getSession().prepare(queryText);
		BoundStatement boundStatement = preparedStatement.bind(genre, numberOfTracks);
		boundStatement.setFetchSize(200);
		ResultSet results = getSession().execute(boundStatement);

		List<Track> tracks = new ArrayList<>();
		for (Row row : results) {
			tracks.add(createStarredTrackFromRow(row));
		}

		return tracks;
	}

	@Override
	public Track getTrackById(UUID id) {
		String query = "SELECT * FROM track_by_id WHERE track_id = ?";

		PreparedStatement preparedStatement = getSession().prepare(query);
		BoundStatement boundStatement = preparedStatement.bind(id);
		ResultSet resultSet = getSession().execute(boundStatement);
		if (resultSet.isExhausted()) {
			return null;
		}
		return createBaseTrackFromRow(resultSet.one());
	}

	@Override
	public void save(Track track) {
		if (track.getId() == null) {
			track.setId(UUID.randomUUID());
		}
		saveInArtistsByFirstLetter(track);
		saveInTrackByIdTable(track);
		saveInTrackByGenreTable(track);
		saveInTrackByArtistTable(track);
	}

	@Override
	public void markAsStar(Track track) {
		markAsStarInTrackByArtistTable(track);
		markAsStarInTrackByGenreTable(track);
	}

	private void markAsStarInTrackByGenreTable(Track track) {
		PreparedStatement preparedStatement = getSession().prepare(
				"UPDATE track_by_genre  USING TTL 60 SET starred = true where genre = ? and artist = ? and track = ? and track_id = ?");
		BoundStatement boundStatement = preparedStatement.bind(track.getArtist(), track.getTrack(), track.getId());
		getSession().execute(boundStatement);
	}

	private void markAsStarInTrackByArtistTable(Track track) {
		PreparedStatement preparedStatement = getSession().prepare(
				"UPDATE track_by_artist  USING TTL 60 SET starred = true where artist = ? and track = ? and track_id = ?");
		BoundStatement boundStatement = preparedStatement.bind(track.getArtist(), track.getTrack(), track.getId());
		getSession().execute(boundStatement);
	}
	
	private void saveInTrackByArtistTable(Track track) {
		PreparedStatement preparedStatement = getSession().prepare(
				"INSERT INTO track_by_artist (genre, track_id, artist, track, track_length_in_seconds) VALUES (?, ?, ?, ?, ?)");
		BoundStatement boundStatement = preparedStatement.bind(track.getGenre(), track.getId(), track.getArtist(),
				track.getTrack(), track.getLength());
		getSession().execute(boundStatement);
	}

	private void saveInTrackByGenreTable(Track track) {
		PreparedStatement preparedStatement = getSession().prepare(
				"INSERT INTO track_by_genre (genre, track_id, artist, track, track_length_in_seconds) VALUES (?, ?, ?, ?, ?)");
		BoundStatement boundStatement = preparedStatement.bind(track.getGenre(), track.getId(), track.getArtist(),
				track.getTrack(), track.getLength());
		getSession().execute(boundStatement);
	}

	private void saveInTrackByIdTable(Track track) {
		PreparedStatement preparedStatement = getSession().prepare(
				"INSERT INTO track_by_id (genre, track_id, artist, track, track_length_in_seconds) VALUES (?, ?, ?, ?, ?)");
		BoundStatement boundStatement = preparedStatement.bind(track.getGenre(), track.getId(), track.getArtist(),
				track.getTrack(), track.getLength());
		getSession().execute(boundStatement);
	}

	private void saveInArtistsByFirstLetter(Track track) {
		String artistFirstLetter = track.getArtist().substring(0, 1).toUpperCase();

		PreparedStatement preparedStatement = getSession()
				.prepare("INSERT INTO artists_by_first_letter (first_letter, artist) VALUES (?, ?)");
		BoundStatement boundStatement = preparedStatement.bind(artistFirstLetter, track.getArtist());
		getSession().execute(boundStatement);
	}


	private Track createStarredTrackFromRow(Row row) {
		Track track = createBaseTrackFromRow(row);
		track.setStarred(row.getBool("starred"));
		return track;
	}
	
	private Track createBaseTrackFromRow(Row row) {
		Track track = new Track();

		track.setId(row.getUUID("track_id"));
		track.setArtist(row.getString("artist"));
		track.setTrack(row.getString("track"));
		track.setGenre(row.getString("genre"));
		track.setMusicFile(row.getString("music_file"));
		track.setLength(row.getInt("track_length_in_seconds"));

		return track;
	}

	protected Session getSession() {
		return template.getSession();
	}

}
