package playlist.model;

import java.util.ArrayList;
import java.util.List;

public class Playlist {

	private String name;
	private String username;
	private List<PlaylistTrack> trackList = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<PlaylistTrack> getTrackList() {
		return trackList;
	}

	public void setTrackList(List<PlaylistTrack> trackList) {
		this.trackList = trackList;
	}

	public int getLength() {
		int playlistLength = 0;
		if (trackList != null) {
			for (PlaylistTrack playlistTrack : trackList) {
				playlistLength += playlistTrack.getLength();
			}
		}
		return playlistLength;
	}

}
