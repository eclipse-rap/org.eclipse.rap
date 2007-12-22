/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.widgets;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.widgets.Display;


public class ExternalBrowser_Test extends TestCase {
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testOpen() {
    RWTFixture.fakeUIThread();
    new Display();
    // Test illegal arguments
    try {
      ExternalBrowser.open( null, "http://nowhere.org", 0 );
      fail( "ExternalBrowser#open must not allow id == null" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      ExternalBrowser.open( "", "http://nowhere.org", 0 );
      fail( "ExternalBrowser#open must not allow id == empty string" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      ExternalBrowser.open( "myId", null, 0 );
      fail( "ExternalBrowser#open must not allow url == null" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
  
  public void testClose() {
    RWTFixture.fakeUIThread();
    new Display();
    // Test illegal arguments
    try {
      ExternalBrowser.close( null );
      fail( "ExternalBrowser#close must not allow id == null" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      ExternalBrowser.close( "" );
      fail( "ExternalBrowser#close must not allow id == empty string" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
  
  public void testEscapeId() {
    String escapedId = ExternalBrowser.escapeId( "my.id" );
    assertEquals( -1, escapedId.indexOf( "." ) );
    
    String escapedId1 = ExternalBrowser.escapeId( "my_id" );
    String escapedId2 = ExternalBrowser.escapeId( "my.id" );
    assertFalse( escapedId1.equals( escapedId2 ) );
    
    escapedId1 = ExternalBrowser.escapeId( "my_id_0" );
    escapedId2 = ExternalBrowser.escapeId( "my.id_0" );
    assertFalse( escapedId1.equals( escapedId2 ) );
    
    escapedId1 = ExternalBrowser.escapeId( "1" );
    assertEquals( "1", escapedId1 );
    escapedId2 = ExternalBrowser.escapeId( "2" );
    assertEquals( "2", escapedId2 );
  }
  
  /* (intentionally non-JavaDoc'ed)
   * Ensure that the order in which the JavaScript commands are rendered
   * matches the order of the ExternalBrowser#open/close calls
   */
  public void testJavaScriptExecutionOrder() throws IOException {
    // set up test environment
    Display display = new Display();
    RWTFixture.fakeNewRequest();
    RWTFixture.fakeUIThread();
    String displayId = DisplayUtil.getId( display );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    // execute a row open/close method calls 
    ExternalBrowser.open( "1", "http://eclipse.org", 0 );
    ExternalBrowser.close( "1" );
    ExternalBrowser.open( "2", "http://eclipse.org", 0 );
    ExternalBrowser.close( "2" );
    // run life cycle
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.execute();
    // assert conditions
    String markup = Fixture.getAllMarkup();
    int open1Index = markup.indexOf( "ExternalBrowser.open( \"1" );
    int close1Index = markup.indexOf( "ExternalBrowser.close( \"1" );
    int open2Index = markup.indexOf( "ExternalBrowser.open( \"2" );
    int close2Index = markup.indexOf( "ExternalBrowser.close( \"2" );
    assertTrue( open1Index != -1 && close1Index != -1 );
    assertTrue( open2Index != -1 && close2Index != -1 );
    assertTrue( open1Index < close1Index );
    assertTrue( open2Index < close2Index );
    assertTrue( open1Index < open2Index );
  }
}
