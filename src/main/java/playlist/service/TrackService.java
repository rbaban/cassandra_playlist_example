package playlist.service;

import java.util.List;
import java.util.UUID;

import playlist.model.Track;

public interface TrackService {
	
	void save(Track track);
	
	void markAsStar(Track track);
	
	List<Track> listTrackByArtist(String artistName);

	List<Track> listTrackByGenre(String genre, int limit);

	Track getTrackById(UUID id);
	
}
