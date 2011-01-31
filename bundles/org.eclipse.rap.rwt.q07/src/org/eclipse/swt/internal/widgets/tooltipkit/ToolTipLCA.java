/*******************************************************************************
 * Copyright (c) 2011 Rüdiger Herrmann and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.tooltipkit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.IToolTipAdapter;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Widget;

public final class ToolTipLCA extends AbstractWidgetLCA {
  static final String PROP_VISIBLE = "visible";
  static final String PROP_AUTO_HIDE = "autoHide";
  static final String PROP_TEXT = "text";
  static final String PROP_MESSAGE = "message";
  static final String PROP_LOCATION = "location";
  static final String PROP_SELECTION_LISTENER = "selectionListener";

  public void preserveValues( final Widget widget ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    ToolTip toolTip = ( ToolTip )widget;
    adapter.preserve( PROP_VISIBLE, Boolean.valueOf( toolTip.isVisible() ) );
    adapter.preserve( PROP_AUTO_HIDE,
                      Boolean.valueOf( toolTip.getAutoHide() ) );
    adapter.preserve( PROP_TEXT, toolTip.getText() );
    adapter.preserve( PROP_MESSAGE, toolTip.getMessage() );
    adapter.preserve( PROP_LOCATION, getLocation( toolTip ) );
    Boolean hasListener = hasSelectionListener( toolTip );
    adapter.preserve( PROP_SELECTION_LISTENER, hasListener );
    WidgetLCAUtil.preserveBackgroundGradient( widget );
    WidgetLCAUtil.preserveRoundedBorder( widget );
    WidgetLCAUtil.preserveCustomVariant( widget );
  }

  public void readData( final Widget widget ) {
    ControlLCAUtil.processSelection( widget, null, false );
    ToolTip toolTip = ( ToolTip )widget;
    readVisible( toolTip );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    ToolTip toolTip = ( ToolTip )widget;
    JSWriter writer = JSWriter.getWriterFor( toolTip );
    Object[] args = new Object[] { getImage( toolTip )};
    writer.newWidget( "org.eclipse.swt.widgets.ToolTip", args );
    WidgetLCAUtil.writeStyleFlag( toolTip, SWT.BALLOON, "BAlLOON" );
    WidgetLCAUtil.writeStyleFlag( toolTip, SWT.ICON_ERROR, "ICON_ERROR" );
    WidgetLCAUtil.writeStyleFlag( toolTip, SWT.ICON_WARNING, "ICON_WARNING" );
    WidgetLCAUtil.writeStyleFlag( toolTip, 
                                  SWT.ICON_INFORMATION, 
                                  "ICON_INFORMATION" );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    ToolTip toolTip = ( ToolTip )widget;
    WidgetLCAUtil.writeBackgroundGradient( widget );
    WidgetLCAUtil.writeRoundedBorder( widget );
    WidgetLCAUtil.writeCustomVariant( widget );
    writeText( toolTip );
    writeMessage( toolTip );
    writeLocation( toolTip );
    writeAutoHide( toolTip );
    writeSelectionListener( toolTip );
    // Order is relevant here: writeVisible must be called after all other
    // properties are set
    writeVisible( toolTip );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  private static void readVisible( ToolTip toolTip ) {
    String value = WidgetLCAUtil.readPropertyValue( toolTip, "visible" );
    if( value != null ) {
      toolTip.setVisible( new Boolean( value ).booleanValue() );
    }
  }

  static void writeText( final ToolTip toolTip ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( toolTip );
    String text = WidgetLCAUtil.escapeText( toolTip.getText(), false );
    writer.set( PROP_TEXT, "text", text, "" );
  }
  
  static void writeMessage( final ToolTip toolTip ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( toolTip );
    String message = WidgetLCAUtil.escapeText( toolTip.getMessage(), false );
    message = WidgetLCAUtil.replaceNewLines( message, "<br/>" );
    writer.set( PROP_MESSAGE, "message", message, "" );
  }
  
  private static void writeVisible( final ToolTip toolTip ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( toolTip );
    Boolean visible = Boolean.valueOf( toolTip.isVisible() );
    writer.set( PROP_VISIBLE, "visible", visible, Boolean.FALSE );
  }

  private static void writeLocation( final ToolTip toolTip )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( toolTip );
    Point location = getLocation( toolTip );
    if( WidgetLCAUtil.hasChanged( toolTip, PROP_LOCATION, location ) ) {
      Object[] args = new Object[] {
        new Integer( location.x ),
        new Integer( location.y )
      };
      writer.call( "setLocation", args );
    }
  }

  private static void writeAutoHide( final ToolTip toolTip ) throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( toolTip );
    Boolean autoHide = Boolean.valueOf( toolTip.getAutoHide() );
    writer.set( PROP_AUTO_HIDE, "hideAfterTimeout", autoHide, Boolean.FALSE );
  }

  private static void writeSelectionListener( ToolTip toolTip ) 
    throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( toolTip );
    Boolean hasListener = hasSelectionListener( toolTip );
    writer.set( PROP_SELECTION_LISTENER, 
                "hasSelectionListener", 
                hasListener, 
                Boolean.FALSE );
  }

  private static Boolean hasSelectionListener( ToolTip toolTip ) {
    return Boolean.valueOf( SelectionEvent.hasListener( toolTip ) );
  }

  private static Point getLocation( final ToolTip toolTip ) {
    Object adapter = toolTip.getAdapter( IToolTipAdapter.class );
    IToolTipAdapter toolTipAdapter = ( IToolTipAdapter )adapter;
    return toolTipAdapter.getLocation();
  }

  static String getImage( ToolTip toolTip ) {
    String result = null;
    if( ( toolTip.getStyle() & SWT.ICON_ERROR ) != 0 ) {
      result = "error";
    } if( ( toolTip.getStyle() & SWT.ICON_WARNING ) != 0 ) {
      result = "warning";
    } if( ( toolTip.getStyle() & SWT.ICON_INFORMATION ) != 0 ) {
      result = "information";
    }
    return result;
  }
}
