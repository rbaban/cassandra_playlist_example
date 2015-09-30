package playlist.api;

import java.net.URI;
import java.util.List;

import javax.ws.rs.DELETE;
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

import playlist.api.model.AddTrackRequest;
import playlist.api.model.DeleteTrackRequest;
import playlist.model.Playlist;
import playlist.service.PageCounterStatisticsService;
import playlist.service.PlaylistService;

@Api(value = "/playlists", description = "Endpoint for playlists listing")
@Component
@Path("/playlists/{username}")
@Produces(MediaType.APPLICATION_JSON)
public class PlaylistEndpoint {

	@Autowired
	private PlaylistService service;

	@Context
	private UriInfo uriInfo;
	
	@Autowired
	private PageCounterStatisticsService statistics;

	@GET
	@ApiOperation(value = "Lists user playlist names", notes = "Lists user playlist names", responseContainer = "array", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful reading user playlists name"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response readPlaylist(@PathParam("username") String username) {
		statistics.incrementCounter("view_all_playlists");
		
		List<String> playlistNames = service.getUsersPlaylistNames(username);
		return Response.ok(playlistNames).build();
	}

	@POST
	@Path("/{playlistName}")
	@ApiOperation(value = "Creates an new playlist", notes = "Creates an new playlist")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful saving of the playlist"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response createPlaylist(@PathParam("username") String username,
			@PathParam("playlistName") String playlistName) {
		Playlist playlist = service.createPlaylist(username, playlistName);
		URI location = uriInfo.getRequestUri();
		return Response.created(location).entity(playlist).build();
	}

	@DELETE
	@Path("/{playlistName}")
	@ApiOperation(value = "Deletes a playlist", notes = "Deletes playlist")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful deleting of the playlist"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response deletePlaylist(@PathParam("username") String username,
			@PathParam("playlistName") String playlistName) {
		Playlist playlist = service.deletePlaylist(username, playlistName);
		URI location = uriInfo.getBaseUriBuilder().path("/playlists/{username}").resolveTemplate("username", username)
				.build();
		return Response.accepted(playlist).location(location).build();
	}

	@GET
	@Path("/{playlistName}")
	@ApiOperation(value = "Returns a playlist", notes = "Returns playlist", response = Playlist.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful return of the playlist", response = Playlist.class),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response getPlaylist(@PathParam("username") String username,
			@PathParam("playlistName") String playlistName) {
		statistics.incrementCounter("view_playlist");
		
		Playlist playlist = service.findByUserAndPlaylistName(username, playlistName);
		return Response.ok(playlist).build();
	}

	@POST
	@Path("/{playlistName}/tracks")
	@ApiOperation(value = "Adds a track to the playlist", notes = "Adds a  track to the playlist")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfuladding of the track"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response addTrack(@PathParam("username") String username, @PathParam("playlistName") String playlistName,
			AddTrackRequest addTrackRequest) {
		Playlist playlist = service.addTrack(addTrackRequest.getTrackId(), username, playlistName);
		
		URI location = uriInfo.getBaseUriBuilder().path("/playlists/{username}/{playlistName}")
				.resolveTemplate("username", username).resolveTemplate("playlistName", playlistName).build();
		return Response.accepted(playlist).location(location).build();
	}

	@DELETE
	@Path("/{playlistName}/tracks")
	@ApiOperation(value = "Removes a track from the playlist", notes = "Removes a track from the playlist given track seequence in playlist")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful adding of the track"),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response deleteTrack(@PathParam("username") String username, @PathParam("playlistName") String playlistName,
			DeleteTrackRequest deleteTrackRequest) {
		Playlist playlist = service.deleteTrack(deleteTrackRequest.getSeequenceNumber(), username, playlistName);
		
		URI location = uriInfo.getBaseUriBuilder().path("/playlists/{username}/{playlistName}")
				.resolveTemplate("username", username).resolveTemplate("playlistName", playlistName).build();
		return Response.accepted(playlist).location(location).build();
	}

}
