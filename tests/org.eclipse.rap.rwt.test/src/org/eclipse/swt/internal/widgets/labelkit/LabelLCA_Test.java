/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.labelkit;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.protocol.Message;
import org.eclipse.rwt.internal.protocol.Message.CreateOperation;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class LabelLCA_Test extends TestCase {

  private Display display;
  private Shell shell;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    Fixture.fakeNewRequest( display );
  }

  @Override
  protected void tearDown() throws Exception {
    display.dispose();
    Fixture.tearDown();
  }
  
  public void testStandardPreserveValues() {
    Label label = new Label( shell, SWT.NONE );
    Fixture.markInitialized( display );
    testPreserveValues( display, label );
    //Text
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( label );
    assertEquals( "", adapter.getPreserved( Props.TEXT ) );
    Fixture.clearPreserved();
    label.setText( "xyz" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( label.getText(), adapter.getPreserved( Props.TEXT ) );
    Fixture.clearPreserved();
    //Image
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    label.setImage( image );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertSame( image, adapter.getPreserved( Props.IMAGE ) );
    Fixture.clearPreserved();
    //aligment
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    Integer alignment = ( Integer )adapter.getPreserved( "alignment" );
    assertEquals( new Integer( SWT.LEFT ), alignment );
    Fixture.clearPreserved();
    label.setAlignment( SWT.RIGHT );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    alignment = ( Integer )adapter.getPreserved( "alignment" );
    assertEquals( new Integer( SWT.RIGHT ), alignment );
    Fixture.clearPreserved();
    label.setAlignment( SWT.CENTER );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    alignment = ( Integer )adapter.getPreserved( "alignment" );
    assertEquals( new Integer( SWT.CENTER ), alignment );
    Fixture.clearPreserved();
    label.setAlignment( SWT.LEFT );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    alignment = ( Integer )adapter.getPreserved( "alignment" );
    assertEquals( new Integer( SWT.LEFT ), alignment );
  }

  private void testPreserveValues( final Display display, final Label label ) {
    // bound
    Rectangle rectangle = new Rectangle( 10, 10, 10, 10 );
    label.setBounds( rectangle );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( label );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    // z-index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    //menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( label );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    label.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    //visible
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    label.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    //enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    label.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    label.setEnabled( true );
    //control_listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    Boolean hasListeners;
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    label.addControlListener( new ControlAdapter() { } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    //foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    label.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    label.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    label.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    //tab_index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    //tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( null, label.getToolTipText() );
    Fixture.clearPreserved();
    label.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( "some text", label.getToolTipText() );
    Fixture.clearPreserved();
    //activate_listeners   Focus_listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    ActivateEvent.addListener( label, new ActivateAdapter() {
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
  }

  public void testSeparatorPreserveValues() {
    int style = SWT.SEPARATOR | SWT.HORIZONTAL;
    Label label = new Label( shell, style );
    Fixture.markInitialized( display );
    testPreserveValues( display, label );
  }
  
  public void testRenderInitialText() throws IOException {
    Label label = new Label( shell, SWT.NONE );
    LabelLCA lca = new LabelLCA();
    
    lca.renderChanges( label );
    
    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "text" ) );
  }

  public void testRenderText() throws IOException {
    Label label = new Label( shell, SWT.NONE );
    LabelLCA lca = new LabelLCA();
    
    label.setText( "test" );
    lca.renderChanges( label );
    
    Message message = Fixture.getProtocolMessage();
    assertEquals( "test", message.findSetProperty( label, "text" ) );
  }

  public void testRenderTextWithQuotationMarks() throws IOException {
    Label label = new Label( shell, SWT.NONE );
    LabelLCA lca = new LabelLCA();
    
    label.setText( "te\"s't" );
    lca.renderChanges( label );
    
    Message message = Fixture.getProtocolMessage();
    assertEquals( "te\"s't", message.findSetProperty( label, "text" ) );
  }

  public void testRenderTextWithNewlines() throws IOException {
    Label label = new Label( shell, SWT.NONE );
    LabelLCA lca = new LabelLCA();
    
    label.setText( "\ntes\r\nt\n" );
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "\ntes\r\nt\n", message.findSetProperty( label, "text" ) );
  }

  public void testRenderTextUnchanged() throws IOException {
    Label label = new Label( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( label );
    LabelLCA lca = new LabelLCA();
    
    label.setText( "foo" );

    Fixture.preserveWidgets();
    lca.renderChanges( label );
    
    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "text" ) );
  }

  public void testRenderDispose() throws IOException {
    Label label = new Label( shell, SWT.NONE );
    label.dispose();
    LabelLCA labelLCA = new LabelLCA();
    labelLCA.renderDispose( label );
    assertEquals( "wm.dispose( \"w2\" );", Fixture.getAllMarkup() );
  }

  public void testRenderCreate() throws IOException {
    Label label = new Label( shell, SWT.WRAP );
    LabelLCA lca = new LabelLCA();
    
    lca.renderInitialization( label );
    
    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( label );
    assertEquals( "org.eclipse.swt.widgets.Label", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "WRAP" ) );
  }

}
