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

package org.eclipse.swt.internal.widgets.tabfolderkit;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.browser.Ie6;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;



public class TabFolderLCA_Test extends TestCase {

  public void testSelectionWithoutListener() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.setLayout( new FillLayout() );
    TabFolder folder = new TabFolder( shell, SWT.NONE );
    TabItem item0 = new TabItem( folder, SWT.NONE );
    Control control0 = new Button( folder, SWT.PUSH );
    item0.setControl( control0 );
    TabItem item1 = new TabItem( folder, SWT.NONE );
    Control control1 = new Button( folder, SWT.PUSH );
    item1.setControl( control1 );
    shell.open();

    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    lifeCycle.addPhaseListener( new PreserveWidgetsPhaseListener() );

    String displayId = DisplayUtil.getAdapter( display ).getId();
    String folderId = WidgetUtil.getId( folder );
    String item1Id = WidgetUtil.getId( item1 );

    // Run life cycle once to reduce markup that is written for the actual 
    // request under test 
    RWTFixture.fakeNewRequest();
    lifeCycle.execute();
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    lifeCycle.execute();

    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, folderId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED_ITEM, item1Id );
    lifeCycle.execute();
    RWTFixture.fakeUIThread();
    assertEquals( 1, folder.getSelectionIndex() );
    assertFalse( control0.getVisible() );
    assertTrue( control1.getVisible() );
  }
  
  public void testSelectionWithListener() throws IOException {
    final java.util.List events = new ArrayList(); 
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.setLayout( new FillLayout() );
    TabFolder folder = new TabFolder( shell, SWT.NONE );
    TabItem item0 = new TabItem( folder, SWT.NONE );
    Control control0 = new Button( folder, SWT.PUSH );
    item0.setControl( control0 );
    TabItem item1 = new TabItem( folder, SWT.NONE );
    Control control1 = new Button( folder, SWT.PUSH );
    item1.setControl( control1 );
    shell.open();
    folder.addSelectionListener( new SelectionListener() {
      public void widgetSelected( final SelectionEvent event ) {
        events.add( event );
      }
      public void widgetDefaultSelected( final SelectionEvent event ) {
        events.add( event );
      }
    } );
    
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    lifeCycle.addPhaseListener( new PreserveWidgetsPhaseListener() );
    
    String displayId = DisplayUtil.getAdapter( display ).getId();
    String item1Id = WidgetUtil.getId( item1 );
    String folderId = WidgetUtil.getId( folder );
    
    // Run life cycle once to reduce markup that is written for the actual 
    // request under test 
    RWTFixture.fakeNewRequest();
    lifeCycle.execute();
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    lifeCycle.execute();
    
    events.clear();
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, folderId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED_ITEM, item1Id );
    
    lifeCycle.execute();
    RWTFixture.fakeUIThread();
    assertEquals( 1, folder.getSelectionIndex() );
    assertFalse( control0.getVisible() );
    assertTrue( control1.getVisible() );
    assertEquals( 1, events.size() );
    SelectionEvent event = ( SelectionEvent )events.get( 0 );
    assertSame( item1, event.item );
    assertSame( folder, event.widget );
    assertTrue( event.doit );
    assertEquals( 0, event.x );
    assertEquals( 0, event.y );
    assertEquals( 0, event.width );
    assertEquals( 0, event.height );
    assertEquals( 0, event.detail );
    assertNull( event.text );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.fakeBrowser( new Ie6( true, true ) );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
