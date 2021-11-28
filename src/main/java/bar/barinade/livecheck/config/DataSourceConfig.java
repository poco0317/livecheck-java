package bar.barinade.livecheck.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DataSourceConfig {
	
	@Value("${livecheck.db.driverclassname}")
	private String driverClassName;
	
	@Value("${livecheck.db.url}")
	private String url;
	
	@Value("${livecheck.db.user}")
	private String user;
	
	@Value("${livecheck.db.password}")
	private String password;

	@Bean
	public DataSource dataSource() {
	    final DriverManagerDataSource dataSource = new DriverManagerDataSource();
	    dataSource.setDriverClassName(driverClassName);
	    dataSource.setUrl(url);
	    dataSource.setUsername(user);
	    dataSource.setPassword(password);
	    return dataSource;
	}

}
