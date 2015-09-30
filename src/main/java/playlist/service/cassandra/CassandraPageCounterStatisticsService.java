package playlist.service.cassandra;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import playlist.model.PageCounterStatistics;
import playlist.service.PageCounterStatisticsService;

@Component
public class CassandraPageCounterStatisticsService implements PageCounterStatisticsService {

	@Autowired
	private CassandraOperations template;

	@Override
	public List<PageCounterStatistics> getStatistics() {

		String queryText = "SELECT * FROM statistics";
		ResultSet results = getSession().execute(queryText);

		List<PageCounterStatistics> statistics = new ArrayList<>();
		
		for (Row row : results) {
			PageCounterStatistics hitPageCounter = new PageCounterStatistics();
			hitPageCounter.setCounterName(row.getString("counter_name"));
			hitPageCounter.setCounterValue(row.getLong("counter_value"));
			statistics.add(hitPageCounter); 
		}

		return statistics;
	}

	@Override
	public void incrementCounter(String counterName) {
		String queryText = "UPDATE statistics set counter_value = counter_value + 1 where counter_name = ?";
		PreparedStatement preparedStatement = getSession().prepare(queryText);
		BoundStatement boundStatement = preparedStatement.bind(counterName);
		getSession().execute(boundStatement);

	}

	@Override
	public void decrementCounter(String counterName) {
		String queryText = "UPDATE statistics set counter_value = counter_value - 1 where counter_name = ?";
		PreparedStatement preparedStatement = getSession().prepare(queryText);
		BoundStatement boundStatement = preparedStatement.bind(counterName);
		getSession().execute(boundStatement);
	}

	protected Session getSession() {
		return template.getSession();
	}

}
