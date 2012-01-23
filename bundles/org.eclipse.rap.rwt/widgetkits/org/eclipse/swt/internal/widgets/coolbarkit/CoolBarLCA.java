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
package org.eclipse.swt.internal.widgets.coolbarkit;

import java.io.IOException;

import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Widget;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;


public class CoolBarLCA extends AbstractWidgetLCA {

  public static final String TYPE = "rwt.widgets.CoolBar";
  private static final String[] ALLOWED_STYLES = new String[] {
    "FLAT", "HORIZONTAL", "VERTICAL", "NO_RADIO_GROUP", "BORDER"
  };

  public static final String PROP_LOCKED = "locked";

  public void preserveValues( Widget widget ) {
    CoolBar coolBar = ( CoolBar )widget;
    ControlLCAUtil.preserveValues( coolBar );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( coolBar );
    adapter.preserve( PROP_LOCKED, Boolean.valueOf( coolBar.getLocked() ) );
    WidgetLCAUtil.preserveCustomVariant( coolBar );
  }

  public void readData( Widget widget ) {
    Control coolBar = ( Control )widget;
    ControlLCAUtil.processMouseEvents( coolBar );
    ControlLCAUtil.processKeyEvents( coolBar );
    ControlLCAUtil.processMenuDetect( coolBar );
    WidgetLCAUtil.processHelp( coolBar );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    CoolBar coolbar = ( CoolBar )widget;
    IClientObject clientObject = ClientObjectFactory.getForWidget( coolbar );
    clientObject.create( TYPE );
    clientObject.set( "parent", WidgetUtil.getId( coolbar.getParent() ) );
    clientObject.set( "style", WidgetLCAUtil.getStyles( coolbar, ALLOWED_STYLES ) );
  }

  public void renderChanges( Widget widget ) throws IOException {
    CoolBar coolBar = ( CoolBar )widget;
    ControlLCAUtil.renderChanges( coolBar );
    renderProperty( coolBar, PROP_LOCKED ,coolBar.getLocked(), false );
    WidgetLCAUtil.renderCustomVariant( coolBar );
}

  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getForWidget( widget ).destroy();
  }
}
