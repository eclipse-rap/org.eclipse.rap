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

package org.eclipse.rap.rwt.internal.widgets.tabfolderkit;

import java.io.IOException;
import org.eclipse.rap.rwt.internal.widgets.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.JSWriter;
import org.eclipse.rap.rwt.widgets.*;


public class TabFolderLCA extends AbstractWidgetLCA {

  public void preserveValues( final Widget widget ) {
    ControlLCAUtil.preserveValues( ( Control )widget );
  }
  
  public void readData( final Widget widget ) {
  }
  
  public void processAction( final Widget widget ) {
    // TODO: [fappel] The selection event is currently only thrown in case
    //                of user action. May it also be thrown in case of changing
    //                the selectionIndex programatically?
    TabFolder folder = ( TabFolder )widget;
    TabItem item = null;
    if( folder.getSelectionIndex() != -1 ) {
      item = folder.getItem( folder.getSelectionIndex() );
    }
    ControlLCAUtil.processSelection( folder, item );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.newWidget( "qx.ui.pageview.tabview.TabView" );
  }
  
  public void renderChanges( final Widget widget ) throws IOException {
    ControlLCAUtil.writeChanges( ( Control )widget );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    // TODO [rh] preliminary: find out how to properly dispose of a TabFolder 
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }
}
