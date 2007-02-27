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

package org.eclipse.rap.rwt.internal.widgets.menukit;

import java.io.IOException;
import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.engine.PhaseListenerRegistry;
import org.eclipse.rap.rwt.internal.lifecycle.PreserveWidgetsPhaseListener;
import org.eclipse.rap.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.rap.rwt.lifecycle.DisplayUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.Fixture;
import com.w4t.engine.requests.RequestParams;
import com.w4t.util.browser.Ie6;


public class MenuLCA_Test extends TestCase {
  
  public void testUnassignedMenuBar() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    String shellId = WidgetUtil.getId( shell );
    Menu menuBar = new Menu( shell, RWT.BAR );
    
    // Ensure that a menuBar that is not assigned to any shell (via setMenuBar)
    // is rendered but without settings its parent
    Fixture.fakeResponseWriter();
    MenuLCA lca = new MenuLCA();
    RWTFixture.markInitialized( display );
    lca.renderInitialization( menuBar );
    lca.renderChanges( menuBar );
    assertTrue( Fixture.getAllMarkup().indexOf( "setParent" ) == -1 );
    
    // The contrary: an assigned menuBar has to be rendered with setParent
    Fixture.fakeResponseWriter();
    shell.setMenuBar( menuBar );
    lca.renderInitialization( menuBar );
    lca.renderChanges( menuBar );
    String expected = "setParent( wm.findWidgetById( \"" + shellId + "\" ) )";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    
    // Un-assigning a menuBar must result in setParent( null ) being rendered
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( menuBar );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    shell.setMenuBar( null );
    lca.renderInitialization( menuBar );
    lca.renderChanges( menuBar );
    expected = "setParent( null )";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }
  
  public void testWriteBoundsForMenuBar() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Menu menuBar = new Menu( shell, RWT.BAR );
    
    MenuLCA menuLCA = new MenuLCA();
    // initial unassigned rendering -> no setSpace
    Fixture.fakeResponseWriter();
    menuLCA.renderChanges( menuBar );
    assertTrue( Fixture.getAllMarkup().indexOf( "setSpace" ) == -1 );

    // initial assigned rendering -> no setSpace
    Fixture.fakeResponseWriter();
    shell.setMenuBar( menuBar );
    menuLCA.renderChanges( menuBar );
    assertTrue( Fixture.getAllMarkup().indexOf( "setSpace" ) != -1 );
    
    // 
    RWTFixture.markInitialized( shell );
    RWTFixture.markInitialized( menuBar );
    
    // changing bounds of shell -> an assigned menuBar must adjust its size
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    shell.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    menuLCA.renderChanges( menuBar );
    assertTrue( Fixture.getAllMarkup().indexOf( "setSpace" ) != -1 );

    // changing bounds of shell -> an unassigned menuBar does nothing
    Fixture.fakeResponseWriter();
    shell.setMenuBar( null );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    shell.setBounds( new Rectangle( 5, 6, 7, 8 ) );
    menuLCA.renderChanges( menuBar );
    assertTrue( Fixture.getAllMarkup().indexOf( "setSpace" ) == -1 );
    
    // Simulate client-side size-change of shell: menuBar must render new size
    RWTFixture.clearPreserved();
    PhaseListenerRegistry.add( new PreserveWidgetsPhaseListener() );
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    shell.setMenuBar( menuBar );
    String displayId = DisplayUtil.getId( display );
    String shellId = WidgetUtil.getId( shell );
    String menuId = WidgetUtil.getId( menuBar );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    lifeCycle.execute();
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( shellId + ".bounds.x", "0" );
    Fixture.fakeRequestParam( shellId + ".bounds.y", "0" );
    Fixture.fakeRequestParam( shellId + ".bounds.width", "1234" );
    Fixture.fakeRequestParam( shellId + ".bounds.height", "4321" );
    lifeCycle.execute();
    String expected = "wm.findWidgetById( \"" + menuId + "\" );w.setSpace";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.fakeResponseWriter();
    Fixture.fakeBrowser( new Ie6( true, true ) );
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
