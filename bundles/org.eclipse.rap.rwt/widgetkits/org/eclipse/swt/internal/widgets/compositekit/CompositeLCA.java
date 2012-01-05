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
package org.eclipse.swt.internal.widgets.compositekit;

import java.io.IOException;

import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.widgets.*;


public class CompositeLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Composite";
  private static final String[] ALLOWED_STYLES = new String[] { "NO_RADIO_GROUP", "BORDER" };

  public void preserveValues( Widget widget ) {
    ControlLCAUtil.preserveValues( ( Control )widget );
    WidgetLCAUtil.preserveCustomVariant( widget );
    WidgetLCAUtil.preserveBackgroundGradient( widget );
    WidgetLCAUtil.preserveRoundedBorder( widget );
  }

  public void readData( Widget widget ) {
    ControlLCAUtil.processMouseEvents( ( Control )widget );
    ControlLCAUtil.processKeyEvents( ( Control )widget );
    ControlLCAUtil.processMenuDetect( ( Control )widget );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    Composite composite = ( Composite )widget;
    IClientObject clientObject = ClientObjectFactory.getForWidget( composite );
    clientObject.create( TYPE );
    clientObject.setProperty( "parent", WidgetUtil.getId( composite.getParent() ) );
    clientObject.setProperty( "style", WidgetLCAUtil.getStyles( composite, ALLOWED_STYLES ) );
  }

  public void renderChanges( Widget widget ) throws IOException {
    ControlLCAUtil.renderChanges( ( Control )widget );
    WidgetLCAUtil.renderBackgroundGradient( widget );
    WidgetLCAUtil.renderRoundedBorder( widget );
    WidgetLCAUtil.renderCustomVariant( widget );
  }

  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getForWidget( widget ).destroy();
  }

}
