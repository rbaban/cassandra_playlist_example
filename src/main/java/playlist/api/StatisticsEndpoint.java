package playlist.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import playlist.model.PageCounterStatistics;
import playlist.service.PageCounterStatisticsService;

@Api(value = "/statistics", description = "Endpoint for page counter statistics listing")
@Component
@Path("/statistics")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StatisticsEndpoint {

	@Autowired
	private PageCounterStatisticsService service;

	@GET
	@ApiOperation(value = "Returns page counters", notes = "Returns page counters.", responseContainer = "array", response = PageCounterStatistics.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful retrieval of page counters", response = PageCounterStatistics.class),
			@ApiResponse(code = 500, message = "Internal server error") })
	public List<PageCounterStatistics> getStatistics() {
		return service.getStatistics();
	}
}
