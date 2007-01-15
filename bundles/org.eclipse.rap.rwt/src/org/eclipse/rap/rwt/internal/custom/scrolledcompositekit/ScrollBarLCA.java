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
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.internal.widgets.ControlLCAUtil;
import org.eclipse.rap.rwt.internal.widgets.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.JSWriter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.widgets.ScrollBar;
import org.eclipse.rap.rwt.widgets.Widget;

// TODO [rh] preliminary; make it a real LCA and move it to its own package?
final class ScrollBarLCA {

  private static final Integer ZERO = new Integer( 0 );
  
  public static final String PROP_SELECTION = "selection";
  public static final String PROP_MAXIMUM = "maximum";
  public static final String PROP_VISIBLE = "visible";
  public static final String PROP_ENABLED = "enabled";
  public static final String PROP_HAS_LISTENER = "hasListener";


  public void preserveValues( final Widget widget ) {
    ScrollBar scrollBar = ( ScrollBar )widget;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( scrollBar );
    Integer selection = new Integer( scrollBar.getSelection() );
    adapter.preserve( PROP_SELECTION, selection );
    Integer maximum = new Integer( scrollBar.getMaximum() );
    adapter.preserve( PROP_MAXIMUM, maximum );
    Boolean visible = Boolean.valueOf( scrollBar.getVisible() );
    adapter.preserve( PROP_VISIBLE, visible );
    Boolean enabled = Boolean.valueOf( scrollBar.getEnabled() );
    adapter.preserve( PROP_ENABLED, enabled );
    boolean hasListener = SelectionEvent.hasListener( scrollBar );
    adapter.preserve( PROP_HAS_LISTENER, new Boolean( hasListener ) );
  }

  public void readData( final Widget widget ) {
    ScrollBar scrollBar = ( ScrollBar )widget;
    String value = WidgetUtil.readPropertyValue( widget, "selection" );
    if( value != null ) {
      scrollBar.setSelection( Integer.parseInt( value ) );
    }
    // TODO [rh] check whether bounds are sent in SWT
    ControlLCAUtil.processSelection( widget, null, false );
  }

  public void renderInitialization( final ScrollBar scrollBar ) 
    throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( scrollBar );
    boolean horizontal = ( scrollBar.getStyle() & RWT.H_SCROLL ) != 0;
    Object[] args = new Object[] { Boolean.valueOf( horizontal ) };
    writer.newWidget( "org.eclipse.rap.rwt.widgets.ScrollBar", args );
  }
  
  public void renderChanges( final Widget widget ) throws IOException {
    ScrollBar scrollBar = ( ScrollBar )widget;
    JSWriter writer = JSWriter.getWriterFor( scrollBar );
    Object newValue = new Integer( scrollBar.getMaximum() );
    writer.set( PROP_MAXIMUM, "maximum", newValue, ZERO );
    newValue = new Integer( scrollBar.getSelection() );
    writer.set( PROP_SELECTION, "selection", newValue, ZERO );
    newValue = Boolean.valueOf( scrollBar.getVisible() );
    writer.set( PROP_VISIBLE, "visibility", newValue, Boolean.TRUE );
    newValue = Boolean.valueOf( scrollBar.getEnabled() );
    writer.set( PROP_ENABLED, "enabled", newValue, Boolean.TRUE );
    newValue = Boolean.valueOf( SelectionEvent.hasListener( scrollBar ) );
    writer.set( PROP_HAS_LISTENER, "hasListener", newValue, Boolean.FALSE );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }
}
