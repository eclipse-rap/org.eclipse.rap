/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.labelkit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class LabelLCA_Test extends TestCase {

  public void testStandardPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
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
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    int style = SWT.SEPARATOR | SWT.HORIZONTAL;
    Label label = new Label( shell, style );
    Fixture.markInitialized( display );
    testPreserveValues( display, label );
  }

  public void testRenderText() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Label label = new Label( shell, SWT.NONE );
    LabelLCA lca = new LabelLCA();
    ControlLCAUtil.preserveValues( label );
    Fixture.markInitialized( label );
    Fixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    label.setText( "test" );
    lca.renderChanges( label );
    String expected = "LabelUtil.setText( wm.findWidgetById( \"w2\" ), \"test\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    label.setText( "\ntest" );
    lca.renderChanges( label );
    expected = "LabelUtil.setText( wm.findWidgetById( \"w2\" ), \"<br/>test\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    label.setText( "te\nst" );
    lca.renderChanges( label );
    expected = "LabelUtil.setText( wm.findWidgetById( \"w2\" ), \"te<br/>st\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    label.setText( "test\n" );
    lca.renderChanges( label );
    expected = "LabelUtil.setText( wm.findWidgetById( \"w2\" ), \"test<br/>\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    label.setText( "te\n\nst" );
    lca.renderChanges( label );
    expected = "LabelUtil.setText( wm.findWidgetById( \"w2\" ), \"te<br/><br/>st\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    label.setText( "te\ns\nt" );
    lca.renderChanges( label );
    expected = "LabelUtil.setText( wm.findWidgetById( \"w2\" ), \"te<br/>s<br/>t\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    // truncate zeros
    label.setText( "te\000st" );
    lca.renderChanges( label );
    expected = "LabelUtil.setText( wm.findWidgetById( \"w2\" ), \"te\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }

  public void testEscape() throws Exception {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Label label = new Label( shell, SWT.NONE );
    label.setText( "&E<s>ca'pe\" && text" );
    label.setToolTipText( "&E<s>ca'pe\" && tooltip" );
    Fixture.fakeResponseWriter();
    StandardLabelLCA lca = new StandardLabelLCA();
    lca.renderChanges( label );
    String expected1 = "\"E&lt;s&gt;ca'pe&quot; &amp; text\"";
    String expected2 = "\"&amp;E&lt;s&gt;ca'pe&quot; &amp;&amp; tooltip\"";
    String actual = Fixture.getAllMarkup();
    assertTrue( actual.indexOf( expected1 ) != -1 );
    assertTrue( actual.indexOf( expected2 ) != -1 );
  }
  
  public void testRenderDispose() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Label label = new Label( shell, SWT.NONE );
    label.dispose();
    Fixture.fakeResponseWriter();
    LabelLCA labelLCA = new LabelLCA();
    labelLCA.renderDispose( label );
    assertEquals( "wm.dispose( \"w2\" );", Fixture.getAllMarkup() );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
