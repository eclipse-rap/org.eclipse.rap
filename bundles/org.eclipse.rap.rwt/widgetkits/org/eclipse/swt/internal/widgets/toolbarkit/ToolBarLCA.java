/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.toolbarkit;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Widget;


public class ToolBarLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.ToolBar";
  private static final String[] ALLOWED_STYLES = new String[] {
    "FLAT", "HORIZONTAL", "VERTICAL", "NO_RADIO_GROUP", "BORDER"
  };

  public void preserveValues( Widget widget ) {
    ToolBar toolBar = ( ToolBar )widget;
    ControlLCAUtil.preserveValues( toolBar );
    WidgetLCAUtil.preserveCustomVariant( toolBar );
  }

  public void readData( Widget widget ) {
    ControlLCAUtil.processEvents( ( Control )widget );
    ControlLCAUtil.processKeyEvents( ( Control )widget );
    ControlLCAUtil.processMenuDetect( ( Control )widget );
    WidgetLCAUtil.processHelp( widget );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    ToolBar toolBar = ( ToolBar )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( toolBar );
    clientObject.create( TYPE );
    clientObject.set( "parent", WidgetUtil.getId( toolBar.getParent() ) );
    clientObject.set( "style", WidgetLCAUtil.getStyles( toolBar, ALLOWED_STYLES ) );
  }

  public void renderChanges( Widget widget ) throws IOException {
    ToolBar toolBar = ( ToolBar )widget;
    ControlLCAUtil.renderChanges( toolBar );
    WidgetLCAUtil.renderCustomVariant( toolBar );
  }

}
