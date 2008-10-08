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
package org.eclipse.swt.internal.widgets.expandbarkit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IExpandBarAdapter;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.Widget;

public final class ExpandBarLCA extends AbstractWidgetLCA {

  // Property names for preserveValues
  public static final String PROP_SHOW_VSCROLLBAR = "showVScrollbar";
  public static final String PROP_BOTTOM_SPACING_BOUNDS = "bottomSpacingBounds";

  public void preserveValues( final Widget widget ) {
    ExpandBar expandBar = ( ExpandBar )widget;
    ControlLCAUtil.preserveValues( expandBar );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    IExpandBarAdapter expandBarAdapter = getExpandBarAdapter( expandBar );
    adapter.preserve( PROP_SHOW_VSCROLLBAR,
                      Boolean.valueOf( expandBarAdapter.isVScrollbarVisible() ) );
    adapter.preserve( PROP_BOTTOM_SPACING_BOUNDS,
                      expandBarAdapter.getBottomSpacingBounds() );
  }

  public void readData( final Widget widget ) {
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    ExpandBar expandBar = ( ExpandBar )widget;
    JSWriter writer = JSWriter.getWriterFor( expandBar );
    String style = "";
    if( ( expandBar.getStyle() & SWT.V_SCROLL ) != 0 ) {
      style = "v_scroll";
    }
    Object[] args = new Object[]{
      style
    };
    writer.newWidget( "org.eclipse.swt.widgets.ExpandBar", args );
    WidgetLCAUtil.writeCustomVariant( widget );
    ControlLCAUtil.writeStyleFlags( expandBar );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    ExpandBar expandBar = ( ExpandBar )widget;
    ControlLCAUtil.writeChanges( expandBar );
    writeShowVScrollbar( expandBar );
    writeBottomSpacing( expandBar );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  public void createResetHandlerCalls( final String typePoolId )
    throws IOException
  {
  }

  public String getTypePoolId( final Widget widget ) {
    return null;
  }

  public static IExpandBarAdapter getExpandBarAdapter( final ExpandBar bar ) {
    return ( IExpandBarAdapter )bar.getAdapter( IExpandBarAdapter.class );
  }

  //////////////////////////////////////
  // Helping methods to write properties
  private static void writeShowVScrollbar( final ExpandBar bar )
    throws IOException
  {
    IExpandBarAdapter expandBarAdapter = getExpandBarAdapter( bar );
    Boolean newValue = Boolean.valueOf( expandBarAdapter.isVScrollbarVisible() );
    if( WidgetLCAUtil.hasChanged( bar, PROP_SHOW_VSCROLLBAR, newValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( bar );
      writer.call( "showVScrollbar", new Object[]{
        newValue
      } );
    }
  }

  private static void writeBottomSpacing( final ExpandBar bar )
    throws IOException
  {
    IExpandBarAdapter expandBarAdapter = getExpandBarAdapter( bar );
    Rectangle bottomSpacingBounds = expandBarAdapter.getBottomSpacingBounds();
    if( WidgetLCAUtil.hasChanged( bar,
                                  PROP_BOTTOM_SPACING_BOUNDS,
                                  bottomSpacingBounds ) )
    {
      JSWriter writer = JSWriter.getWriterFor( bar );
      writer.call( "setBottomSpacingBounds", new Object[]{
        new Integer( bottomSpacingBounds.x ),
        new Integer( bottomSpacingBounds.y ),
        new Integer( bottomSpacingBounds.width ),
        new Integer( bottomSpacingBounds.height )
      } );
    }
  }
}
