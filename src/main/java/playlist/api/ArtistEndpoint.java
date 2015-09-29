package playlist.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import playlist.model.Artist;
import playlist.service.ArtistService;

@Api(value = "/artists", description = "Endpoint for artist listing")
@Component
@Path("/artists")
@Produces(MediaType.APPLICATION_JSON)
public class ArtistEndpoint {

	@Autowired
	private ArtistService service;

	@GET
	@Path("{firstLetter}")
	@ApiOperation(value = "Returns artists", notes = "Returns list of artists whose name start with given letter.", responseContainer = "array", response = Artist.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful retrieval of artists by first letter", response = Artist.class),
			@ApiResponse(code = 500, message = "Internal server error") })
	public Response findByFirstLetter(@PathParam("firstLetter") String firstLetter) {
		Iterable<Artist> artists = service.findArtistsByFirstLetter(firstLetter);
		return Response.ok(artists).build();
	}

}
