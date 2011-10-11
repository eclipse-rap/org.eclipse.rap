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
import org.eclipse.swt.widgets.*;


public class TabFolderLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.TabFolder";

  private static final String PROP_SELECTION_INDEX = "selectionIndex";

  private static final int DEFAULT_SELECTION_INDEX = -1;

  public void preserveValues( Widget widget ) {
    TabFolder tabFolder = ( TabFolder )widget;
    ControlLCAUtil.preserveValues( tabFolder );
    WidgetLCAUtil.preserveCustomVariant( tabFolder );
    preserveProperty( tabFolder, PROP_SELECTION_INDEX, tabFolder.getSelectionIndex() );
  }

  public void readData( Widget widget ) {
    ControlLCAUtil.processMouseEvents( ( TabFolder )widget );
    ControlLCAUtil.processKeyEvents( ( TabFolder )widget );
    ControlLCAUtil.processMenuDetect( ( TabFolder )widget );
    WidgetLCAUtil.processHelp( widget );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    TabFolder tabFolder = ( TabFolder )widget;
    IClientObject clientObject = ClientObjectFactory.getForWidget( tabFolder );
    clientObject.create( TYPE );
    clientObject.setProperty( "parent", WidgetUtil.getId( tabFolder.getParent() ) );
    clientObject.setProperty( "style", WidgetLCAUtil.getStyles( tabFolder ) );
  }

  public void renderChanges( Widget widget ) throws IOException {
    TabFolder tabFolder = ( TabFolder )widget;
    ControlLCAUtil.renderChanges( tabFolder );
    WidgetLCAUtil.renderCustomVariant( tabFolder );
    renderProperty( tabFolder,
                    PROP_SELECTION_INDEX,
                    tabFolder.getSelectionIndex(),
                    DEFAULT_SELECTION_INDEX );
  }

  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getForWidget( widget ).destroy();
  }
}
