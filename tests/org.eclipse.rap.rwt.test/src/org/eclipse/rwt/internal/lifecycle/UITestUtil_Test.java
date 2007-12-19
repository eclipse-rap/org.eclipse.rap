/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class UITestUtil_Test extends TestCase {
  
  public void testIsValidId() {
    // Test with legal id
    assertTrue( UITestUtil.isValidId( "customId" ) );
    // Test with illegal id's
    assertFalse( UITestUtil.isValidId( null ) );
    assertFalse( UITestUtil.isValidId( "" ) );
    assertFalse( UITestUtil.isValidId( "1" ) );
    assertFalse( UITestUtil.isValidId( "$A" ) );
    assertFalse( UITestUtil.isValidId( "A$" ) );
    assertFalse( UITestUtil.isValidId( "A&B" ) );
    assertFalse( UITestUtil.isValidId( "A/8" ) );
  }
  
  public void testWriteIds() throws IOException {
    System.setProperty( WidgetUtil.ENABLE_UI_TESTS, "true" );
    Display display = new Display();
    new Shell( display, SWT.NONE );
    String displayId = DisplayUtil.getId( display );
    // Request with not yet initialized widgets
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    lifeCycle.execute();
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "setHtmlId" ) != -1 );
    // Request with already initialized widgets
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    lifeCycle.execute();
    markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "setHtmlId" ) == -1 );
    // clean up
    System.getProperties().remove( WidgetUtil.ENABLE_UI_TESTS );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
