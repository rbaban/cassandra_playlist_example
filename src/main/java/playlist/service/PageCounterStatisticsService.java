package playlist.service;

import java.util.List;

import playlist.model.PageCounterStatistics;

public interface PageCounterStatisticsService {

	void incrementCounter(String counterName);
	
	void decrementCounter(String counterName);

	List<PageCounterStatistics> getStatistics();
}
