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
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;


// TODO [rh] selection semantics differ from SWT: in SWT selecting an already
//      selected item fires a widgetSelected event, in RWT it does not.
public final class TreeLCA extends AbstractWidgetLCA {

  // tree functions as defined in org.eclipse.rap.rwt.TreeUtil
  private static final String CREATE_TREE 
    = "org.eclipse.rap.rwt.TreeUtil.createTree";
  private static final String REMOVE_SELECTION_LISTENER 
    = "org.eclipse.rap.rwt.TreeUtil.removeSelectionListener";
  private static final String ADD_SELECTION_LISTENER 
    = "org.eclipse.rap.rwt.TreeUtil.addSelectionListener";
  
  public void preserveValues( final Widget widget ) {
    Tree tree  = ( Tree )widget;
    ControlLCAUtil.preserveValues( ( Control )widget );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( tree );
    adapter.preserve( Props.SELECTION_LISTENERS, 
                      Boolean.valueOf( SelectionEvent.hasListener( tree ) ) );
  }
  
  public void readData( final Widget widget ) {
  }

  public void processAction( final Widget widget ) {
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    Tree tree = ( Tree )widget;
    Object[] args = new Object[] { WidgetUtil.getId( tree ), tree.getParent() };
    writer.callStatic( CREATE_TREE, args );
  }
  
  public void renderChanges( final Widget widget ) throws IOException {
    Tree tree = ( Tree )widget;
    ControlLCAUtil.writeChanges( tree );
    updateSelectionListener( tree, SelectionEvent.hasListener( tree ) );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    // TODO [rh] preliminary: find out how to properly dispose of a TabFolder 
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }
  
  private static void updateSelectionListener( final Tree tree, 
                                               final boolean hasListeners ) 
    throws IOException 
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( tree );
    if( adapter.isInitialized() ) {
      Boolean hadListeners 
        = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
      if( hadListeners == null || Boolean.FALSE.equals( hadListeners ) ) {
        if( hasListeners ) {
          addSelectionListener( tree );
        }
      } else if( !hasListeners ) {
        removeSelectionListener( tree );
      }
    } else {
      if( hasListeners ) {
        addSelectionListener( tree );
      }
    }
  }

  private static void addSelectionListener( final Tree tree ) 
    throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( tree );
    writer.callStatic( ADD_SELECTION_LISTENER, new Object[] { tree } );
  }

  private static void removeSelectionListener( final Tree tree ) 
    throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( tree );
    writer.callStatic( REMOVE_SELECTION_LISTENER, new Object[] { tree } );
  }
}
