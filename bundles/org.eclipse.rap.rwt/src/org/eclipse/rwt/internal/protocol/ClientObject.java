/******************************************************************************* 
* Copyright (c) 2011 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rwt.internal.protocol;

import java.util.Map;

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.widgets.*;


public final class ClientObject implements IClientObject {

  private final Object target;
  private final String targetId;
  private final ProtocolMessageWriter writer;

  public ClientObject( Widget widget ) {
    target = widget;
    targetId = WidgetUtil.getId( widget );
    writer = ContextProvider.getStateInfo().getResponseWriter().getProtocolWriter();
  }

  public void create( String[] styles, Map<String, Object> properties ) {
    String parentId = getParentId();
    String type = target.getClass().getName();
    writer.appendCreate( targetId, parentId, type, styles, properties );
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
    writer.appendDestroy( targetId );
  }
  
  public void setProperty( String name, int value ) {
    writer.appendSet( targetId, name, value );
  }
  
  public void setProperty( String name, double value ) {
    writer.appendSet( targetId, name, value );
  }
  
  public void setProperty( String name, boolean value ) {
    writer.appendSet( targetId, name, value );
  }
  
  public void setProperty( String name, String value ) {
    writer.appendSet( targetId, name, value );
  }

  public void setProperty( String name, Object value ) {
    writer.appendSet( targetId, name, value );
  }

  public void addListener( String eventName ) {
    writer.appendListen( targetId, eventName, true );
  }

  public void removeListener( String eventName ) {
    writer.appendListen( targetId, eventName, false );
  }

  public void call( String methodName, Map<String, Object> properties ) {
    writer.appendDo( targetId, methodName, properties );
  }

  public void executeScript( String type, String script ) {
    writer.appendExecuteScript( targetId, type, script );
  }
   
}
