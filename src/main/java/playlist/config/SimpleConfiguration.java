package playlist.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cassandra.config.java.AbstractCqlTemplateConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import com.datastax.driver.core.Session;

@Configuration
@EnableAutoConfiguration
class SimpleConfiguration {

	@Configuration
	@EnableCassandraRepositories
	@PropertySource(value = { "classpath:application.properties" })
	static class CassandraConfig extends AbstractCqlTemplateConfiguration {

	    @Autowired
	    private Environment environment;
		
		@Override
		public String getKeyspaceName() {
			return environment.getProperty("cassandra.keyspace");
		}
		
		@Bean
		public CassandraTemplate cassandraTemplate(Session session) {
			return new CassandraTemplate(session);
		}
	}
}
