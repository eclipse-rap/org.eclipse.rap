/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.compositekit;

import java.io.IOException;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;


public class CompositeLCA extends AbstractWidgetLCA {
  
  public void preserveValues( final Widget widget ) {
    ControlLCAUtil.preserveValues( ( Control )widget );
  }
  
  public void readData( final Widget widget ) {
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.newWidget( "qx.ui.layout.CanvasLayout" );
    writer.set( "appearance", "composite" );
    writer.set( "overflow", "hidden" );
    writer.set( "hideFocus", true );
    ControlLCAUtil.writeStyleFlags( widget );
  }
  
  public void renderChanges( final Widget widget ) throws IOException {
    ControlLCAUtil.writeChanges( ( Control )widget );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }
}
