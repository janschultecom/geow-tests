/*******************************************************************************
 * Copyright 2013 Jan Schulte
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.geow.key.tests;

import static org.geow.testkit.GeoTesting.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geow.key.KeyGenerator;
import org.geow.key.KeyGenerator.PRECISION;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KeyGeneratorTest {
	private static Logger logger = LogManager.getLogger(KeyGeneratorTest.class
			.getName());

	private KeyGenerator keyGenLow;
	private KeyGenerator keyGenMedium;
	private KeyGenerator keyGenHigh;
	private KeyGenerator keyGenUltra;

	@Before
	public void setUp() throws Exception {
		keyGenLow = new KeyGenerator(KeyGenerator.PRECISION.LOW_20KM);
		keyGenMedium = new KeyGenerator(KeyGenerator.PRECISION.MEDIUM_5KM);
		keyGenHigh = new KeyGenerator(KeyGenerator.PRECISION.VERY_HIGH_1M);
		keyGenUltra = new KeyGenerator(KeyGenerator.PRECISION.ULTRA_1CM);
	}

	@Test
	public void testSequentialEncode13() {
		for (int i = 0; i < 10000; i++) {
			Double longitude = createLon();
			Double latitude = createLat();
			Long hash = keyGenMedium.encodeSequential(longitude, latitude);
			double[] decoded = keyGenMedium.decodeSequential(hash);
			assertEquals(longitude, decoded[0],
					keyGenMedium.getLongitudeFailure());
			assertEquals(latitude, decoded[1],
					keyGenMedium.getLatitudeFailure());
		}
	}

	@Test
	public void testSequentialEncode30() {
		for (int i = 0; i < 10000; i++) {
			Double longitude = createLon();
			Double latitude = createLat();
			Long hash = keyGenHigh.encodeSequential(longitude, latitude);
			double[] decoded = keyGenHigh.decodeSequential(hash);
			assertEquals(longitude, decoded[0],
					keyGenHigh.getLongitudeFailure());
			assertEquals(latitude, decoded[1], keyGenHigh.getLatitudeFailure());
		}
	}

	@Test
	public void testParallelEncode13() {
		for (int i = 0; i < 10000; i++) {
			Double longitude = createLon();
			Double latitude = createLat();
			Long hash = keyGenMedium.encodeParallel(longitude, latitude);
			double[] decoded = keyGenMedium.decodeParallel(hash);
			assertEquals(longitude, decoded[0],
					keyGenMedium.getLongitudeFailure());
			assertEquals(latitude, decoded[1],
					keyGenMedium.getLatitudeFailure());
		}
	}

	@Test
	public void testParallelEncode30() {
		for (int i = 0; i < 10000; i++) {
			Double longitude = createLon();
			Double latitude = createLat();
			Long hash = keyGenHigh.encodeParallel(longitude, latitude);
			double[] decoded = keyGenHigh.decodeParallel(hash);
			assertEquals(longitude, decoded[0],
					keyGenHigh.getLongitudeFailure());
			assertEquals(latitude, decoded[1], keyGenHigh.getLatitudeFailure());
		}
	}

	@Test
	public void testHashSplitting13() {
		for (int i = 0; i < 10000; i++) {
			Double longitude = createLon();
			Double latitude = createLat();
			Long hash = keyGenMedium.encodeParallel(longitude, latitude);
			long[] splitHash = keyGenMedium.splitHash(hash);
			Long joinedHash = keyGenMedium.joinHash(splitHash[0], splitHash[1]);
			assertEquals(hash, joinedHash);
		}
	}

	@Test
	public void testHashSplitting30() {
		for (int i = 0; i < 10000; i++) {
			Double longitude = createLon();
			Double latitude = createLat();
			Long hash = keyGenHigh.encodeParallel(longitude, latitude);
			long[] splitHash = keyGenHigh.splitHash(hash);
			Long joinedHash = keyGenHigh.joinHash(splitHash[0], splitHash[1]);
			assertEquals(hash, joinedHash);
		}
	}

	@Test
	public void testGetBoundingBox30() {
		Double longitude = createLon();
		Double latitude = createLat();
		Long hash = keyGenMedium.encodeParallel(longitude, latitude);
		Double[] boundingBox = keyGenMedium.getBoundingBox(hash);

		for (int i = 0; i < 10000; i++) {
			logger.trace("Orig[{},{}]", longitude, latitude);
			logger.trace("Diff[{},{}]", boundingBox[0], boundingBox[1]);
			Double lon = (longitude - boundingBox[0])
					+ (Math.random() * (boundingBox[0] * 2));
			Double lat = (latitude - boundingBox[1])
					+ (Math.random() * (boundingBox[1] * 2));

			assertTrue(lon >= longitude - boundingBox[0]);
			assertTrue(lon <= longitude + boundingBox[0]);
			assertTrue(lat >= latitude - boundingBox[1]);
			assertTrue(lat <= latitude + boundingBox[1]);

			Long hash2 = keyGenMedium.encodeParallel(lon, lat);
			Long[][] neighbours = keyGenMedium.getNeighbourHashes(hash);

			logger.trace("New[{},{}]", lon, lat);

			boolean inNeighbours = false;
			for (int j = 0; j < neighbours.length; j++) {
				for (int k = 0; k < neighbours[j].length; k++) {
					if (neighbours[j][k].equals(hash2)) {
						inNeighbours = true;
						logger.trace("In neighbour [{},{}]", j, k);
					}
				}
			}

			assertTrue("Hashes do not match: hash1[" + hash + "] hash2["
					+ hash2 + "]\nOriginal: [" + longitude + "," + latitude
					+ "]\nNew: [" + lon + "," + lat + "]", inNeighbours);
		}
	}

	@Test
	public void testReduceParallelHash() {
		for (int i = 0; i < 10000; i++) {
			Double longitude = createLon();
			Double latitude = createLat();

			long low = keyGenLow.encodeParallel(longitude, latitude);
			long medium = keyGenMedium.encodeParallel(longitude, latitude);
			long high = keyGenHigh.encodeParallel(longitude, latitude);
			long ultra = keyGenUltra.encodeParallel(longitude, latitude);

			long reducedUltraToLow = keyGenUltra.reducePrecisionParallel(ultra,
					PRECISION.LOW_20KM);
			long reducedUltraToMedium = keyGenUltra.reducePrecisionParallel(
					ultra, PRECISION.MEDIUM_5KM);
			long reducedUltraToHigh = keyGenUltra.reducePrecisionParallel(
					ultra, PRECISION.VERY_HIGH_1M);
			logger.trace("{} - Ultra", Long.toBinaryString(ultra));
			logger.trace("{} - Ultra reduced to low",
					Long.toBinaryString(reducedUltraToLow));
			logger.trace("{} - Low", Long.toBinaryString(low));
			logger.trace("{} - Ultra reduced to medium",
					Long.toBinaryString(reducedUltraToMedium));
			logger.trace("{} - Medium", Long.toBinaryString(medium));
			logger.trace("{} - Ultra reduced to high",
					Long.toBinaryString(reducedUltraToHigh));
			logger.trace("{} - High", Long.toBinaryString(high));

			assertEquals(reducedUltraToLow + " does not match " + low,
					reducedUltraToLow, low);
			assertEquals(reducedUltraToMedium + " does not match " + medium,
					reducedUltraToMedium, medium);
			assertEquals(reducedUltraToHigh + " does not match " + high,
					reducedUltraToHigh, high);

			long reducedHighToLow = keyGenUltra.reducePrecisionParallel(high,
					PRECISION.LOW_20KM);
			long reducedHighToMedium = keyGenUltra.reducePrecisionParallel(
					high, PRECISION.MEDIUM_5KM);

			assertEquals(reducedHighToLow + " does not match " + low,
					reducedHighToLow, low);
			assertEquals(reducedHighToMedium + " does not match " + medium,
					reducedHighToMedium, medium);

			long reducedMediumToLow = keyGenUltra.reducePrecisionParallel(
					medium, PRECISION.LOW_20KM);

			assertEquals(reducedMediumToLow + " does not match " + low,
					reducedMediumToLow, low);

		}

	}

	@Test
	public void testGetGeoRangeLow() {

		for (int i = 0; i < 10000; i++) {
			Double longitude = createLon();
			Double latitude = createLat();

			long low = keyGenLow.encodeParallel(longitude, latitude);
			long high = keyGenHigh.encodeParallel(longitude, latitude);
			long ultra = keyGenUltra.encodeParallel(longitude, latitude);

			logger.trace("{} low hash", Long.toBinaryString(low));

			long maxLowNumber = keyGenLow.calculateMaxNumber(keyGenLow
					.getNumberOfBits(PRECISION.LOW_20KM));
			logger.trace("{} maxLowNumber", Long.toBinaryString(maxLowNumber));

			Long[] lowRange = keyGenLow.getGeoIdRange(low);
			long minLowNumberBoundingBox = keyGenLow.reducePrecisionParallel(
					lowRange[0], PRECISION.LOW_20KM);
			long maxLowNumberBoundingBox = keyGenLow.reducePrecisionParallel(
					lowRange[1], PRECISION.LOW_20KM);

			logger.trace("{} lowRange[0]", Long.toBinaryString(lowRange[0]));
			logger.trace("{} lowRange[1]", Long.toBinaryString(lowRange[1]));

			assertEquals(
					"Min low number does not have the correct bounding box",
					low, minLowNumberBoundingBox);
			assertEquals(
					"Max low number does not have the correct bounding box",
					low, maxLowNumberBoundingBox);
		}
	}

	@Test
	public void testGetGeoRangeMedium() {
		for (int i = 0; i < 10000; i++) {
			Double longitude = createLon();
			Double latitude = createLat();
			long medium = keyGenMedium.encodeParallel(longitude, latitude);
			long maxMediumNumber = keyGenMedium.calculateMaxNumber(keyGenMedium
					.getNumberOfBits(PRECISION.MEDIUM_5KM));
			logger.trace("{} maxMediumNumber",
					Long.toBinaryString(maxMediumNumber));

			Long[] mediumRange = keyGenMedium.getGeoIdRange(medium);
			long minMediumNumberBoundingBox = keyGenMedium
					.reducePrecisionParallel(mediumRange[0],
							PRECISION.MEDIUM_5KM);
			long maxMediumNumberBoundingBox = keyGenMedium
					.reducePrecisionParallel(mediumRange[1],
							PRECISION.MEDIUM_5KM);

			logger.trace("{} mediumRange[0]",
					Long.toBinaryString(mediumRange[0]));
			logger.trace("{} mediumRange[1]",
					Long.toBinaryString(mediumRange[1]));

			assertEquals(
					"Min medium number does not have the correct bounding box",
					medium, minMediumNumberBoundingBox);
			assertEquals(
					"Max medium number does not have the correct bounding box",
					medium, maxMediumNumberBoundingBox);
		}
	}

	@Test
	public void testGetGeoRangeHigh() {
		for (int i = 0; i < 10000; i++) {
			Double longitude = createLon();
			Double latitude = createLat();
			long high = keyGenHigh.encodeParallel(longitude, latitude);
			long maxHighNumber = keyGenHigh.calculateMaxNumber(keyGenHigh
					.getNumberOfBits(PRECISION.VERY_HIGH_1M));
			logger.trace("{} maxHighNumber", Long.toBinaryString(maxHighNumber));

			Long[] highRange = keyGenHigh.getGeoIdRange(high);
			long minHighNumberBoundingBox = keyGenHigh.reducePrecisionParallel(
					highRange[0], PRECISION.VERY_HIGH_1M);
			long maxHighNumberBoundingBox = keyGenHigh.reducePrecisionParallel(
					highRange[1], PRECISION.VERY_HIGH_1M);

			logger.trace("{} highRange[0]", Long.toBinaryString(highRange[0]));
			logger.trace("{} highRange[1]", Long.toBinaryString(highRange[1]));

			assertEquals(
					"Min high number does not have the correct bounding box",
					high, minHighNumberBoundingBox);
			assertEquals(
					"Max high number does not have the correct bounding box",
					high, maxHighNumberBoundingBox);

			long hash1 = keyGenHigh.reducePrecisionParallel(highRange[0],
					PRECISION.VERY_HIGH_1M);
			long hash2 = keyGenHigh.reducePrecisionParallel(highRange[1],
					PRECISION.VERY_HIGH_1M);
			assertEquals("Bounding boxes of min and max range do not match",
					hash1, hash2);
		}
	}
	

	@Test
	public void testGetEncapsulatingRectangle() {
		
		for(int lonBB = -180; lonBB < 180; lonBB += 10){
			for(int latBB = -90; latBB < 90; latBB += 10){	
				
				double minLon = lonBB + 10, maxLon = lonBB - 10, minLat = latBB + 10, maxLat = latBB - 10;
				
				boolean init = true;
				
				List<Long> hashes = new ArrayList<Long>();
				
				for(int i=0;i<100;i++){
					
					double[] point = createLonLatInRange(lonBB, lonBB + 10, latBB, latBB + 10);
					
					Long hash = keyGenUltra.encodeParallel(point);
					hashes.add(hash);
					
					double lon = point[0];
					double lat = point[1];
	
					if(init){
						minLon = lon;
						maxLon = lon;
						minLat = lat;
						maxLat = lat;
						init = false;
					}
					
					if(lon < minLon){
						minLon = lon;
					}else if(lon > maxLon){
						maxLon = lon;
					}
					
					if(lat < minLat){
						minLat = lat;
					}else if(lat > maxLat){
						maxLat = lat;
					}
					
				}
				
				double[] upperLeft = new double[] { minLon,maxLat};
				double[] lowerRight = new double[] {maxLon, minLat};
				
				long upperLeftHash = keyGenUltra.encodeParallel(upperLeft);
				long lowerRightHash = keyGenUltra.encodeParallel(lowerRight);
				
				long[] encapsulation = keyGenUltra.getEncapsulatingRectangle(hashes);
				
				assertEquals("Upper left mismatch", upperLeftHash,encapsulation[0]);
				assertEquals("Lower right mismatch", lowerRightHash,encapsulation[1]);
				
			}
		}
	}
	
	@Test
	public void testGetEncapsulatingRectangleBoundingBoxes() {
		
		for(int lonBB = -180; lonBB < 180; lonBB += 10){
			for(int latBB = -90; latBB < 90; latBB += 10){	
				
				double minLon = lonBB + 10, maxLon = lonBB - 10, minLat = latBB + 10, maxLat = latBB - 10;
				
				boolean init = true;
				
				List<Long> hashes = new ArrayList<Long>();
				
				for(int i=0;i<100;i++){
					
					double[] point = createLonLatInRange(lonBB, lonBB + 10, latBB, latBB + 10);
					
					Long hash = keyGenLow.encodeParallel(point);
					hashes.add(hash);
					
					double lon = point[0];
					double lat = point[1];
	
					if(init){
						minLon = lon;
						maxLon = lon;
						minLat = lat;
						maxLat = lat;
						init = false;
					}
					
					if(lon < minLon){
						minLon = lon;
					}else if(lon > maxLon){
						maxLon = lon;
					}
					
					if(lat < minLat){
						minLat = lat;
					}else if(lat > maxLat){
						maxLat = lat;
					}
					
				}
				
				double[] upperLeft = new double[] { minLon,maxLat};
				double[] lowerRight = new double[] {maxLon, minLat};
				
				long upperLeftHash = keyGenLow.encodeParallel(upperLeft);
				long lowerRightHash = keyGenLow.encodeParallel(lowerRight);
				
				double[] upperLeftLonLat = keyGenLow.decodeParallel(upperLeftHash);
				double upperLeftLon = upperLeftLonLat[0];
				double upperLeftLat = upperLeftLonLat[1];
				

				double[] lowerRightLonLat = keyGenLow.decodeParallel(lowerRightHash);
				double lowerRightLon = lowerRightLonLat[0];
				double lowerRightLat = lowerRightLonLat[1];

				long[] rectangle = keyGenLow.getEncapsulatingRectangle(hashes); 
				long[][] encapsulation = keyGenLow.getEncapsulatingRectangleBoundingBoxes(rectangle);
				for(long[] row : encapsulation){
					for(long column : row){
						double[] boundingBoxLonLat = keyGenLow.decodeParallel(column);
						double boundingBoxLon = boundingBoxLonLat[0];
						double boundingBoxLat = boundingBoxLonLat[1];
						assertTrue("Bounding lon smaller than min lon. Expected >= "+upperLeftLon+" Actual: "+boundingBoxLon, boundingBoxLon >= upperLeftLon);
						assertTrue("Bounding lon greater than max lon. Expected <= "+lowerRightLon+" Actual: "+boundingBoxLon, boundingBoxLon <= lowerRightLon);
						assertTrue("Bounding lat smaller than min lat. Expected >= "+lowerRightLat+" Actual: "+boundingBoxLat, boundingBoxLat >= lowerRightLat);
						assertTrue("Bounding lat greater than max lat. Expected <= "+upperLeftLat+" Actual: "+boundingBoxLat, boundingBoxLat <= upperLeftLat);
					}
				}
			}
		}
	}
	

	@Test
	public void testGetEncapsulatingRectangleBoundingBoxesIdentity() {
		double[] point = createLonLat();
		
		long hash = keyGenUltra.encodeParallel(point);
			
		long[] rectangle = {hash,hash};
		
		long[][] encapsulation = keyGenUltra.getEncapsulatingRectangleBoundingBoxes(rectangle);
	
		assertThat(encapsulation.length, equalTo(1));
		assertThat(encapsulation[0].length, equalTo(1));
		assertThat(encapsulation[0][0], equalTo(hash));
	}
	
}
