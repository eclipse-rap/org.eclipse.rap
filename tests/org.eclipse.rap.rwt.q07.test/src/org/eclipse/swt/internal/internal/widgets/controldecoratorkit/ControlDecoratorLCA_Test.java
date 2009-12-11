/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.internal.widgets.controldecoratorkit;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.ControlDecorator;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class ControlDecoratorLCA_Test extends TestCase {

  public void testPreserveValues() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    ControlDecorator decoration
      = new ControlDecorator( control, SWT.RIGHT, null );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( decoration );
    assertEquals( new Rectangle( 0, 0, 0, 0 ),
                  adapter.getPreserved( Props.BOUNDS ) );
    assertEquals( null, adapter.getPreserved( Props.IMAGE ) );
    assertEquals( null,
                  adapter.getPreserved( ControlDecoratorLCA.PROP_TEXT ) );
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( ControlDecoratorLCA.PROP_SHOW_HOVER ) );
    assertEquals( Boolean.FALSE,
                  adapter.getPreserved( Props.VISIBLE ) );
    String prop = ControlDecoratorLCA.PROP_SELECTION_LISTENERS;
    assertEquals( Boolean.FALSE, adapter.getPreserved( prop ) );
    Fixture.clearPreserved();
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    decoration.setImage( image );
    decoration.setText( "text" );
    decoration.setShowHover( false );
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    decoration.addSelectionListener( selectionListener );
    shell.open();
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( decoration );
    assertEquals( new Rectangle( 0, -6, 58, 12 ),
                  adapter.getPreserved( Props.BOUNDS ) );
    assertEquals( image, adapter.getPreserved( Props.IMAGE ) );
    assertEquals( "text",
                  adapter.getPreserved( ControlDecoratorLCA.PROP_TEXT ) );
    assertEquals( Boolean.FALSE,
                  adapter.getPreserved( ControlDecoratorLCA.PROP_SHOW_HOVER ) );
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( Props.VISIBLE ) );
    assertEquals( Boolean.TRUE, adapter.getPreserved( prop ) );
  }

  public void testSelectionEvent() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Composite composite = new Composite( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    final ControlDecorator decoration = new ControlDecorator( control, SWT.RIGHT, null );
    final StringBuffer log = new StringBuffer();
    SelectionListener selectionListener = new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        assertEquals( decoration, event.getSource() );
        assertEquals( null, event.item );
        assertEquals( SWT.NONE, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, event.doit );
        log.append( "widgetSelected" );
      }
      public void widgetDefaultSelected( final SelectionEvent event ) {
        assertEquals( decoration, event.getSource() );
        assertEquals( null, event.item );
        assertEquals( SWT.NONE, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, event.doit );
        log.append( "widgetDefaultSelected" );
      }
    };
    decoration.addSelectionListener( selectionListener );
    String decorId = WidgetUtil.getId( decoration );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, decorId );
    Fixture.readDataAndProcessAction( decoration );
    assertEquals( "widgetSelected", log.toString() );
    Fixture.fakeNewRequest();
    log.setLength( 0 );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED, decorId );
    Fixture.readDataAndProcessAction( decoration );
    assertEquals( "widgetDefaultSelected", log.toString() );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
