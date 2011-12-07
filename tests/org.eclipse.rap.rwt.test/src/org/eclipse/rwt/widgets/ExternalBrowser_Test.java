/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.widgets;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.widgets.Display;


public class ExternalBrowser_Test extends TestCase {

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testOpen() {
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

  /* (intentionally non-JavaDoc'ed)
   * Ensure that the order in which the protocol messages are rendered
   * matches the order of the ExternalBrowser#open/close calls
   */
  public void testExecutionOrder() throws IOException {
    RWTFactory.getEntryPointManager().register( EntryPointManager.DEFAULT,
                                                TestExecutionOrderEntryPoint.class );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, "w1" );
    // run life cycle
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    lifeCycle.execute();
    // assert conditions
    String markup = Fixture.getAllMarkup();
    int open1Index = markup.indexOf( createOpenMarkup( "1" ) );
    int close1Index = markup.indexOf( createCloseMarkup( "1" ) );
    int open2Index = markup.indexOf( createOpenMarkup( "2" ) );
    int close2Index = markup.indexOf( createCloseMarkup( "2" ) );
    assertTrue( open1Index != -1 && close1Index != -1 );
    assertTrue( open2Index != -1 && close2Index != -1 );
    assertTrue( open1Index < close1Index );
    assertTrue( open2Index < close2Index );
    assertTrue( open1Index < open2Index );
  }

  private static String createOpenMarkup( String id ) {
    StringBuilder builder = new StringBuilder();
    builder.append( "\"target\": \"eb\",\n" );
    builder.append( "\"action\": \"call\",\n" );
    builder.append( "\"method\": \"open\",\n" );
    builder.append( "\"properties\": {\n" );
    builder.append( "\"id\": \"" );
    builder.append( id );
    builder.append( "\",\n" );
    builder.append( "\"url\": \"http://eclipse.org\",\n" );
    builder.append( "\"style\": [ \"STATUS\", \"LOCATION_BAR\" ]" );
    return builder.toString();
  }

  private static String createCloseMarkup( String id ) {
    StringBuilder builder = new StringBuilder();
    builder.append( "\"target\": \"eb\",\n" );
    builder.append( "\"action\": \"call\",\n" );
    builder.append( "\"method\": \"close\",\n" );
    builder.append( "\"properties\": {\n" );
    builder.append( "\"id\": \"" );
    builder.append( id );
    builder.append( "\"" );
    return builder.toString();
  }

  public static final class TestExecutionOrderEntryPoint implements IEntryPoint {

    public int createUI() {
      new Display();
      // execute a row open/close method calls
      ExternalBrowser.open( "1",
                            "http://eclipse.org",
                            ExternalBrowser.STATUS | ExternalBrowser.LOCATION_BAR );
      ExternalBrowser.close( "1" );
      ExternalBrowser.open( "2",
                            "http://eclipse.org",
                            ExternalBrowser.STATUS | ExternalBrowser.LOCATION_BAR );
      ExternalBrowser.close( "2" );
      return 0;
    }
  }
}
