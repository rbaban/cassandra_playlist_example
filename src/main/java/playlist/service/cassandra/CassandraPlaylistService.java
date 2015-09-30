package playlist.service.cassandra;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import playlist.model.Playlist;
import playlist.model.PlaylistTrack;
import playlist.model.Track;
import playlist.service.PersistenceException;
import playlist.service.PlaylistService;
import playlist.service.TrackService;

@Component
public class CassandraPlaylistService implements PlaylistService {

	@Autowired
	private CassandraOperations template;

	@Autowired
	private TrackService trackService;

	@Override
	public List<String> getUsersPlaylistNames(String username) {
		String query = "SELECT playlist_names FROM users where username = ?";
		PreparedStatement preparedStatement = getSession().prepare(query);
		BoundStatement bs = preparedStatement.bind(username);
		Row row = getSession().execute(bs).one();

		TreeSet<String> playlistNames = new TreeSet<>();
		if (row != null) {
			playlistNames.addAll(row.getSet("playlist_names", String.class));
		}

		return new ArrayList<>(playlistNames);
	}

	@Override
	public Playlist createPlaylist(String username, String playlistName) {
		String query = "UPDATE users set playlist_names = playlist_names + {'" + playlistName + "'} WHERE username = ?";

		PreparedStatement preparedStatement = getSession().prepare(query);
		BoundStatement bs = preparedStatement.bind(username);
		getSession().execute(bs);

		Playlist playlist = new Playlist();
		playlist.setName(playlistName);
		playlist.setUsername(username);
		return playlist;
	}

	@Override
	public Playlist deletePlaylist(String username, String playlistName) {

		Playlist deletedPlaylist = findByUserAndPlaylistName(username, playlistName);
		
		if (deletedPlaylist == null){
			throw new PersistenceException(String.format("User %s playlist %s not found", username, playlistName));
		}

		String query = "BEGIN BATCH " + "UPDATE users set playlist_names = playlist_names - {'" + playlistName
				+ "'} WHERE username = ? " + "DELETE FROM playlist_tracks WHERE username = ? and playlist_name = ? "
				+ "APPLY BATCH;";

		PreparedStatement preparedStatement = getSession().prepare(query);
		BoundStatement bs = preparedStatement.bind(username, username, username);
		getSession().execute(bs);

		return deletedPlaylist;
	}

	@Override
	public Playlist findByUserAndPlaylistName(String username, String playlistName) {
		String query = "SELECT username, playlist_name, sequence_no, artist, track_name, track_id, genre, track_length_in_seconds "
				+ "FROM playlist_tracks WHERE username = ? and playlist_name = ?";
		PreparedStatement statement = getSession().prepare(query);
		BoundStatement boundStatement = statement.bind(username, playlistName);
		ResultSet resultSet = getSession().execute(boundStatement);

		Playlist playlist = new Playlist();
		playlist.setName(playlistName);
		playlist.setUsername(username);

		List<PlaylistTrack> trackList = new ArrayList<>();
		for (Row row : resultSet) {
			trackList.add(createPlaylistTrackFromRow(row));
		}
		playlist.setTrackList(trackList);

		return playlist;
	}

	@Override
	public Playlist addTrack(UUID trackId, String username, String playlistName) {

		Track track = trackService.getTrackById(trackId);
		if (track == null) {
			throw new PersistenceException(String.format("Track %s not found", trackId));
		}

		Playlist playlist = findByUserAndPlaylistName(username, playlistName);
		if (playlist == null) {
			throw new PersistenceException(String.format("User %s playlist %s not found", username, playlistName));
		}

		PlaylistTrack playlistTrack = convertToPlaylistTrack(track);
		playlist.getTrackList().add(playlistTrack);

		savePlaylistTrack(username, playlistName, playlistTrack);

		return playlist;
	}

	@Override
	public Playlist deleteTrack(long seequenceNumber, String username, String playlistName) {
		// remove it from playlist_tracks
		PreparedStatement ps = getSession()
				.prepare("DELETE from playlist_tracks where username = ? and playlist_name = ? and sequence_no = ?");
		BoundStatement bs = ps.bind(username, playlistName, new Date(seequenceNumber));
		getSession().execute(bs);
				
		Playlist playlist = findByUserAndPlaylistName(username, playlistName);
		if(playlist == null){
			throw new PersistenceException(String.format("User's %s playlist %s not found", username, playlistName));
		}
		return playlist;
	}

	protected Session getSession() {
		return template.getSession();
	}
	
	private void savePlaylistTrack(String username, String playlistName, PlaylistTrack playlistTrack) {
		// save playlist track
		String query = "INSERT into playlist_tracks"
				+ " (username, playlist_name, sequence_no, artist, track_name, genre, track_id, track_length_in_seconds) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		// Prepare an insert statement
		PreparedStatement statement = getSession().prepare(query);
		BoundStatement boundStatement = statement.bind();
		
		// Let's use named parameters this time
		boundStatement.setString("username", username);
		boundStatement.setString("playlist_name", playlistName);
		boundStatement.setDate("sequence_no", new Date());
		boundStatement.setString("track_name", playlistTrack.getName());
		boundStatement.setString("artist", playlistTrack.getArtist());
		boundStatement.setUUID("track_id", playlistTrack.getId());
		boundStatement.setInt("track_length_in_seconds", playlistTrack.getLength());
		boundStatement.setString("genre", playlistTrack.getGenre());
		
		getSession().execute(boundStatement);
	}

	private playlist.model.PlaylistTrack createPlaylistTrackFromRow(Row row) {
		PlaylistTrack track = new PlaylistTrack();

		track.setArtist(row.getString("artist"));
		track.setGenre(row.getString("genre"));
		track.setId(row.getUUID("track_id"));
		track.setLength(row.getInt("track_length_in_seconds"));
		track.setName(row.getString("track_name"));
		track.setSequence(row.getDate("sequence_no").getTime());

		return track;
	}
	
	private PlaylistTrack convertToPlaylistTrack(Track track) {
		PlaylistTrack playlistTrack = new PlaylistTrack();
		playlistTrack.setArtist(track.getArtist());
		playlistTrack.setGenre(track.getGenre());
		playlistTrack.setLength(track.getLength());
		playlistTrack.setName(track.getTrack());
		playlistTrack.setSequence(new Date().getTime());
		return playlistTrack;
	}

}
