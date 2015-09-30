package playlist.model;

import java.util.SortedSet;

public class User {

	private String username;
	private SortedSet<String> playlistNames;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public SortedSet<String> getPlaylistNames() {
		return playlistNames;
	}

	public void setPlaylistNames(SortedSet<String> playlistNames) {
		this.playlistNames = playlistNames;
	}

}
