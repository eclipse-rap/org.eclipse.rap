/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.browser.browserkit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.IInitialization;
import org.eclipse.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.rwt.internal.resources.DefaultResourceManagerFactory;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class BrowserLCA_Test extends TestCase {
  
  public void testUrl() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display );
    Browser browser = new Browser( shell, SWT.NONE );
    
    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    String expected = String.valueOf( BrowserLCA.BLANK_HTML.hashCode() );
    assertTrue( BrowserLCA.getUrl( browser ).indexOf( expected ) != -1 );
    
    RWTFixture.markInitialized( browser );
    RWTFixture.preserveWidgets();
    assertFalse( BrowserLCA.hasUrlChanged( browser ) );
    
    browser = new Browser( shell, SWT.NONE );
    browser.setText( "Hello" );
    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    expected = String.valueOf( "Hello".hashCode() );
    assertTrue( BrowserLCA.getUrl( browser ).indexOf( expected ) != -1 );
    
    RWTFixture.markInitialized( browser );
    RWTFixture.preserveWidgets();
    browser.setText( "GoodBye" );
    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    expected = String.valueOf( "GoodBye".hashCode() );
    assertTrue( BrowserLCA.getUrl( browser ).indexOf( expected ) != -1 );

    browser = new Browser( shell, SWT.NONE );
    browser.setUrl( "http://eclipse.org/rap" );
    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    assertEquals( "http://eclipse.org/rap", BrowserLCA.getUrl( browser ) );

    RWTFixture.markInitialized( browser );
    RWTFixture.preserveWidgets();
    browser.setUrl( "http://eclipse.org/rip" );
    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    assertEquals( "http://eclipse.org/rip", BrowserLCA.getUrl( browser ) );
    
    browser = new Browser( shell, SWT.NONE );
    browser.setText( "" );
    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    expected = String.valueOf( BrowserLCA.BLANK_HTML.hashCode() );
    assertTrue( BrowserLCA.getUrl( browser ).indexOf( expected ) != -1 );
  }
  
  protected void setUp() throws Exception {
    // we need the resource manager for this test
    System.setProperty( IInitialization.PARAM_LIFE_CYCLE, 
                        RWTLifeCycle.class.getName() );
    Fixture.setUp();
    RWTFixture.registerAdapterFactories();
    Fixture.createContext( false );
    // registration of real resource manager
    ResourceManager.register( new DefaultResourceManagerFactory() );
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
