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
package org.geow.testkit.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geow.key.KeyGenerator;
import org.geow.key.KeyGenerator.PRECISION;
import org.geow.testkit.GeoTesting;
import org.junit.Before;
import org.junit.Test;

public class GeoTestingTest {

	private static final int numberTests = 10000;
	private KeyGenerator keyGenPoint;
	private KeyGenerator keyGenBB;

	@Before
	public void setup() {
		keyGenPoint = new KeyGenerator(PRECISION.ULTRA_1CM);
		keyGenBB = new KeyGenerator(PRECISION.MEDIUM_5KM);
	}

	@Test
	public void testCreateLonLatInBB() {
		int diffCount = 0;
		for (int i = 0; i < numberTests; i++) {
			Double lon = GeoTesting.createLon();
			Double lat = GeoTesting.createLat();
			Long element = keyGenPoint.encodeParallel(lon, lat);
			Long hash = keyGenPoint.reducePrecisionParallel(element,
					PRECISION.MEDIUM_5KM);
			double[] random = GeoTesting.createLonLatInBB(hash, keyGenBB,
					keyGenPoint);
			Long element2 = keyGenPoint.encodeParallel(random[0], random[1]);
			Long hash2 = keyGenPoint.reducePrecisionParallel(element2,
					PRECISION.MEDIUM_5KM);
			assertEquals("Hashes do not match", hash, hash2);
			
			if(element != element2){
				diffCount++;
			}
		}
		assertTrue("In most of the cases the elements should be different (diffCount was too small):"+diffCount,diffCount >= numberTests-50);
	}

}
