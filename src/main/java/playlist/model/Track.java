package playlist.model;

import java.util.UUID;

public class Track {

	private UUID id;
	private String artist;
	private String track;
	private String genre;
	private Boolean starred;
	private String musicFile;
	private int length;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getTrack() {
		return track;
	}

	public void setTrack(String track) {
		this.track = track;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public Boolean getStarred() {
		return starred;
	}

	public void setStarred(Boolean starred) {
		this.starred = starred;
	}

	public String getMusicFile() {
		return musicFile;
	}

	public void setMusicFile(String musicFile) {
		this.musicFile = musicFile;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

}
