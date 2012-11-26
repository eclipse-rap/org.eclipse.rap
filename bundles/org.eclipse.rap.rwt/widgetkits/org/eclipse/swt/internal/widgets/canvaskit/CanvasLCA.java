/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.canvaskit;

import java.io.IOException;

import org.eclipse.rap.rwt.Adaptable;
import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.internal.graphics.GCOperation;
import org.eclipse.swt.internal.graphics.IGCAdapter;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;


public final class CanvasLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Canvas";
  private static final String TYPE_GC = "rwt.widgets.GC";
  private static final String[] ALLOWED_STYLES = new String[] { "NO_RADIO_GROUP", "BORDER" };

  @Override
  public void preserveValues( Widget widget ) {
    ControlLCAUtil.preserveValues( ( Control )widget );
    WidgetLCAUtil.preserveCustomVariant( widget );
    WidgetLCAUtil.preserveBackgroundGradient( widget );
    WidgetLCAUtil.preserveRoundedBorder( widget );
  }

  public void readData( Widget widget ) {
    ControlLCAUtil.processEvents( ( Control )widget );
    ControlLCAUtil.processKeyEvents( ( Control )widget );
    ControlLCAUtil.processMenuDetect( ( Control )widget );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    Canvas canvas = ( Canvas )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( canvas );
    clientObject.create( TYPE );
    clientObject.set( "parent", WidgetUtil.getId( canvas.getParent() ) );
    clientObject.set( "style", WidgetLCAUtil.getStyles( canvas, ALLOWED_STYLES ) );
    IClientObject clientObjectGC = ClientObjectFactory.getClientObject( getGC( canvas ) );
    clientObjectGC.create( TYPE_GC );
    clientObjectGC.set( "parent", WidgetUtil.getId( canvas ) );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    ControlLCAUtil.renderChanges( ( Control )widget );
    WidgetLCAUtil.renderBackgroundGradient( widget );
    WidgetLCAUtil.renderRoundedBorder( widget );
    WidgetLCAUtil.renderCustomVariant( widget );
    writeGCOperations( ( Canvas )widget );
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

  private static Adaptable getGC( Widget widget ) {
    WidgetAdapter adapter = ( WidgetAdapter )widget.getAdapter( IWidgetAdapter.class );
    return adapter.getGCForClient();
  }

}
