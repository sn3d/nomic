package nomic.itest.steps;

import net.thucydides.core.annotations.Step;
import nomic.NomicConfig;
import nomic.TypesafeNomicConfig;
import nomic.hive.InvalidHiveQueryException;
import nomic.hive.plugin.HivePlugin;

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
		NomicConfig config = TypesafeNomicConfig.Companion.loadDefaultConfiguration();
		HivePlugin hive = HivePlugin.Companion.createPlugin(config);

		hive.exec("SHOW TABLES IN NOMIC_TEST");
	}

	@Step
	public void dropTableIfExist(String table) {
		NomicConfig config = TypesafeNomicConfig.Companion.loadDefaultConfiguration();
		dropTableIfExist(config.getHiveSchema(), table);
	}


	@Step
	public void dropTableIfExist(String schema, String table) {
		// connect to Hive
		NomicConfig config = TypesafeNomicConfig.Companion.loadDefaultConfiguration();
		HivePlugin hive = HivePlugin.Companion.createPlugin(config);
		hive.exec("DROP TABLE " + schema + "." + table);
	}


	//-------------------------------------------------------------------------------------------------
	// private methods
	//-------------------------------------------------------------------------------------------------

	private boolean tableExist(String table) {
		NomicConfig config = TypesafeNomicConfig.Companion.loadDefaultConfiguration();
		return tableExist(config.getHiveSchema(), table);
	}


	private boolean tableExist(String schema, String table) {
		try {
			// connect to Hive
			NomicConfig config = TypesafeNomicConfig.Companion.loadDefaultConfiguration();
			HivePlugin hive = new HivePlugin(config.getHiveJdbcUrl(), config.getHiveSchema(), config.getHiveUsername(), config.getHivePassword());

			// check the table existence
			hive.exec("SELECT * FROM " + schema + "." + table);
			return true;
		} catch (InvalidHiveQueryException e) {
			return false;
		}
	}

}
