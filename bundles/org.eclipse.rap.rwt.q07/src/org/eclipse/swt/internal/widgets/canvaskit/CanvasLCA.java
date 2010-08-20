/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.canvaskit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.internal.graphics.*;
import org.eclipse.swt.widgets.*;

public final class CanvasLCA extends AbstractWidgetLCA {

  public void preserveValues( final Widget widget ) {
    ControlLCAUtil.preserveValues( ( Control )widget );
    WidgetLCAUtil.preserveCustomVariant( widget );
    WidgetLCAUtil.preserveBackgroundGradient( widget );
    WidgetLCAUtil.preserveRoundedBorder( widget );
  }

  public void readData( final Widget widget ) {
    ControlLCAUtil.processMouseEvents( ( Control )widget );
    ControlLCAUtil.processKeyEvents( ( Control )widget );
    ControlLCAUtil.processMenuDetect( ( Control )widget );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    Canvas canvas = ( Canvas )widget;
    JSWriter writer = JSWriter.getWriterFor( canvas );
    writer.newWidget( "org.eclipse.swt.widgets.Canvas" );
    ControlLCAUtil.writeStyleFlags( canvas );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    ControlLCAUtil.writeChanges( ( Control )widget );
    WidgetLCAUtil.writeBackgroundGradient( widget );
    WidgetLCAUtil.writeRoundedBorder( widget );
    WidgetLCAUtil.writeCustomVariant( widget );
    writeGCOperations( ( Canvas )widget );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  private static void writeGCOperations( final Canvas canvas )
    throws IOException
  {
    IGCAdapter adapter = ( IGCAdapter )canvas.getAdapter( IGCAdapter.class );
    GCOperation[] operations = adapter.getTrimmedGCOperations();
    if( adapter.hasDrawOperation() ) {
      GCOperationWriter operationWriter = new GCOperationWriter( canvas );
      for( int i = 0; i < operations.length; i++ ) {
        operationWriter.write( operations[ i ] );
      }
    }
    adapter.clearGCOperations();
  }
}
