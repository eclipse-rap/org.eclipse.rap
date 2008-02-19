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

package org.eclipse.swt.internal.widgets.textkit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class TextLCA_Test extends TestCase {

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Text text = new Text( shell, SWT.NONE );
    text.setText( "abc" );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    assertEquals( text.getText(), adapter.getPreserved( Props.TEXT ) );
    display.dispose();
  }

  public void testReadData() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Text text = new Text( shell, SWT.NONE );
    String textId = WidgetUtil.getId( text );
    // read changed text
    Fixture.fakeRequestParam( textId + ".text", "abc" );
    WidgetUtil.getLCA( text ).readData( text );
    assertEquals( "abc", text.getText() );
    // read changed selection
    Fixture.fakeRequestParam( textId + ".text", "abc" );
    Fixture.fakeRequestParam( textId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( textId + ".selectionCount", "1" );
    WidgetUtil.getLCA( text ).readData( text );
    assertEquals( new Point( 1, 2 ), text.getSelection() );
  }

  public void testRenderChanges() throws IOException {
    Fixture.fakeResponseWriter();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Text text = new Text( shell, SWT.NONE );
    shell.open();
    RWTFixture.markInitialized( display );
    RWTFixture.markInitialized( text );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    TextLCA textLCA = new TextLCA();
    text.setText( "hello" );
    textLCA.renderChanges( text );
    assertTrue( Fixture.getAllMarkup().endsWith( "setValue( \"hello\" );" ) );
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    textLCA.renderChanges( text );
    assertEquals( "", Fixture.getAllMarkup() );
  }

  public void testModifyEvent() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Text text = new Text( shell, SWT.NONE );
    text.addModifyListener( new ModifyListener() {
      public void modifyText( final ModifyEvent event ) {
        assertEquals( text, event.getSource() );
        log.append( "modifyText" );
      }
    } );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    String textId = WidgetUtil.getId( text );
    
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( textId + ".text", "new text" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, textId );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( "modifyText", log.toString() );
  }
  
  public void testVerifyEvent() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Text text = new Text( shell, SWT.NONE );
    text.addVerifyListener( new VerifyListener() {
      public void verifyText( final VerifyEvent event ) {
        assertEquals( text, event.getSource() );
        assertEquals( text, event.widget );
        assertTrue( event.doit );
        log.append( "verifyText" );
      }
    } );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    String textId = WidgetUtil.getId( text );
    
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, textId );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( "verifyText", log.toString() );
  }
  
  public void testTextLimit() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Text text = new Text( shell, SWT.NONE );
    TextLCA lca = new TextLCA();
    // run LCA one to dump the here uninteresting prolog
    Fixture.fakeResponseWriter();
    lca.renderChanges( text ); 
    
    // Initially no textLimit must be rendered if the initial value is untouched
    Fixture.fakeResponseWriter();
    lca.renderChanges( text );
    assertEquals( -1, Fixture.getAllMarkup().indexOf( "setMaxLength" ) );
    
    // Positive textLimit is written as setMaxLength( ... )
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( text );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    text.setTextLimit( 12 );
    lca.renderChanges( text );
    String expected = "setMaxLength( 12 );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    
    // Negative textLimit is tread as 'no limit'
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( text );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    text.setTextLimit( -50 );
    lca.renderChanges( text );
    expected = "setMaxLength( null );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
