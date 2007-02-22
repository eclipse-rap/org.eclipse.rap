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

package org.eclipse.rap.rwt.internal.custom.scrolledcompositekit;

import java.io.IOException;
import org.eclipse.rap.rwt.custom.ScrolledComposite;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;


public final class ScrolledCompositeLCA extends AbstractWidgetLCA {
  
  private static final Integer ZERO = new Integer( 0 );

  // Request parameter names
  private static final String PARAM_H_BAR_SELECTION 
    = "horizontalBar.selection"; 
  private static final String PARAM_V_BAR_SELECTION 
    = "verticalBar.selection"; 
  
  // Property names for preserve value mechanism
  private static final String PROP_BOUNDS = "clientArea";
  private static final String PROP_OVERFLOW = "overflow";
  private static final String PROP_H_BAR_SELECTION = "hBarSelection";
  private static final String PROP_V_BAR_SELECTION = "vBarSelection";

  public void preserveValues( final Widget widget ) {
    ScrolledComposite composite = ( ScrolledComposite )widget;
    ControlLCAUtil.preserveValues( composite );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( composite );
    adapter.preserve( PROP_BOUNDS, composite.getBounds() );
    adapter.preserve( PROP_OVERFLOW, getOverflow( composite ) );
    adapter.preserve( PROP_H_BAR_SELECTION, 
                      getBarSelection( composite.getHorizontalBar() ) );
    adapter.preserve( PROP_V_BAR_SELECTION, 
                      getBarSelection( composite.getVerticalBar() ) );
  }

  public void readData( final Widget widget ) {
    ScrolledComposite composite = ( ScrolledComposite )widget;
    String value 
      = WidgetLCAUtil.readPropertyValue( widget, PARAM_H_BAR_SELECTION );
    if( value != null && composite.getHorizontalBar() != null ) {
      composite.getHorizontalBar().setSelection( Integer.parseInt( value ) );
    }
    value = WidgetLCAUtil.readPropertyValue( widget, PARAM_V_BAR_SELECTION );
    if( value != null && composite.getVerticalBar() != null ) {
      composite.getVerticalBar().setSelection( Integer.parseInt( value ) );
    }
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.newWidget( "org.eclipse.rap.rwt.custom.ScrolledComposite" );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    ScrolledComposite composite = ( ScrolledComposite )widget;
    ControlLCAUtil.writeChanges( composite );
    writeClipBounds( composite );
    writeScrollBars( composite );
    // TODO [rh] initial positioning of the client-side scroll bar does not work 
    writeHBarSelection( composite );
    writeVBarSelection( composite );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    ScrolledComposite composite = ( ScrolledComposite )widget;
    JSWriter writer = JSWriter.getWriterFor( composite );
    writer.dispose();
  }

  ///////////////////////////////////
  // Helping methods to write changes

  private static void writeScrollBars( final ScrolledComposite composite ) 
    throws IOException 
  {
    String overflow = getOverflow( composite );
    JSWriter writer = JSWriter.getWriterFor( composite );
    writer.set( PROP_OVERFLOW, "overflow", overflow, null );
  }

  private static void writeHBarSelection( final ScrolledComposite composite )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( composite );
    Integer selection = getBarSelection( composite.getHorizontalBar() );
    writer.set( PROP_H_BAR_SELECTION, "hBarSelection", selection, ZERO );
  }

  private static void writeVBarSelection( final ScrolledComposite composite )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( composite );
    Integer selection = getBarSelection( composite.getVerticalBar() );
    writer.set( PROP_V_BAR_SELECTION, "vBarSelection", selection, ZERO );
  }

  private static void writeClipBounds( final ScrolledComposite composite ) 
    throws IOException 
  {
    Rectangle bounds = composite.getBounds();
    if( WidgetLCAUtil.hasChanged( composite, PROP_BOUNDS, bounds, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( composite );
      writer.set( "clipWidth", bounds.width );
      writer.set( "clipHeight", bounds.height );
    }
  }
  
  private static String getOverflow( final ScrolledComposite composite ) {
    String result;
    ScrollBar horizontalBar = composite.getHorizontalBar();
    boolean scrollX = horizontalBar != null && horizontalBar.getVisible();
    ScrollBar verticalBar = composite.getVerticalBar();
    boolean scrollY = verticalBar != null && verticalBar.getVisible();
    if( scrollX && scrollY ) {
      result = "scroll";
    } else if( scrollX ) {
      result = "scrollX";
    } else if( scrollY ) {
      result = "scrollY";
    } else {
      result = "hidden";
    }
    return result;
  }
  
  ///////////////////////////////////////////////////////////////////////////
  // Helping methods to obtain scroll bar properties
  // These methods return a constant placeholder value in case the scroll bar
  // is null or invisible
  
  private static Integer getBarSelection( final ScrollBar scrollBar ) {
    Integer result;
    if( scrollBar != null && scrollBar.getVisible() ) {
      result = new Integer( scrollBar.getSelection() );
    } else {
      result = new Integer( -1 );
    }
    return result;
  }
}
