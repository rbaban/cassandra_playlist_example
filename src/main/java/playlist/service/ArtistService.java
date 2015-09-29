package playlist.service;

import java.util.List;

import playlist.model.Artist;

public interface ArtistService {
	List<Artist> findArtistsByFirstLetter(String firstLetter);
}
