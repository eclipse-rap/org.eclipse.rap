/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.widgets;


import junit.framework.TestCase;

import org.eclipse.rwt.IBrowserHistory;
import org.eclipse.rwt.RWT;
import org.eclipse.swt.RWTFixture;


public class BrowserHistory_Test extends TestCase {
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
  
  public void testCreateEntry() {
    IBrowserHistory history = RWT.getBrowserHistory();
    try {
      history.createEntry( null, "name" );
      fail( "BrowserHistory#createEntry must not allow id == null" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      history.createEntry( "", "name" );
      fail( "BrowserHistory#createEntry must not id to be an empty string" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      history.createEntry( "entry", null );
      fail( "BrowserHistory#createEntry must not allow null for title" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
  
  public void testAddBrowserHistoryListener() {
    final IBrowserHistory history = RWT.getBrowserHistory();
    try {
      history.addBrowserHistoryListener( null );
      fail( "BrowserHistory#addBrowserHistoryListener must not allow null" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testRemoveBrowserHistoryListener() {
    final IBrowserHistory history = RWT.getBrowserHistory();
    try {
      history.removeBrowserHistoryListener( null );
      fail( "BrowserHistory#removeBrowserHistoryListener must not allow null" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
}
