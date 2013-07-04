/*******************************************************************************
* Copyright (c) 2011, 2013 EclipseSource and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleRemoteObject;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceStore;
import org.eclipse.rap.rwt.internal.util.ParamCheck;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;


/**
 * The methods of this class are used to create remote objects, only for use in LCAs.
 *
 * @see RemoteObject
 */
public final class RemoteObjectFactory {

  private static final String CLIENT_OBJECT_MAP_KEY = "synchronizerMapKey";

  public static RemoteObject getRemoteObject( Widget widget ) {
    ParamCheck.notNull( widget, "widget" );
    return getForId( WidgetUtil.getId( widget ) );
  }

  public static RemoteObject getRemoteObject( Display display ) {
    ParamCheck.notNull( display, "display" );
    return getForId( DisplayUtil.getId( display ) );
  }

  public static RemoteObject getRemoteObject( String id ) {
    ParamCheck.notNull( id, "id" );
    return getForId( id );
  }

  public static RemoteObject createRemoteObject( Widget widget, String type ) {
    ParamCheck.notNull( widget, "widget" );
    ParamCheck.notNull( type, "type" );
    return createForId( WidgetUtil.getId( widget ), type );
  }

  public static RemoteObject createRemoteObject( String id, String type ) {
    ParamCheck.notNull( id, "id" );
    ParamCheck.notNull( type, "type" );
    return createForId( id, type );
  }

  private static RemoteObject createForId( String id, String type ) {
    Map<String, RemoteObject> map = getRemoteObjectMap();
    if( map.containsKey( id ) ) {
      throw new IllegalStateException( "Client object already created for id: " + id );
    }
    LifeCycleRemoteObject remoteObject = new LifeCycleRemoteObject( id, type );
    map.put( id, remoteObject );
    return remoteObject;
  }

  private static RemoteObject getForId( String id ) {
    Map<String, RemoteObject> map = getRemoteObjectMap();
    RemoteObject remoteObject = map.get( id );
    if( remoteObject == null ) {
      remoteObject = new LifeCycleRemoteObject( id, null );
      map.put( id, remoteObject );
    }
    return remoteObject;
  }

  @SuppressWarnings("unchecked")
  private static Map<String, RemoteObject> getRemoteObjectMap() {
    ServiceStore serviceStore = ContextProvider.getServiceStore();
    Map<String, RemoteObject> map
      = ( Map<String, RemoteObject> )serviceStore.getAttribute( CLIENT_OBJECT_MAP_KEY );
    if( map == null ) {
      map = new HashMap<String, RemoteObject>();
      serviceStore.setAttribute( CLIENT_OBJECT_MAP_KEY, map );
    }
    return map;
  }

  private RemoteObjectFactory() {
    // prevent instantiation
  }

}
