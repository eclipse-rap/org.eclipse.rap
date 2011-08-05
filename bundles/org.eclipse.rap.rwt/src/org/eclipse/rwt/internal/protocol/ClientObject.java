/*******************************************************************************
* Copyright (c) 2011 EclipseSource and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rwt.internal.protocol;

import java.util.Map;

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.widgets.*;


public final class ClientObject implements IClientObject {

  private final Object target;
  private final String targetId;

  public ClientObject( Widget widget ) {
    target = widget;
    targetId = WidgetUtil.getId( widget );
  }

  public void create() {
    String parentId = getParentId();
    String type = target.getClass().getName();
    getWriter().appendCreate( targetId, parentId, type );
  }

  private String getParentId() {
    String parentId = null;
    if( target instanceof Control ) {
      Composite parent = ( ( Control )target ).getParent();
      if( parent != null ) {
        parentId = WidgetUtil.getId( parent );
      }
    }
    return parentId;
  }

  public void destroy() {
    getWriter().appendDestroy( targetId );
  }

  public void setProperty( String name, int value ) {
    getWriter().appendSet( targetId, name, value );
  }

  public void setProperty( String name, double value ) {
    getWriter().appendSet( targetId, name, value );
  }

  public void setProperty( String name, boolean value ) {
    getWriter().appendSet( targetId, name, value );
  }

  public void setProperty( String name, String value ) {
    getWriter().appendSet( targetId, name, value );
  }
  
  public void setProperty( String name, int[] value ) {
    getWriter().appendSet( targetId, name, value );
  }

  public void setProperty( String name, Object value ) {
    getWriter().appendSet( targetId, name, value );
  }

  public void addListener( String eventName ) {
    getWriter().appendListen( targetId, eventName, true );
  }

  public void removeListener( String eventName ) {
    getWriter().appendListen( targetId, eventName, false );
  }

  public void call( String method, Map<String, Object> properties ) {
    getWriter().appendCall( targetId, method, properties );
  }

  public void executeScript( String type, String script ) {
    getWriter().appendExecuteScript( targetId, type, script );
  }

  private static ProtocolMessageWriter getWriter() {
    return ContextProvider.getStateInfo().getResponseWriter().getProtocolWriter();
  }

}
