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

package org.eclipse.swt.internal.widgets.buttonkit;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.widgets.IShellAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class ButtonLCA_Test extends TestCase {

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    Boolean hasListeners;
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    SelectionListener selectionListener = new SelectionAdapter() {};
    button.addSelectionListener( selectionListener );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    display.dispose();
  }
  
  public void testDisabledButtonSelection() {
    final StringBuffer log = new StringBuffer();
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Button button = new Button( shell, SWT.NONE );
    Label label = new Label( shell, SWT.NONE );
    ActivateEvent.addListener( button, new ActivateAdapter() {
      public void activated( final ActivateEvent event ) {
        log.append( "widgetActivated|" );
        button.setEnabled( false );
      }
    } );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        log.append( "widgetSelected|" );
      }
    } );
    Object adapter = shell.getAdapter( IShellAdapter.class );
    IShellAdapter shellAdapter = ( IShellAdapter )adapter;
    shellAdapter.setActiveControl( label );

    String displayId = DisplayUtil.getId( display );
    String buttonId = WidgetUtil.getId( button );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_ACTIVATED, buttonId );
    
    RWTFixture.readDataAndProcessAction( display );
    assertEquals( "widgetActivated|", log.toString() );
  }
  
  public void testSelectionEvent() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display );
    final Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertSame( button, event.getSource() );
        assertEquals( 0, event.detail );
        log.append( "widgetSelected" );
      }
    } );
    String displayId = DisplayUtil.getId( display );
    String buttonId = WidgetUtil.getId( button );

    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( "org.eclipse.swt.events.widgetSelected",
                              buttonId );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( "widgetSelected", log.toString() );
  }
  
  public void testEscape() throws Exception {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Button pushButton = new Button( shell, SWT.PUSH );
    pushButton.setText( "PUSH &E<s>ca'pe\" && me" );
    Button checkButton = new Button( shell, SWT.CHECK );
    checkButton.setText( "CHECK &E<s>ca'pe\" && me" );
    Button radioButton = new Button( shell, SWT.RADIO );
    radioButton.setText( "RADIO &E<s>ca'pe\" && me" );
    Fixture.fakeResponseWriter();
    ButtonDelegateLCA pushLCA = new PushButtonDelegateLCA();
    pushLCA.renderChanges( pushButton );
    String expected = "\"PUSH E&lt;s&gt;ca'pe&quot; &amp; me\"";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    ButtonDelegateLCA checkLCA = new CheckButtonDelegateLCA();
    checkLCA.renderChanges( checkButton );
    expected = "\"CHECK E&lt;s&gt;ca'pe&quot; &amp; me\"";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    ButtonDelegateLCA radioLCA = new RadioButtonDelegateLCA();
    radioLCA.renderChanges( radioButton );
    expected = "\"RADIO E&lt;s&gt;ca'pe&quot; &amp; me\"";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
