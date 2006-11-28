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

package org.eclipse.rap.rwt.internal.widgets.shellkit;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.rap.rwt.internal.widgets.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.engine.service.ContextProvider;


public class ShellLCA extends AbstractWidgetLCA {
  
  public void preserveValues( final Widget widget ) {
    ControlLCAUtil.preserveValues( ( Control )widget );
  }

  public void readData( final Widget widget ) { 
    HttpServletRequest request = ContextProvider.getRequest();
    String closedShellId = request.getParameter( JSConst.EVENT_SHELL_CLOSED );
    if( WidgetUtil.getAdapter( widget ).getId().equals( closedShellId ) ) {
      ( ( Shell )widget ).close();
    }
  }

  public void processAction( final Widget widget ) {
    ControlLCAUtil.readBounds( ( Composite )widget );
    // Note: call to preserveValues to avoid sending the bounds to the client,
    // the client application already knows them, because the new bounds are a 
    // result of an user action. Sending the bounds could also cause trouble in
    // case of a maximized shell, since the location portion of the
    // bounds are not correct.
    ControlLCAUtil.preserveValues( ( Control )widget );
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.newWidget( "qx.ui.window.Window" );
    ControlLCAUtil.writeResizeNotificator( widget );
    ControlLCAUtil.writeMoveNotificator( widget );
    writer.addListener( JSConst.QX_EVENT_CHANGE_VISIBILITY, 
                        JSConst.JS_SHELL_CLOSED );
    ( ( Composite )widget ).layout();
    writer.call( "open", null );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    ControlLCAUtil.writeChanges( ( Control )widget );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }
}
