package nomic.itest.steps;

import net.thucydides.core.annotations.Step;
import nomic.NomicConfig;
import nomic.TypesafeNomicConfig;
import nomic.hdfs.HdfsAdapter;
import nomic.hdfs.plugin.HdfsPlugin;
import org.apache.commons.io.IOUtils;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author zdenko.vrabel@wirecard.com
 */
public class HdfsSteps {

	@Step
	public void fileOrDirExist(String path) {
		NomicConfig config = TypesafeNomicConfig.Companion.loadDefaultConfiguration();
		HdfsAdapter hdfs = HdfsPlugin.Companion.createAdapter(config);
		assertThat(hdfs.exist(path)).as("Check if the file or directory %s exist", path).isTrue();
	}


	@Step
	public void fileOrDirNotExist(String path) {
		NomicConfig config = TypesafeNomicConfig.Companion.loadDefaultConfiguration();
		HdfsAdapter hdfs = HdfsPlugin.Companion.createAdapter(config);
		assertThat(hdfs.exist(path)).as("Check if the file or directory %s exist", path).isFalse();
	}


	@Step
	public String loadFileAsText(String path) {
		NomicConfig config = TypesafeNomicConfig.Companion.loadDefaultConfiguration();
		HdfsAdapter hdfs = HdfsPlugin.Companion.createAdapter(config);
		try (
			InputStreamReader reader = new InputStreamReader(hdfs.open(path))
		) {
			StringWriter writer = new StringWriter();
			IOUtils.copy(reader, writer);
			return writer.toString();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

}
