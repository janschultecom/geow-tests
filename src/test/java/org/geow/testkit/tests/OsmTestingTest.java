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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geow.model.impl.OsmTagImpl;
import org.geow.model.impl.OsmWayImpl;
import org.geow.testkit.OsmTesting;
import org.junit.Before;
import org.junit.Test;

import com.ecyrd.speed4j.StopWatch;

public class OsmTestingTest {
	private static Logger logger = LogManager.getLogger(OsmTestingTest.class
			.getName());

	private static final int MAX_TAGS = 100;
	private static final int MAX_NODES = 100;
	private static final int MAX_WAYS = 100;
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGenerateOsmNode() {
		StopWatch sw = new StopWatch();
		for(int i=0;i<MAX_NODES;i++){
			OsmTesting.generateOsmNode();
		}
		sw.stop();
		logger.debug("Generating tags took {} - per tag {}",sw,sw.toString(MAX_TAGS));
	}

	@Test
	public void testgenerateOsmVersion() {
		StopWatch sw = new StopWatch();
		for(int i=0;i<MAX_NODES;i++){
			OsmTesting.generateOsmVersion();
		}
		sw.stop();
		logger.debug("Generating version took {} - per version {}",sw,sw.toString(MAX_NODES));
	}

	@Test
	public void testGenerateOsmUser() {
		StopWatch sw = new StopWatch();
		for(int i=0;i<MAX_NODES;i++){
			OsmTesting.generateOsmUser();
		}
		sw.stop();
		logger.debug("Generating user took {} - per user {}",sw,sw.toString(MAX_NODES));
	}

	@Test
	public void testGenerateOsmUid() {
		StopWatch sw = new StopWatch();
		for(int i=0;i<MAX_NODES;i++){
			OsmTesting.generateOsmUid();
		}
		sw.stop();
		logger.debug("Generating uid took {} - per uid {}",sw,sw.toString(MAX_NODES));
	}

	@Test
	public void testGenerateOsmLon() {
		StopWatch sw = new StopWatch();
		for(int i=0;i<MAX_NODES;i++){
			OsmTesting.generateOsmLon();
		}
		sw.stop();
		logger.debug("Generating lon took {} - per lon {}",sw,sw.toString(MAX_NODES));
	}

	@Test
	public void testGenerateOsmLat() {
		StopWatch sw = new StopWatch();
		for(int i=0;i<MAX_NODES;i++){
			OsmTesting.generateOsmLat();
		}
		sw.stop();
		logger.debug("Generating lat took {} - per lat {}",sw,sw.toString(MAX_NODES));
	}

	@Test
	public void testGenerateOsmId() {
		StopWatch sw = new StopWatch();
		for(int i=0;i<MAX_NODES;i++){
			OsmTesting.generateOsmId();
		}
		sw.stop();
		logger.debug("Generating id took {} - per id {}",sw,sw.toString(MAX_NODES));
	}

	@Test
	public void testGenerateOsmChangeset() {
		StopWatch sw = new StopWatch();
		for(int i=0;i<MAX_NODES;i++){
			OsmTesting.generateOsmChangeset();
		}
		sw.stop();
		logger.debug("Generating changeset took {} - per changeset {}",sw,sw.toString(MAX_NODES));
	}

	@Test
	public void testGenerateXMLTimestamp() {
		StopWatch sw = new StopWatch();
		for(int i=0;i<MAX_NODES;i++){
			OsmTesting.generateXMLTimestamp();
		}
		sw.stop();
		logger.debug("Generating timestamp took {} - per timestamp {}",sw,sw.toString(MAX_NODES));	
	}
	
	@Test
	public void testGenerateOsmTag() {
		StopWatch sw = new StopWatch();
		for(int i=0;i<MAX_TAGS;i++){
			OsmTagImpl tag = OsmTesting.generateTag();
			logger.trace(tag);
		}
		sw.stop();
		logger.debug("Generating tags took {} - per way {}",sw,sw.toString(MAX_TAGS));
	}

	@Test
	public void testGenerateOsmWay(){

		StopWatch sw = new StopWatch();
		for(int i=0;i<MAX_WAYS;i++){
			OsmWayImpl way = OsmTesting.generateOsmWay();
			logger.trace(way);
		}
		sw.stop();
		logger.debug("Generating ways took {} - per way {}",sw,sw.toString(MAX_WAYS));
	}
	
	@Test
	public void testGenerateOsmWayWithTags(){

		StopWatch sw = new StopWatch();
		for(int i=0;i<MAX_WAYS;i++){
			OsmWayImpl way = OsmTesting.generateOsmWayWithTags();
			logger.trace(way);
		}
		sw.stop();
		logger.debug("Generating ways took {} - per way {}",sw,sw.toString(MAX_WAYS));
	}
	
	@Test
	public void testGenerateOsmWayWithTagsAndNds(){

		StopWatch sw = new StopWatch();
		for(int i=0;i<MAX_WAYS;i++){
			OsmWayImpl way = OsmTesting.generateOsmWayWithTagsAndNds();
			logger.trace(way);
		}
		sw.stop();
		logger.debug("Generating ways took {} - per way {}",sw,sw.toString(MAX_WAYS));
	}
	
	@Test
	public void testGenerateModifiedOsmWay(){

		StopWatch sw = new StopWatch();
		for(int i=0;i<MAX_WAYS;i++){
			OsmWayImpl original = OsmTesting.generateOsmWayWithTagsAndNds();
			logger.trace(original);
			
			OsmWayImpl revised = OsmTesting.generateModifiedOsmWay(original);
			logger.trace(revised);
			
		}
		sw.stop();
		logger.debug("Generating ways took {} - per way {}",sw,sw.toString(MAX_WAYS));
	}
}
