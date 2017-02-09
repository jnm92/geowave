package mil.nga.giat.geowave.format.avro;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import mil.nga.giat.geowave.adapter.vector.avro.AvroSimpleFeatureCollection;
import mil.nga.giat.geowave.adapter.vector.ingest.DataSchemaOptionProvider;
import mil.nga.giat.geowave.core.index.ByteArrayId;
import mil.nga.giat.geowave.core.index.StringUtils;
import mil.nga.giat.geowave.core.ingest.GeoWaveData;
import mil.nga.giat.geowave.core.store.CloseableIterator;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.specific.SpecificDatumReader;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

public class AVROIngestTest
{
	private DataSchemaOptionProvider optionsProvider;
	private AvroIngestPlugin ingester;
	private AvroIngestPlugin ingesterExt;
	private String filePath;
	private int expectedCount;

	@Before
	public void setup() {
		optionsProvider = new DataSchemaOptionProvider();
		optionsProvider.setSupplementalFields(true);

		ingester = new AvroIngestPlugin(); //TODO update constructor??
		ingester.init(null);

		filePath = "20130401.export.CSV.zip"; //TODO is this the right data to use from AVRO?
		expectedCount = 14056;
	}

	@Test
	public void testIngest()
			throws IOException {

		final File toIngest = new File(
				this.getClass().getClassLoader().getResource(
						filePath).getPath());

		assertTrue(validate(toIngest));
		final Collection<ByteArrayId> indexIds = new ArrayList<ByteArrayId>();
		indexIds.add(new ByteArrayId(
				"123".getBytes(StringUtils.UTF8_CHAR_SET)));
		final CloseableIterator<GeoWaveData<SimpleFeature>> features = ingester.toGeoWaveData(
				toIngest,
				indexIds,
				"");

		assertTrue((features != null) && features.hasNext());

		int featureCount = 0;
		while (features.hasNext()) {
			final GeoWaveData<SimpleFeature> feature = features.next();

			if (isValidGDELTFeature(feature)) { //TODO replace with isValidAVRO
				featureCount++;
			}
		}
		features.close();

		final CloseableIterator<GeoWaveData<SimpleFeature>> featuresExt = ingesterExt.toGeoWaveData(
				toIngest,
				indexIds,
				"");

		assertTrue((featuresExt != null) && featuresExt.hasNext());


		final boolean readExpectedCount = (featureCount == expectedCount);
		if (!readExpectedCount) {
			System.out.println("Expected " + expectedCount + " features, ingested " + featureCount);
		}

		assertTrue(readExpectedCount);
	}
	
	/* Features avro should have:
	 * adapter
	 * primaryIndexIds
	 * simpleFeature
	 */

	private boolean isValidGDELTFeature( //TODO replace this function
			final GeoWaveData<SimpleFeature> feature ) {
		if ((feature.getValue().getAttribute(
				"adapter") == null) || (feature.getValue().getAttribute(
				"primaryIndexIds") == null) || (feature.getValue().getAttribute(
				"simpleFeature") == null)) {
			return false;
		}
		return true;
	}

	private boolean validate(File file){
		try {
			DataFileReader.openReader(
					file,
					new SpecificDatumReader<AvroSimpleFeatureCollection>()).close();
			return true;
		}
		catch (final IOException e) {
			// TODO log this??
			// just log as info as this may not have been intended to be read as
			// avro vector data
//			LOGGER.info(
//					"Unable to read file as Avro vector data '" + file.getName() + "'",
//					e);
		}

		return false;
	}
}
