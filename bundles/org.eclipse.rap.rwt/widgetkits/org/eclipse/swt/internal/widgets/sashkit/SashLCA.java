/*******************************************************************************
 * Copyright (c) 2002, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.sashkit;

import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.createRemoteObject;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.getStyles;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.eclipse.swt.internal.events.EventLCAUtil.isListening;

import java.io.IOException;

import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Widget;


public final class SashLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Sash";
  private static final String[] ALLOWED_STYLES = new String[] {
    "HORIZONTAL", "VERTICAL", "SMOOTH", "BORDER"
  };

  private static final String PROP_SELECTION_LISTENER = "Selection";

  @Override
  public void preserveValues( Widget widget ) {
    Sash sash = ( Sash )widget;
    ControlLCAUtil.preserveValues( sash );
    WidgetLCAUtil.preserveCustomVariant( sash );
    preserveListener( sash, PROP_SELECTION_LISTENER, isListening( sash, SWT.Selection ) );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    Sash sash = ( Sash )widget;
    RemoteObject remoteObject = createRemoteObject( sash, TYPE );
    remoteObject.setHandler( new SashOperationHandler( sash ) );
    remoteObject.set( "parent", getId( sash.getParent() ) );
    remoteObject.set( "style", createJsonArray( getStyles( sash, ALLOWED_STYLES ) ) );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    Sash sash = ( Sash )widget;
    ControlLCAUtil.renderChanges( sash );
    WidgetLCAUtil.renderCustomVariant( sash );
    renderListener( sash, PROP_SELECTION_LISTENER, isListening( sash, SWT.Selection ), false );
  }

}
