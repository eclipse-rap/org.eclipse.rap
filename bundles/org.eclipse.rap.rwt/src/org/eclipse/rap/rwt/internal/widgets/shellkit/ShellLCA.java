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
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.engine.service.ContextProvider;


public class ShellLCA extends AbstractWidgetLCA {
  
  private static final String PROP_ACTIVE_CONTROL = "activeControl";

  public void preserveValues( final Widget widget ) {
    ControlLCAUtil.preserveValues( ( Control )widget );
    Shell shell = ( Shell )widget;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( shell );
    adapter.preserve( PROP_ACTIVE_CONTROL, getActiveControl( shell ) );
  }

  public void readData( final Widget widget ) { 
    Shell shell = ( Shell )widget;
    ControlLCAUtil.readBounds( shell );
    if( WidgetUtil.wasEventSent( shell, JSConst.EVENT_SHELL_CLOSED ) ) {
      shell.close();
    }
    processActivate( shell );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.newWidget( "org.eclipse.rap.rwt.widgets.Shell" );
    ControlLCAUtil.writeResizeNotificator( widget );
    ControlLCAUtil.writeMoveNotificator( widget );
    writer.addListener( JSConst.QX_EVENT_CHANGE_VISIBILITY, 
                        JSConst.JS_SHELL_CLOSED );
    writer.call( "open", null );
  }
  
  public void renderChanges( final Widget widget ) throws IOException {
    Shell shell = ( Shell )widget;
    ControlLCAUtil.writeChanges( shell );
    writeActiveControl( shell );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  /////////////////////////////////////////////////////
  // Methods to handle activeControl and ActivateEvents
  
  private static void writeActiveControl( Shell shell ) throws IOException {
    Control activeControl = getActiveControl( shell );
    String prop = PROP_ACTIVE_CONTROL;
    if( WidgetUtil.hasChanged( shell, prop, activeControl, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( shell );
      writer.set( "activeControl", new Object[] { activeControl } );
    }
  }
  
  // TODO [rh] is this safe for multiple shells?
  private static void processActivate( final Shell shell ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String widgetId = request.getParameter( JSConst.EVENT_WIDGET_ACTIVATED );
    if( widgetId != null ) {
      Widget widget = WidgetUtil.find( shell, widgetId );
      if( widget != null ) {
        setActiveControl( shell, widget );
      }
    } else {
      String activeControlId 
      = WidgetUtil.readPropertyValue( shell, "activeControl" );
      Widget widget = WidgetUtil.find( shell, activeControlId );
      if( widget != null ) {
        setActiveControl( shell, widget );
      }
    }
  }

  private static Control getActiveControl( final Shell shell ) {
    Object adapter = shell.getAdapter( IShellAdapter.class );
    IShellAdapter shellAdapter = ( IShellAdapter )adapter;
    Control activeControl = shellAdapter.getActiveControl();
    return activeControl;
  }

  private static void setActiveControl( final Shell shell, final Widget widget ) 
  {
    Object adapter = shell.getAdapter( IShellAdapter.class );
    IShellAdapter shellAdapter = ( IShellAdapter )adapter;
    shellAdapter.setActiveControl( ( Control )widget );
  }
}
