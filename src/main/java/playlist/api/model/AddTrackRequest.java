package playlist.api.model;

import java.util.UUID;

public class AddTrackRequest {

	private UUID trackId;

	public UUID getTrackId() {
		return trackId;
	}

	public void setTrackId(UUID trackId) {
		this.trackId = trackId;
	}

}
