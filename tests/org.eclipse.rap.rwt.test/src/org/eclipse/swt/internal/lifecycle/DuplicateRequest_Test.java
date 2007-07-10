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

package org.eclipse.swt.internal.lifecycle;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;

import com.w4t.Fixture;
import com.w4t.engine.requests.RequestParams;
import com.w4t.util.browser.Ie6;

import junit.framework.TestCase;


public class DuplicateRequest_Test extends TestCase {
  
  public void testDisabledButton() throws IOException {
    final java.util.List events = new ArrayList(); 
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.setLayout( new FillLayout() );
    final Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        events.add( event );
        button.setEnabled( false );
      }
    } );
    shell.open();
    
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    String displayId = DisplayUtil.getAdapter( display ).getId();
    String buttonId = WidgetUtil.getId( button );

    // First request - within this request the button will become disabled
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    lifeCycle.execute();
    RWTFixture.fakeUIThread();
    assertFalse( button.getEnabled() );
    assertEquals( 1, events.size() );

    // Second request - simulating a click on the now disabled button
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    lifeCycle.execute();
    RWTFixture.fakeUIThread();
    assertEquals( 1, events.size() );
  }
  
  public void testInvisibleButton() throws IOException {
    final java.util.List events = new ArrayList(); 
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.setLayout( new FillLayout() );
    final Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        events.add( event );
        button.setVisible( false );
      }
    } );
    shell.open();
    
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    String displayId = DisplayUtil.getAdapter( display ).getId();
    String buttonId = WidgetUtil.getId( button );
    
    // First request - within this request the button will become disabled
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    lifeCycle.execute();
    RWTFixture.fakeUIThread();
    assertFalse( button.getVisible() );
    assertEquals( 1, events.size() );
    
    // Second request - simulating a click on the now disabled button
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    lifeCycle.execute();
    RWTFixture.fakeUIThread();
    assertEquals( 1, events.size() );
  }
  
  public void testDisposedButton() throws IOException {
    final java.util.List events = new ArrayList(); 
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.setLayout( new FillLayout() );
    final Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        events.add( event );
        button.dispose();
      }
    } );
    shell.open();
    
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    String displayId = DisplayUtil.getAdapter( display ).getId();
    String buttonId = WidgetUtil.getId( button );
    
    // First request - within this request the button will become disabled
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    lifeCycle.execute();
    RWTFixture.fakeUIThread();
    assertTrue( button.isDisposed() );
    assertEquals( 1, events.size() );
    
    // Second request - simulating a click on the now disabled button
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    lifeCycle.execute();
    RWTFixture.fakeUIThread();
    assertEquals( 1, events.size() );
  }
  
  public void testButtonOpensModalShell() throws IOException {
    final java.util.List events = new ArrayList(); 
    Display display = new Display();
    final Shell shell = new Shell( display );
    shell.setLayout( new FillLayout() );
    final Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        events.add( event );
        Shell dialog = new Shell( shell, SWT.APPLICATION_MODAL );
        dialog.setSize( 100, 100 );
        dialog.open();
      }
    } );
    shell.open();
    
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    String displayId = DisplayUtil.getAdapter( display ).getId();
    String buttonId = WidgetUtil.getId( button );

    // First request - within this request a modal dialog will be opened
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    lifeCycle.execute();
    RWTFixture.fakeUIThread();
    assertEquals( 1, events.size() );

    // Second request - simulates click on the button that should not be 
    // available anymore as it is blocked by the modal dialog
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    lifeCycle.execute();
    RWTFixture.fakeUIThread();
    assertEquals( 1, events.size() );
  }

  public void testFocusOutOpensModalShell() throws IOException {
    final java.util.List events = new ArrayList(); 
    Display display = new Display();
    final Shell shell = new Shell( display );
    shell.setLayout( new FillLayout() );
    final Text text = new Text( shell, SWT.NONE );
    text.addFocusListener( new FocusAdapter() {
      public void focusLost( final FocusEvent event ) {
        events.add( event );
        Shell dialog = new Shell( shell, SWT.APPLICATION_MODAL );
        dialog.setSize( 100, 100 );
        dialog.open();
      }
    } );
    final Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        events.add( event );
      }
    } );
    shell.open();
    
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    String displayId = DisplayUtil.getAdapter( display ).getId();
    String buttonId = WidgetUtil.getId( button );
    
    // Within this request a focusLost and widgetSelected (for the button)
    // is sent. The focusList listener opens a modal shell, thus the event on
    // button must not be executed
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( displayId + ".focusControl", buttonId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    lifeCycle.execute();
    RWTFixture.fakeUIThread();
    assertEquals( 1, events.size() );
    assertEquals( FocusEvent.class, events.get( 0 ).getClass() );
    FocusEvent event = ( FocusEvent )events.get( 0 );
    assertSame( text, event.widget );
  }
  
  public void testCloseClosedShell() throws IOException {
    final java.util.List events = new ArrayList(); 
    Display display = new Display();
    final Shell shell = new Shell( display, SWT.SHELL_TRIM );
    shell.setSize( 100, 100 );
    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( final ShellEvent event ) {
        events.add( event );
      }
    } );
    shell.open();
    
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    String displayId = DisplayUtil.getAdapter( display ).getId();
    String shellId = WidgetUtil.getId( shell );
    
    // First request - simulates click on close button of shell
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_SHELL_CLOSED, shellId );
    lifeCycle.execute();
    RWTFixture.fakeUIThread();
    assertEquals( 1, events.size() );
    assertTrue( shell.isDisposed() );

    // Second request - simulates click on close button of shell that was 
    // already closed by the first request
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_SHELL_CLOSED, shellId );
    lifeCycle.execute();
    RWTFixture.fakeUIThread();
    assertEquals( 1, events.size() );
  }
  
  public void testNestedModalShell() throws IOException {
    final java.util.List events = new ArrayList(); 
    Display display = new Display();
    final Shell shell1 = new Shell( display, SWT.APPLICATION_MODAL );
    shell1.setSize( 100, 100 );
    shell1.open();
    
    Shell shell2 = new Shell( display, SWT.APPLICATION_MODAL );
    shell2.setSize( 100, 100 );
    Button button = new Button( shell2, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        events.add( event );
      }
    } );
    shell2.open();
    
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    String displayId = DisplayUtil.getAdapter( display ).getId();
    String buttonId = WidgetUtil.getId( button );
    
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    lifeCycle.execute();
    RWTFixture.fakeUIThread();
    assertEquals( 1, events.size() );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.fakeBrowser( new Ie6( true, true ) );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
