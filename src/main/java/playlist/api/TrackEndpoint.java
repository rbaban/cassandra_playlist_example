package playlist.api;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import playlist.api.model.FilterByArtistRequest;
import playlist.api.model.FilterByGenreRequest;
import playlist.model.Track;
import playlist.service.TrackService;

@Api(value = "/tracks", description = "Endpoint for tracks listing")
@Component
@Path("/tracks")
@Produces(MediaType.APPLICATION_JSON)
public class TrackEndpoint {

	@Autowired
	private TrackService service;

	@Context
	private UriInfo uriInfo;

	@GET
	@Path("{id}")
	@ApiOperation(value = "Finds track", notes = "Returns track.", response = Track.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful retrieval of track by track id", response = Track.class),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response findByTrackID(@PathParam("id") UUID id) {
		Track track = service.getTrackById(id);
		return Response.ok(track).build();
	}

	@GET
	@Path("artist")
	@ApiOperation(value = "Filter tracks by artist", notes = "Returns list of tracks whose artist match parameterr.", responseContainer = "array", response = Track.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful retrieval of artists by first letter", response = Track.class),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response findByArtistName(FilterByArtistRequest request) {
		List<Track> tracks = service.listTrackByArtist(request.getArtistName());
		return Response.ok(tracks).build();
	}

	@GET
	@Path("genre")
	@ApiOperation(value = "Filter tracks by genre", notes = "Returns list of tracks whose genre match parameter.", responseContainer = "array", response = Track.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful retrieval of artists by first letter", response = Track.class),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response findByGenre(FilterByGenreRequest request) {
		List<Track> tracks = service.listTrackByGenre(request.getGenre(), request.getLimit());
		return Response.ok(tracks).build();
	}

	@POST
	@ApiOperation(value = "Saves a track", notes = "Saves a track.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful saving of the track"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response save(Track track) {
		service.save(track);
		URI location = uriInfo.getAbsolutePathBuilder().path("{id}").resolveTemplate("id", track.getId()).build();
		return Response.created(location).build();
	}
}
