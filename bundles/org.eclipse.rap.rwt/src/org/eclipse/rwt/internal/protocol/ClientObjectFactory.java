/*******************************************************************************
* Copyright (c) 2011, 2012 EclipseSource and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rwt.internal.protocol;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.Adaptable;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.rwt.service.IServiceStore;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;


/**
 * A <code>ClientObjectFactory</code> is used to create instances of
 * {@link IClientObject} for given objects.
 * 
 * @see IClientObject
 * @since 1.5
 */
public final class ClientObjectFactory {

  private static final String GC_SUFFIX = "#gc";
  private static final String CLIENT_OBJECT_MAP_KEY = "synchronizerMapKey";

  /**
   * Returns an {@link IClientObject} instance for the given object. The object
   * must provide an {@link IClientObjectAdapter} in its
   * {@link Adaptable#getAdapter(Class)} method. The returned client object
   * instance is unique during a request.
   * <p>
   * This method must be called in request scope.
   * </p>
   * 
   * @param object the object to create a client object for
   * @return the client object for the given object, unique within the current
   *         request
   */
  public static IClientObject getClientObject( Adaptable object ) {
    ParamCheck.notNull( object, "object" );
    checkValidThread( object );
    IClientObjectAdapter adapter = getAdapter( object );
    return getForId( adapter.getId() );
  }

  /**
   * Creates a {@link IClientObject} for the GC for a specific Widget. The
   * returned instance is unique for the time a Request exists. The relationship
   * between these two is a 1:1 relationship.
   * 
   * @param widget The server side {@link Widget} instance.
   * @return a request specific {@link IClientObject} instance for the GC of the
   *         passed {@link Widget}.
   */
  public static IClientObject getForGC( Widget widget ) {
    if( !isValidThread( widget ) ) {
      throw new IllegalStateException( "Illegal thread access" );
    }
    return getForId( WidgetUtil.getId( widget ) + GC_SUFFIX );
  }

  private static void checkValidThread( Adaptable object ) {
    Display display = getDisplay( object );
    if( display != null ) {
      if( !isValidThread( display ) ) {
        throw new IllegalStateException( "Illegal thread access" );
      }
    }
  }

  private static Display getDisplay( Adaptable object ) {
    Display display = null;
    if( object instanceof Display ) {
      display = ( Display )object;
    } else if( object instanceof Widget ) {
      display = ( ( Widget )object ).getDisplay();
    }
    return display;
  }

  private static IClientObjectAdapter getAdapter( Adaptable object ) {
    IClientObjectAdapter result = object.getAdapter( IClientObjectAdapter.class );
    if( result == null ) {
      throw new IllegalStateException( "Could not retrieve an instance of IWidgetAdapter." );
    }
    return result;
  }

  private static IClientObject getForId( String id ) {
    IClientObject result;
    Map<String, IClientObject> map = getClientObjectMap();
    if( map.containsKey( id ) ) {
      result = map.get( id );
    } else {
      result = new ClientObject( id );
      map.put( id, result );
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  private static Map<String, IClientObject> getClientObjectMap() {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    Map<String, IClientObject> result = ( Map<String, IClientObject> )serviceStore.getAttribute( CLIENT_OBJECT_MAP_KEY );
    if( result == null ) {
      result = new HashMap<String, IClientObject>();
      serviceStore.setAttribute( CLIENT_OBJECT_MAP_KEY, result );
    }
    return result;
  }

  private static boolean isValidThread( Widget widget ) {
    return isValidThread( widget.getDisplay() );
  }

  private static boolean isValidThread( Display display ) {
    IDisplayAdapter adapter = display.getAdapter( IDisplayAdapter.class );
    return adapter.isValidThread();
  }

  private ClientObjectFactory() {
    // prevent instantiation
  }
}
