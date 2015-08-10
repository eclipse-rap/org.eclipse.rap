/*******************************************************************************
 * Copyright (c) 2010, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.canvaskit;

import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.createRemoteObject;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.getStyles;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderClientListeners;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.swt.internal.widgets.canvaskit.GCOperationWriter.getGcId;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.internal.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.swt.internal.graphics.GCOperation;
import org.eclipse.swt.internal.graphics.IGCAdapter;
import org.eclipse.swt.widgets.Canvas;


public final class CanvasLCA extends WidgetLCA<Canvas> {

  private static final String TYPE = "rwt.widgets.Canvas";
  private static final String TYPE_GC = "rwt.widgets.GC";
  private static final String[] ALLOWED_STYLES = { "NO_RADIO_GROUP", "BORDER" };
  private static final String PROP_CLIENT_AREA = "clientArea";

  @Override
  public void preserveValues( Canvas canvas ) {
    WidgetLCAUtil.preserveCustomVariant( canvas );
    WidgetLCAUtil.preserveBackgroundGradient( canvas );
    WidgetLCAUtil.preserveRoundedBorder( canvas );
    WidgetLCAUtil.preserveProperty( canvas, PROP_CLIENT_AREA, canvas.getClientArea() );
  }

  @Override
  public void renderInitialization( Canvas canvas ) throws IOException {
    RemoteObject remoteObject = createRemoteObject( canvas, TYPE );
    remoteObject.setHandler( new CanvasOperationHandler( canvas ) );
    remoteObject.set( "parent", getId( canvas.getParent() ) );
    remoteObject.set( "style", createJsonArray( getStyles( canvas, ALLOWED_STYLES ) ) );
    RemoteObject remoteObjectForGC = createRemoteObject( getGcId( canvas ), TYPE_GC );
    remoteObjectForGC.set( "parent", WidgetUtil.getId( canvas ) );
  }

  @Override
  public void renderChanges( Canvas canvas ) throws IOException {
    ControlLCAUtil.renderChanges( canvas );
    WidgetLCAUtil.renderBackgroundGradient( canvas );
    WidgetLCAUtil.renderRoundedBorder( canvas );
    WidgetLCAUtil.renderCustomVariant( canvas );
    renderClientArea( canvas );
    writeGCOperations( canvas );
    renderClientListeners( canvas );
  }

  private static void writeGCOperations( Canvas canvas ) {
    IGCAdapter adapter = canvas.getAdapter( IGCAdapter.class );
    GCOperation[] operations = adapter.getTrimmedGCOperations();
    if( operations.length > 0 || adapter.getForceRedraw() ) {
      GCOperationWriter operationWriter = new GCOperationWriter( canvas );
      operationWriter.initialize();
      for( int i = 0; i < operations.length; i++ ) {
        operationWriter.write( operations[ i ] );
      }
      operationWriter.render();
    }
    adapter.clearGCOperations();
    adapter.setForceRedraw( false );
  }

  public static void renderClientArea( Canvas canvas ) {
    renderProperty( canvas, PROP_CLIENT_AREA, canvas.getClientArea(), null );
  }

}
