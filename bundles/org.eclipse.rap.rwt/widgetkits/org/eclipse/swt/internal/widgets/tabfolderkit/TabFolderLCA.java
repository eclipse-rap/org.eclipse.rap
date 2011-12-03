/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.tabfolderkit;

import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;

import java.io.IOException;

import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;


public class TabFolderLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.TabFolder";

  private static final String PROP_SELECTION = "selection";

  public void preserveValues( Widget widget ) {
    TabFolder folder = ( TabFolder )widget;
    ControlLCAUtil.preserveValues( folder );
    WidgetLCAUtil.preserveCustomVariant( folder );
    preserveProperty( folder, PROP_SELECTION, getSelection( folder ) );
  }

  public void readData( Widget widget ) {
    TabFolder folder = ( TabFolder )widget;
    ControlLCAUtil.processMouseEvents( folder );
    ControlLCAUtil.processKeyEvents( folder );
    ControlLCAUtil.processMenuDetect( folder );
    WidgetLCAUtil.processHelp( widget );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    TabFolder folder = ( TabFolder )widget;
    IClientObject clientObject = ClientObjectFactory.getForWidget( folder );
    clientObject.create( TYPE );
    clientObject.setProperty( "parent", WidgetUtil.getId( folder.getParent() ) );
    clientObject.setProperty( "style", WidgetLCAUtil.getStyles( folder ) );
  }

  public void renderChanges( Widget widget ) throws IOException {
    TabFolder folder = ( TabFolder )widget;
    ControlLCAUtil.renderChanges( folder );
    WidgetLCAUtil.renderCustomVariant( folder );
    renderProperty( folder, PROP_SELECTION, getSelection( folder ), null );
  }

  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getForWidget( widget ).destroy();
  }

  //////////////////
  // Helping methods

  private static String getSelection( TabFolder folder ) {
    String selection = null;
    int selectionIndex = folder.getSelectionIndex();
    if( selectionIndex != -1 ) {
      selection = WidgetUtil.getId( folder.getItem( selectionIndex ) );
    }
    return selection;
  }

  // TODO: Remove when all widgets are migrated to the protocol
  public Rectangle adjustCoordinates( Widget widget, Rectangle newBounds ) {
    return new Rectangle( 0, 0, newBounds.width, newBounds.height );
  }
}
