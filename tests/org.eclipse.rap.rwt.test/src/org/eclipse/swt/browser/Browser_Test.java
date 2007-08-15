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

package org.eclipse.swt.browser;

import junit.framework.TestCase;

import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.IBrowserAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class Browser_Test extends TestCase {
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testInitialValues() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Browser browser = new Browser( shell, SWT.NONE );
    
    assertEquals( null, browser.getUrl() );
    assertEquals( null, getText( browser ) );
  }
  
  public void testUrlAndText() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Browser browser = new Browser( shell, SWT.NONE );
    
    browser.setUrl( "http://eclipse.org/rap" );
    assertEquals( null, getText( browser ) );
    browser.setText( "<html></head>..." );
    assertEquals( null, browser.getUrl() );
    
    try {
      browser.setUrl( "oldValue" );
      browser.setUrl( null );
      fail( "Browser#setUrl: null not allowed" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "oldValue", browser.getUrl() );
    }
    try {
      browser.setText( "oldValue" );
      browser.setText( null );
      fail( "Browser#setText: null not allowed" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "oldValue", getText( browser ) );
    }
  }
  
  public void testLocationEvent() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final StringBuffer log = new StringBuffer();
    final String[] expectedLocation = new String[ 1 ];
    Display display = new Display();
    Shell shell = new Shell( display );
    final Browser browser = new Browser( shell, SWT.NONE );
    LocationListener listener = new LocationListener() {
      public void changing( final LocationEvent event ) {
        log.append( "changing" + event.location + "|" );
        assertEquals( expectedLocation[ 0 ], event.location );
      }
      public void changed( final LocationEvent event ) {
        log.append( "changed" + event.location );
        assertSame( browser, event.getSource() );
        assertEquals( true, event.doit );
        assertEquals( expectedLocation[ 0 ], event.location );
      }
    };
    LocationListener vetoListener = new LocationListener() {
      public void changing( final LocationEvent event ) {
        log.append( "changing" + event.location + "|" );
        event.doit = false;
      }
      public void changed( final LocationEvent event ) {
        log.append( "changed" + event.location );
      }
    };

    // test basic event behaviour with setUrl
    browser.addLocationListener( listener );
    expectedLocation[ 0 ] = "NEW_URL";
    boolean success = browser.setUrl( expectedLocation[ 0 ] );
    assertEquals( true, success );
    assertEquals( "changingNEW_URL|changedNEW_URL", log.toString() );
    // setting the current url must also fire events
    log.setLength( 0 );
    success = browser.setUrl( expectedLocation[ 0 ] );
    assertEquals( true, success );
    assertEquals( "changingNEW_URL|changedNEW_URL", log.toString() );
    // clean up 
    log.setLength( 0 );
    browser.removeLocationListener( listener );
    
    // test vetoing listener with with setUrl
    browser.setUrl( "OLD_URL" );
    browser.addLocationListener( vetoListener );
    success = browser.setUrl( "NEW_URL" );
    assertEquals( false, success );
    assertEquals( "changingNEW_URL|", log.toString() );
    assertEquals( "OLD_URL", browser.getUrl() );
    // clean up 
    log.setLength( 0 );
    browser.removeLocationListener( vetoListener );
    
    // test basic event behaviour with setText
    browser.addLocationListener( listener );
    expectedLocation[ 0 ] = "about:blank";
    success = browser.setText( "Some html" );
    assertEquals( true, success );
    assertEquals( "changingabout:blank|changedabout:blank", log.toString() );
    // setting the current url must also fire events
    log.setLength( 0 );
    success = browser.setText( "Some html" );
    assertEquals( true, success );
    assertEquals( "changingabout:blank|changedabout:blank", log.toString() );
    // clean up 
    log.setLength( 0 );
    browser.removeLocationListener( listener );

    // test vetoing listener with with setText
    browser.setText( "Old html" );
    browser.addLocationListener( vetoListener );
    success = browser.setText( "New html" );
    assertEquals( false, success );
    assertEquals( "changingabout:blank|", log.toString() );
    assertEquals( "Old html", getText( browser ) );
    // clean up 
    log.setLength( 0 );
    browser.removeLocationListener( vetoListener );
  }

  private static String getText( final Browser browser ) {
    Object adapter = browser.getAdapter( IBrowserAdapter.class );
    IBrowserAdapter browserAdapter = ( IBrowserAdapter )adapter;
    return browserAdapter.getText();
  }
}
