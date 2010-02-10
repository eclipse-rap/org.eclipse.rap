/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.scalekit;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class ScaleLCA_Test extends TestCase {

  public void testScalePreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Scale scale = new Scale( shell, SWT.HORIZONTAL );
    Fixture.markInitialized( display );
    // Test preserved minimum, maximum,
    // selection, increment and ageIncrement
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( scale );
    Integer minimum
      = ( Integer )adapter.getPreserved( ScaleLCA.PROP_MINIMUM );
    assertEquals( 0, minimum.intValue() );
    Integer maximum
      = ( Integer )adapter.getPreserved( ScaleLCA.PROP_MAXIMUM );
    assertEquals( 100, maximum.intValue() );
    Integer selection
      = ( Integer )adapter.getPreserved( ScaleLCA.PROP_SELECTION );
    assertEquals( 0, selection.intValue() );
    Integer increment
      = ( Integer )adapter.getPreserved( ScaleLCA.PROP_INCREMENT );
    assertEquals( 1, increment.intValue() );
    Integer pageIncrement
      = ( Integer )adapter.getPreserved( ScaleLCA.PROP_PAGE_INCREMENT );
    assertEquals( 10, pageIncrement.intValue() );
    Fixture.clearPreserved();
    // Test preserved control properties
    testPreserveControlProperties( scale );
    // Test preserved selection listeners
    testPreserveSelectionListener( scale );
    display.dispose();
  }

  public void testSelectionEvent() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Scale scale = new Scale( shell, SWT.HORIZONTAL );
    testSelectionEvent( scale );
  }

  private void testPreserveControlProperties( final Scale scale ) {
    // bound
    Rectangle rectangle = new Rectangle( 10, 10, 10, 10 );
    scale.setBounds( rectangle );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( scale );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    // enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( scale );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    scale.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( scale );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    scale.setEnabled( true );
    // visible
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( scale );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    scale.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( scale );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    // menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( scale );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( scale );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    scale.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( scale );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    //foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    scale.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    scale.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    scale.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( scale );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
  }

  private void testPreserveSelectionListener( final Scale scale ) {
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( scale );
    Boolean hasListeners
      = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    SelectionListener selectionListener = new SelectionAdapter() { };
    scale.addSelectionListener( selectionListener );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( scale );
    hasListeners
      = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
  }

  private void testSelectionEvent( final Scale scale ) {
    final StringBuffer log = new StringBuffer();
    SelectionListener selectionListener = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        assertEquals( scale, event.getSource() );
        assertEquals( null, event.item );
        assertEquals( SWT.NONE, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, event.doit );
        log.append( "widgetSelected" );
      }
    };
    scale.addSelectionListener( selectionListener );
    String scaleId = WidgetUtil.getId( scale );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, scaleId );
    Fixture.readDataAndProcessAction( scale );
    assertEquals( "widgetSelected", log.toString() );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
