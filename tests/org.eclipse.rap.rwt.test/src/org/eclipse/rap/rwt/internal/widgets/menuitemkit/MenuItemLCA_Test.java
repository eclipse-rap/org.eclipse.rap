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

package org.eclipse.rap.rwt.internal.widgets.menuitemkit;

import java.io.IOException;
import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.events.SelectionListener;
import org.eclipse.rap.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.Fixture;
import com.w4t.engine.requests.RequestParams;
import com.w4t.util.browser.Ie6;


public class MenuItemLCA_Test extends TestCase {
  
  public void testWidgetSelected() throws IOException {
    final boolean[] wasEventFired = { false };
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Menu menu = new Menu( shell, RWT.BAR );
    shell.setMenuBar( menu );
    final MenuItem menuItem = new MenuItem( menu, RWT.PUSH );
    menuItem.addSelectionListener( new SelectionListener() {
      public void widgetSelected( final SelectionEvent event ) {
        wasEventFired[ 0 ] = true;
        assertEquals( null, event.item );
        assertSame( menuItem, event.getSource() );
        assertEquals( true, event.doit );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
      }
    } );
    
    String displayId = DisplayUtil.getAdapter( display ).getId();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    String menuItemId = WidgetUtil.getId( menuItem );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, menuItemId );
    new RWTLifeCycle().execute();
    assertEquals( true, wasEventFired[ 0 ] );
  }

  public void testCheckItemSelected() throws IOException {
    final boolean[] wasEventFired = { false };
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Menu menuBar = new Menu( shell, RWT.BAR );
    shell.setMenuBar( menuBar );
    Menu menu = new Menu( menuBar );
    final MenuItem menuItem = new MenuItem( menu, RWT.CHECK );
    menuItem.addSelectionListener( new SelectionListener() {
      public void widgetSelected( final SelectionEvent event ) {
        wasEventFired[ 0 ] = true;
        assertEquals( null, event.item );
        assertSame( menuItem, event.getSource() );
        assertEquals( true, event.doit );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, menuItem.getSelection() );
      }
    } );
    
    String displayId = DisplayUtil.getAdapter( display ).getId();
    String menuItemId = WidgetUtil.getId( menuItem );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( menuItemId + ".selection", "true" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, menuItemId );
    new RWTLifeCycle().execute();
    assertEquals( true, wasEventFired[ 0 ] );
  }
  
  public void testRadioItemSelected() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Menu menuBar = new Menu( shell, RWT.BAR );
    MenuItem menuBarItem = new MenuItem( menuBar, RWT.CASCADE );
    Menu menu = new Menu( menuBarItem );
    menuBarItem.setMenu( menu );
    new MenuItem( menu, RWT.PUSH );
    MenuItem radioItem1Group1 = new MenuItem( menu, RWT.RADIO );
    MenuItem radioItem2Group1 = new MenuItem( menu, RWT.RADIO );
    new MenuItem( menu, RWT.CHECK );
    
    String displayId = DisplayUtil.getAdapter( display ).getId();
    String radioItem1Group1Id = WidgetUtil.getId( radioItem1Group1 );
    
    radioItem2Group1.setSelection( true );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( radioItem1Group1Id + ".selection", "true" );
    new RWTLifeCycle().execute();
    assertEquals( true, radioItem1Group1.getSelection() );
    assertEquals( false, radioItem2Group1.getSelection() );
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
