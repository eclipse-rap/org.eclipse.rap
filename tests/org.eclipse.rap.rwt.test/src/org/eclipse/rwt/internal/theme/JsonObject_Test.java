/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;


public class JsonObject_Test extends TestCase {

  public void testToStringWhenEmpty() {
    JsonObject object = new JsonObject();
    
    assertEquals( "{}", object.toString() );
  }

  public void testAppendOnce() {
    JsonObject object = new JsonObject();

    object.append( "a", 23 );
    
    assertEquals( "{\n\"a\": 23\n}", object.toString() );
  }

  public void testAppendMultiple() {
    JsonObject object = new JsonObject();

    object.append( "a", 23 );
    object.append( "b", 3.14f );
    object.append( "c", "foo" );
    object.append( "d", true );
    object.append( "e", ( String )null );

    assertEquals( "{\n\"a\": 23,\n\"b\": 3.14,\n\"c\": \"foo\",\n\"d\": true,\n\"e\": null\n}",
                  object.toString() );
  }

  public void testAppendAfterToString() {
    JsonObject object = new JsonObject();
    
    object.append( "a", 23 );
    object.toString();
    object.append( "b", false );

    assertEquals( "{\n\"a\": 23,\n\"b\": false\n}", object.toString() );
  }

  public void testAppendArray() {
    JsonObject object = new JsonObject();
    
    object.append( "a", 23 );
    object.append( "b", new JsonArray() );
    object.append( "c", ( JsonArray )null );

    assertEquals( "{\n\"a\": 23,\n\"b\": [],\n\"c\": null\n}", object.toString() );
  }

  public void testAppendObject() {
    JsonObject object = new JsonObject();

    object.append( "a", 23 );
    object.append( "b", new JsonObject() );
    object.append( "c", ( JsonObject )null );

    assertEquals( "{\n\"a\": 23,\n\"b\": {},\n\"c\": null\n}", object.toString() );
  }
}
