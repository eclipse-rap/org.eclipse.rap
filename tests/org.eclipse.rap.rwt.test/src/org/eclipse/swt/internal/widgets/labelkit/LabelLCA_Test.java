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
package org.eclipse.swt.internal.widgets.labelkit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
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
    RWTFixture.markInitialized( display );
    testPreserveValues( display, label );
    //Text
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( label );
    assertEquals( "", adapter.getPreserved( Props.TEXT ) );
    RWTFixture.clearPreserved();
    label.setText( "xyz" );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( label.getText(), adapter.getPreserved( Props.TEXT ) );
    RWTFixture.clearPreserved();
    //Image
    Image image = Graphics.getImage( RWTFixture.IMAGE1 );
    label.setImage( image );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertSame( image, adapter.getPreserved( Props.IMAGE ) );
    RWTFixture.clearPreserved();
    //aligment
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    Integer alignment = ( Integer )adapter.getPreserved( "alignment" );
    assertEquals( new Integer( SWT.LEFT ), alignment );
    RWTFixture.clearPreserved();
    label.setAlignment( SWT.RIGHT );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    alignment = ( Integer )adapter.getPreserved( "alignment" );
    assertEquals( new Integer( SWT.RIGHT ), alignment );
    RWTFixture.clearPreserved();
    label.setAlignment( SWT.CENTER );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    alignment = ( Integer )adapter.getPreserved( "alignment" );
    assertEquals( new Integer( SWT.CENTER ), alignment );
    RWTFixture.clearPreserved();
    label.setAlignment( SWT.LEFT );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    alignment = ( Integer )adapter.getPreserved( "alignment" );
    assertEquals( new Integer( SWT.LEFT ), alignment );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  private void testPreserveValues( final Display display, final Label label ) {
    // bound
    Rectangle rectangle = new Rectangle( 10, 10, 10, 10 );
    label.setBounds( rectangle );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( label );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    RWTFixture.clearPreserved();
    // z-index
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    RWTFixture.clearPreserved();
    //menu
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    RWTFixture.clearPreserved();
    Menu menu = new Menu( label );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    label.setMenu( menu );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    //visible
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    label.setVisible( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    //enabled
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    label.setEnabled( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    //control_listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    Boolean hasListeners;
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    label.addControlListener( new ControlListener() {

      public void controlMoved( final ControlEvent e ) {
      }

      public void controlResized( final ControlEvent e ) {
      }
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    //foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    label.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    label.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    label.setFont( font );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    RWTFixture.clearPreserved();
    //tab_index
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    RWTFixture.clearPreserved();
    //tooltiptext
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( null, label.getToolTipText() );
    RWTFixture.clearPreserved();
    label.setToolTipText( "some text" );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( "some text", label.getToolTipText() );
    RWTFixture.clearPreserved();
    //activate_listeners   Focus_listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    ActivateEvent.addListener( label, new ActivateAdapter() {
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
  }

  public void testSeparatorPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    int style = SWT.SEPARATOR | SWT.HORIZONTAL;
    Label label = new Label( shell, style );
    RWTFixture.markInitialized( display );
    testPreserveValues( display, label );
    display.dispose();
  }

  public void testRenderText() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Label label = new Label( shell, SWT.NONE );
    LabelLCA lca = new LabelLCA();
    ControlLCAUtil.preserveValues( label );
    RWTFixture.markInitialized( label );
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

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
