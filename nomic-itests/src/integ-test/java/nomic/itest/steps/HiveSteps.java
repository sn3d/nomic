package nomic.itest.steps;

import net.thucydides.core.annotations.Step;
import nomic.core.NomicConfig;
import nomic.app.config.TypesafeConfig;
import nomic.hive.InvalidHiveQueryException;
import nomic.hive.adapter.JdbcHiveAdapter;
import nomic.itest.TestSession;


import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author zdenko.vrabel@wirecard.com
 */
public class HiveSteps {

	@Step
	public void checkIfTableExist(String table) {
		boolean res = tableExist(table);
		assertThat(res).isTrue();
	}

	@Step
	public void checkIfTableNotExist(String table) {
		boolean res = tableExist(table);
		assertThat(res).isFalse();
	}


	@Step
	public void checkIfSchemeExist(String scheme) {
		// connect to Hive
		NomicConfig config = TestSession.nomicConfig();
		JdbcHiveAdapter hive = new JdbcHiveAdapter(config.get("hive.jdbc.url"), config.get("hive.user"), config.get("hive.password"));
		hive.exec("SHOW TABLES IN NOMIC_TEST");
	}

	@Step
	public void dropTableIfExist(String table) {
		NomicConfig config = TypesafeConfig.Companion.loadDefaultConfiguration();
		dropTableIfExist(config.get("hive.schema"), table);
	}


	@Step
	public void dropTableIfExist(String schema, String table) {
		// connect to Hive
		NomicConfig config = TypesafeConfig.Companion.loadDefaultConfiguration();
		JdbcHiveAdapter hive = new JdbcHiveAdapter(config.get("hive.jdbc.url"), config.get("hive.user"), config.get("hive.password"));
		hive.exec("DROP TABLE " + schema + "." + table);
	}


	//-------------------------------------------------------------------------------------------------
	// private methods
	//-------------------------------------------------------------------------------------------------

	private boolean tableExist(String table) {
		NomicConfig config = TypesafeConfig.Companion.loadDefaultConfiguration();
		return tableExist(config.get("hive.schema"), table);
	}


	private boolean tableExist(String schema, String table) {
		try {
			// connect to Hive
			NomicConfig config = TypesafeConfig.Companion.loadDefaultConfiguration();
			JdbcHiveAdapter hive = new JdbcHiveAdapter(config.get("hive.jdbc.url"), config.get("hive.user"), config.get("hive.password"));

			// check the table existence
			hive.exec("SELECT * FROM " + schema + "." + table);
			return true;
		} catch (InvalidHiveQueryException e) {
			return false;
		}
	}

}
