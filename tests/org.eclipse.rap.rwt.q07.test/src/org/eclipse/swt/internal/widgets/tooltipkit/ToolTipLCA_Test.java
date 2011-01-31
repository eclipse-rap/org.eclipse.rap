/*******************************************************************************
 * Copyright (c) 2011 Rüdiger Herrmann and others. All rights reserved.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.tooltipkit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;


public class ToolTipLCA_Test extends TestCase {
  private Display display;
  private Shell shell;
  private ToolTip toolTip;

  public void testPreserveVisible() {
    toolTip.setVisible( true );
    Fixture.preserveWidgets();
    assertEquals( Boolean.TRUE, getPreserved( ToolTipLCA.PROP_VISIBLE ) );
  }
  
  public void testPreserveLocation() {
    Point location = new Point( 1, 2 );
    toolTip.setLocation( location );
    Fixture.preserveWidgets();
    assertEquals( location, getPreserved( ToolTipLCA.PROP_LOCATION ) );
  }
  
  public void testPreserveText() {
    final String text = "text";
    toolTip.setText( text );
    Fixture.preserveWidgets();
    assertEquals( text, getPreserved( ToolTipLCA.PROP_TEXT ) );
  }
  
  public void testPreserveMessage() {
    final String text = "message";
    toolTip.setMessage( text );
    Fixture.preserveWidgets();
    assertEquals( text, getPreserved( ToolTipLCA.PROP_MESSAGE ) );
  }
  
  public void testPreserveAutoHide() {
    toolTip.setAutoHide( false );
    Fixture.preserveWidgets();
    assertEquals( Boolean.FALSE, getPreserved( ToolTipLCA.PROP_AUTO_HIDE ) );
  }
  
  public void testPreserveSelectionListeners() {
    SelectionListener selectionListener = new SelectionAdapter() { };
    toolTip.addSelectionListener( selectionListener );
    Fixture.preserveWidgets();
    assertEquals( Boolean.TRUE, 
                  getPreserved( ToolTipLCA.PROP_SELECTION_LISTENER ) );
  }
  
  public void testReadVisibleWithRequestParamFalse() {
    toolTip.setVisible( true );
    String toolTipId = WidgetUtil.getId( toolTip );
    Fixture.fakeRequestParam( toolTipId + ".visible", "false" );
    Fixture.readDataAndProcessAction( display );
    assertFalse( toolTip.isVisible() );
  }
  
  public void testReadVisibleWithNoRequestParam() {
    toolTip.setVisible( true );
    Fixture.readDataAndProcessAction( display );
    assertTrue( toolTip.isVisible() );
  }
  
  public void testSelectionEvent() {
    final SelectionEvent[] eventLog = { null };
    toolTip.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        eventLog[ 0 ] = event;
      }
    } );
    String toolTipId = WidgetUtil.getId( toolTip );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, toolTipId );
    Fixture.readDataAndProcessAction( display );
    SelectionEvent event = eventLog[ 0 ];
    assertNotNull( event );
    assertSame( toolTip, event.widget );
    assertTrue( event.doit );
    assertNull( event.data );
    assertNull( event.text );
    assertEquals( 0, event.detail );
    assertEquals( 0, event.y );
    assertEquals( 0, event.y );
    assertEquals( 0, event.width );
    assertEquals( 0, event.height );
    assertSame( display, event.display );
    assertNull( event.item );
  }
  
  public void testPropertySetterOrderInRenderChanges() throws IOException {
    toolTip.setText( "text" );
    toolTip.setMessage( "message" );
    toolTip.setAutoHide( false );
    toolTip.setLocation( 1, 2 );
    toolTip.setVisible( true );
    Fixture.markInitialized( toolTip );
    new ToolTipLCA().renderChanges( toolTip );
    String markup = Fixture.getAllMarkup();
    int setVisibleIndex = markup.indexOf( "setVisible" );
    assertTrue( markup.indexOf( "setText" ) < setVisibleIndex );
    assertTrue( markup.indexOf( "setMessage" ) < setVisibleIndex );
    assertTrue( markup.indexOf( "setLocation" ) < setVisibleIndex );
    assertTrue( markup.indexOf( "setHideAfterTimeout" ) < setVisibleIndex );
  }
  
  public void testEscapeText() throws IOException {
    toolTip.setText( "E<s>ca'pe\" && me" );
    ToolTipLCA.writeText( toolTip );
    String expected = "w.setText( \"E&lt;s&gt;ca'pe&quot; &amp;&amp; me\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) > -1 );
  }
  
  public void testEscapeMessage() throws IOException {
    toolTip.setMessage( "E<s>ca'pe\" && me\n" );
    ToolTipLCA.writeMessage( toolTip );
    String expected
      = "w.setMessage( \"E&lt;s&gt;ca'pe&quot; &amp;&amp; me<br/>\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) > -1 );
  }
  
  public void testGetImageWithErrorIconStyle() {
    ToolTip toolTip = new ToolTip( shell, SWT.ICON_ERROR );
    assertNotNull( ToolTipLCA.getImage( toolTip ) );
  }
  
  public void testGetImageWithNoStyle() {
    ToolTip toolTip = new ToolTip( shell, SWT.NONE );
    assertNull( ToolTipLCA.getImage( toolTip ) );
  }

  public void testDontOverwriteBackgroundGradient() throws IOException {
    ToolTipLCA lca = new ToolTipLCA();
    Fixture.markInitialized( toolTip );
    lca.preserveValues( toolTip );
    lca.renderChanges( toolTip );
    String notExpected = "wm.setBackgroundGradient";
    assertTrue( Fixture.getAllMarkup().indexOf( notExpected ) == -1 );
  }
  
  public void testDontOverwriteBorderRadius() throws IOException {
    ToolTipLCA lca = new ToolTipLCA();
    Fixture.markInitialized( toolTip );
    lca.preserveValues( toolTip );
    lca.renderChanges( toolTip );
    String notExpected = "wm.setRoundedBorder";
    assertTrue( Fixture.getAllMarkup().indexOf( notExpected ) == -1 );
  }
  
  public void testDontrerwriteCusotmVariant() throws IOException {
    ToolTipLCA lca = new ToolTipLCA();
    toolTip.setData( WidgetUtil.CUSTOM_VARIANT, "foo" );
    Fixture.markInitialized( toolTip );
    lca.preserveValues( toolTip );
    lca.renderChanges( toolTip );
    String notExpected = "w.addState";
    assertTrue( Fixture.getAllMarkup().indexOf( notExpected ) == -1 );
    toolTip.setData( WidgetUtil.CUSTOM_VARIANT, null );
  }

  private Object getPreserved( String propertyName ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( toolTip );
    return adapter.getPreserved( propertyName );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    toolTip = new ToolTip( shell, SWT.NONE );
    Fixture.markInitialized( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
