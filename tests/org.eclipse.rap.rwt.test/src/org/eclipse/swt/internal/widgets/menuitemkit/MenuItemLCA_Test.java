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

package org.eclipse.swt.internal.widgets.menuitemkit;

import java.io.IOException;
import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;
import com.w4t.Fixture;
import com.w4t.engine.requests.RequestParams;
import com.w4t.util.browser.Ie6;


public class MenuItemLCA_Test extends TestCase {
  
  public void testWidgetSelected() throws IOException {
    final boolean[] wasEventFired = { false };
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Menu menu = new Menu( shell, SWT.BAR );
    shell.setMenuBar( menu );
    final MenuItem menuItem = new MenuItem( menu, SWT.PUSH );
    menuItem.addSelectionListener( new SelectionAdapter() {
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
    Shell shell = new Shell( display , SWT.NONE );
    Menu menuBar = new Menu( shell, SWT.BAR );
    shell.setMenuBar( menuBar );
    Menu menu = new Menu( menuBar );
    final MenuItem menuItem = new MenuItem( menu, SWT.CHECK );
    menuItem.addSelectionListener( new SelectionAdapter() {
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
    Shell shell = new Shell( display , SWT.NONE );
    Menu menuBar = new Menu( shell, SWT.BAR );
    MenuItem menuBarItem = new MenuItem( menuBar, SWT.CASCADE );
    Menu menu = new Menu( menuBarItem );
    menuBarItem.setMenu( menu );
    new MenuItem( menu, SWT.PUSH );
    MenuItem radioItem1Group1 = new MenuItem( menu, SWT.RADIO );
    MenuItem radioItem2Group1 = new MenuItem( menu, SWT.RADIO );
    new MenuItem( menu, SWT.CHECK );
    
    String displayId = DisplayUtil.getAdapter( display ).getId();
    String radioItem1Group1Id = WidgetUtil.getId( radioItem1Group1 );
    
    radioItem2Group1.setSelection( true );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( radioItem1Group1Id + ".selection", "true" );
    new RWTLifeCycle().execute();
    RWTFixture.fakeUIThread();
    assertEquals( true, radioItem1Group1.getSelection() );
    assertEquals( false, radioItem2Group1.getSelection() );
    RWTFixture.removeUIThread();
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
