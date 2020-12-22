package me.ooi.tinyquery;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

/**
 * @author jun.zhao
 */
public class TinyQuerySetup {
	
	private static String configPath = "/tinyquery.properties";
	
	public void setup(DataSource dataSource) {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = TinyQuerySetup.class.getResourceAsStream(configPath);
			prop.load(input);
		} catch (IOException e) {
			throw new QueryBuildException(e);
		} finally {
			if( input != null ) {
				try {
					input.close();
				} catch (IOException e) {
				}
			}
		}
		
		Configuration configuration = new Configuration();
		configuration.setDataSource(dataSource);
		configuration.setDbtype(prop.getProperty("app.dbtype"));
		Map<String, Object> cfgMap = new LinkedHashMap<String, Object>();
		configuration.setProps(cfgMap);
		for (final String name: prop.stringPropertyNames()) {
			cfgMap.put(name, prop.getProperty(name));
		}
		configuration.init();
	}

}
