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

package org.eclipse.rap.rwt.internal.widgets.treekit;

import java.io.IOException;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.events.TreeEvent;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;


// TODO [rh] selection semantics differ from SWT: in SWT selecting an already
//      selected item fires a widgetSelected event, in RWT it does not.
public final class TreeLCA extends AbstractWidgetLCA {

  // Property names used by preserve mechanism
  private static final String PROP_SELECTION_LISTENERS = "selectionListeners";
  private static final String PROP_TREE_LISTENERS = "treeListeners";
  
  public void preserveValues( final Widget widget ) {
    Tree tree  = ( Tree )widget;
    ControlLCAUtil.preserveValues( ( Control )widget );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( tree );
    adapter.preserve( PROP_SELECTION_LISTENERS, 
                      Boolean.valueOf( SelectionEvent.hasListener( tree ) ) );
    adapter.preserve( PROP_TREE_LISTENERS, 
                      Boolean.valueOf( TreeEvent.hasListener( tree ) ) );
  }
  
  public void readData( final Widget widget ) {
    Tree tree = ( Tree )widget;
    String value = WidgetLCAUtil.readPropertyValue( widget, "selection" );
    if( value != null ) {
      String[] values = value.split( "," );
      TreeItem[] selectedItems = new TreeItem[ values.length ];
      for( int i = 0; i < values.length; i++ ) {
        selectedItems[ i ] = ( TreeItem )WidgetUtil.find( tree, values[ i ] );
      }
      tree.setSelection( selectedItems );
    }
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    StringBuffer style = new StringBuffer();
    if( ( widget.getStyle() & RWT.MULTI ) != 0 ) {
      style.append( "multi|" );
    }
    if( ( widget.getStyle() & RWT.CHECK ) != 0 ) {
      style.append( "check|" );
    }
    writer.newWidget( "org.eclipse.rap.rwt.widgets.Tree", 
                      new Object[] { style.toString() } );
    ControlLCAUtil.writeStyleFlags( widget );
  }
  
  public void renderChanges( final Widget widget ) throws IOException {
    Tree tree = ( Tree )widget;
    ControlLCAUtil.writeChanges( tree );
    updateSelectionListener( tree );
    updateTreeListener( tree );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  /////////////////////////////////////////////////////////////
  // Helping methods to write JavaScript for changed properties

  private static void updateSelectionListener( final Tree tree ) 
    throws IOException 
  {
    Boolean newValue = Boolean.valueOf( SelectionEvent.hasListener( tree ) );
    String prop = PROP_SELECTION_LISTENERS;
    if( WidgetLCAUtil.hasChanged( tree, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( tree );
      writer.set( "widgetSelectedListeners", newValue );
    }
  }

  private static void updateTreeListener( final Tree tree ) 
    throws IOException 
  {
    Boolean newValue = Boolean.valueOf( TreeEvent.hasListener( tree ) );
    String prop = PROP_TREE_LISTENERS;
    if( WidgetLCAUtil.hasChanged( tree, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( tree );
      writer.set( "treeListeners", newValue );
    }
  }
}
