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

package org.eclipse.swt.internal.widgets.groupkit;

import java.io.IOException;

import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Widget;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;


public class GroupLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Group";

  private static final String PROP_TEXT = "text";

  public void preserveValues( final Widget widget ) {
    Group group = ( Group )widget;
    ControlLCAUtil.preserveValues( group );
    WidgetLCAUtil.preserveCustomVariant( group );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( group );
    adapter.preserve( PROP_TEXT, group.getText() );
  }

  public void readData( Widget widget ) {
    ControlLCAUtil.processMouseEvents( ( Group )widget );
    ControlLCAUtil.processKeyEvents( ( Group )widget );
    ControlLCAUtil.processMenuDetect( ( Group )widget );
    WidgetLCAUtil.processHelp( widget );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    Group group = ( Group )widget;
    IClientObject clientObject = ClientObjectFactory.getForWidget( group );
    clientObject.create( TYPE );
    clientObject.setProperty( "parent", WidgetUtil.getId( group.getParent() ) );
    clientObject.setProperty( "style", WidgetLCAUtil.getStyles( group ) );
  }

  public void renderChanges( Widget widget ) throws IOException {
    Group group = ( Group )widget;
    ControlLCAUtil.renderChanges( group );
    WidgetLCAUtil.renderCustomVariant( group );
    renderProperty( group, PROP_TEXT, group.getText(), "" );
  }

  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getForWidget( widget ).destroy();
  }
}
