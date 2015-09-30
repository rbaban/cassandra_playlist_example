package playlist.service;

import java.util.List;
import java.util.UUID;

import playlist.model.Playlist;

public interface PlaylistService {

	Playlist createPlaylist(String username, String playlistName);

	Playlist deletePlaylist(String username, String playlistName);
	
	List<String> getUsersPlaylistNames(String username);

	Playlist findByUserAndPlaylistName(String username, String playlistName);

	Playlist addTrack(UUID trackId, String username, String playlistName);

	Playlist deleteTrack(long trackSeequence, String username, String playlistName);

}
