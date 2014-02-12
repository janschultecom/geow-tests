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
package org.geow.api.tests;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geow.model.IOsmElement;
import org.geow.model.IOsmNode;
import org.geow.model.IOsmRelation;
import org.geow.model.IOsmWay;
import org.geow.model.impl.OsmNodeImpl;
import org.geow.model.impl.OsmRelationImpl;
import org.geow.model.impl.OsmWayImpl;
import org.geow.model.util.OsmComparator;
import org.geow.model.util.OsmExternalizer;
import org.geow.testkit.GeneralTesting;
import org.geow.testkit.OsmTesting;
import org.junit.Before;
import org.junit.Test;

import com.ecyrd.speed4j.StopWatch;

public class OsmExternalizationTest {
	private static Logger logger = LogManager.getLogger(OsmExternalizationTest.class
			.getName());

	private OsmExternalizer externalizer;

	@Before
	public void setUp() throws Exception {
		externalizer = new OsmExternalizer();
	}

	@Test
	public void testExternalizingElement() throws IOException, ClassNotFoundException {
		int maxTests = 100000;
				
		List<IOsmElement> elements = new ArrayList<IOsmElement>();
		List<IOsmElement> deserializedElements = new ArrayList<IOsmElement>();
		
		StopWatch sw = new StopWatch();
		for (int j = 0; j <= maxTests; j++) {			
			elements.add(OsmTesting.generateOsmNodeWithTags());
		}
		sw.stop();
		logger.info("Generation of {} osm nodes took {}",maxTests,sw);


		sw.start();
		long sizes = 0L;
		for(int j=0; j <= maxTests; j++){
			IOsmElement node = elements.get(j);
			byte[] element = externalizer.writeExternalElement(node);
			sizes += element.length;
			deserializedElements.add(externalizer.readExternalElement(element));
		}
		sw.stop();
		logger.info("Average node size in bytes: {}",sizes/maxTests);
		logger.info("Serialization/deserialization of {} osm nodes took {}",maxTests,sw);

		sw = sw.start();
		for(int j=0; j <= maxTests; j++){
			IOsmNode node = (IOsmNode) elements.get(j);
			IOsmNode deserializedNode = (IOsmNode) deserializedElements.get(j);
			assertTrue(OsmComparator.areNodesDeepEqual(node, deserializedNode));
		}
		sw.stop();
		logger.info("Comparison of {} osm nodes took {}",maxTests,sw);

		elements.clear();
		deserializedElements.clear();
		
		sw.start();
		for (int j = 0; j <= maxTests; j++) {			
			elements.add(OsmTesting.generateOsmWayWithTagsAndNds());
		}
		sw.stop();
		logger.info("Generation of {} osm ways took {}",maxTests,sw);
		
		sw.start();
		sizes = 0L;
		for(int j=0; j <= maxTests; j++){
			IOsmElement way = elements.get(j);
			byte[] element = externalizer.writeExternalElement(way);
			sizes += element.length;
			deserializedElements.add(externalizer.readExternalElement(element));
		}
		sw.stop();
		logger.info("Average way size in bytes: {}",sizes/maxTests);
		logger.info("Serialization/deserialization of {} osm ways took {}",maxTests,sw);
		
		sw = sw.start();
		for(int j=0; j <= maxTests; j++){
			IOsmWay way = (IOsmWay) elements.get(j);
			IOsmWay deserializedWay = (IOsmWay) deserializedElements.get(j);
			assertTrue(OsmComparator.areWaysDeepEqual(way, deserializedWay));
		}
		sw.stop();
		logger.info("Comparison of {} osm ways took {}",maxTests,sw);

		elements.clear();
		deserializedElements.clear();
	
		sw.start();
		for (int j = 0; j <= maxTests; j++) {			
			elements.add(OsmTesting.generateOsmRelationWithTagsAndMembers());
		}
		sw.stop();
		logger.info("Generation of {} osm rels took {}",maxTests,sw);
		
		sw.start();
		sizes = 0L;
		for(int j=0; j <= maxTests; j++){
			IOsmElement rel = elements.get(j);
			byte[] element = externalizer.writeExternalElement(rel);
			sizes += element.length;
			deserializedElements.add(externalizer.readExternalElement(element));
		}
		sw.stop();
		logger.info("Average rel size in bytes: {}",sizes/maxTests);
		logger.info("Serialization/deserialization of {} osm rels took {}",maxTests,sw);
		
		sw = sw.start();
		for(int j=0; j <= maxTests; j++){
			IOsmRelation relation = (IOsmRelation) elements.get(j);
			IOsmRelation deserializedRelation = (IOsmRelation) deserializedElements.get(j);
			assertTrue(OsmComparator.areRelationsDeepEqual(relation, deserializedRelation));
		}
		sw.stop();
		logger.info("Comparison of {} osm rels took {}",maxTests,sw);
		
	}
	
	@Test
	public void testExternalizingNode() throws IOException, ClassNotFoundException {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		ByteArrayOutputStream baos = null;
		int maxTests = GeneralTesting.createRandomUpperLimit(1000);
		
		for (int j = 0; j <= maxTests; j++) {
			OsmNodeImpl node = OsmTesting.generateOsmNodeWithTags();

			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			externalizer.writeExternalNode(node, oos);

			oos.flush();

			ois = new ObjectInputStream(new ByteArrayInputStream(
					baos.toByteArray()));

			IOsmNode deserializedNode = externalizer.readExternalNode(ois);

			assertTrue(OsmComparator.areNodesEqual(node, deserializedNode));

			oos.close();
			ois.close();
			baos.close();

		}
	}
	
	
	@Test
	public void testExternalizingWay() throws IOException, ClassNotFoundException {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		ByteArrayOutputStream baos = null;
		int maxTests = GeneralTesting.createRandomUpperLimit(1000);
		
		for (int j = 0; j <= maxTests; j++) {
			OsmWayImpl node = OsmTesting.generateOsmWayWithTagsAndNds();

			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			externalizer.writeExternalWay(node, oos);

			oos.flush();

			ois = new ObjectInputStream(new ByteArrayInputStream(
					baos.toByteArray()));

			IOsmWay deserializedNode = externalizer.readExternalWay(ois);

			assertTrue(OsmComparator.areWaysEqual(node, deserializedNode));

			oos.close();
			ois.close();
			baos.close();

		}
	}
	
	@Test
	public void testExternalizingRelation() throws IOException, ClassNotFoundException {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		ByteArrayOutputStream baos = null;
		int maxTests = GeneralTesting.createRandomUpperLimit(1000);
		
		for (int j = 0; j <= maxTests; j++) {
			OsmRelationImpl relation = OsmTesting.generateOsmRelationWithTagsAndMembers();

			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			externalizer.writeExternalRelation(relation, oos);

			oos.flush();

			ois = new ObjectInputStream(new ByteArrayInputStream(
					baos.toByteArray()));

			IOsmRelation deserializedRelation = externalizer.readExternalRelation(ois);

			assertTrue(OsmComparator.areRelationsEqual(relation, deserializedRelation));

			oos.close();
			ois.close();
			baos.close();

		}
	}
}
