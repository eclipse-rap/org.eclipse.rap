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
package org.eclipse.rap.rwt.internal.protocol;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.Adaptable;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceStore;
import org.eclipse.rap.rwt.internal.util.ParamCheck;


/**
 * A <code>ClientObjectFactory</code> is used to create instances of
 * {@link IClientObject} for given objects.
 *
 * @see IClientObject
 */
public final class ClientObjectFactory {

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
    IClientObjectAdapter adapter = getAdapter( object );
    return getForId( adapter.getId() );
  }

  private static IClientObjectAdapter getAdapter( Adaptable object ) {
    IClientObjectAdapter result = object.getAdapter( IClientObjectAdapter.class );
    if( result == null ) {
      String message = "Could not retrieve an instance of IClientObjectAdapter.";
      throw new IllegalArgumentException( message );
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
    ServiceStore serviceStore = ContextProvider.getServiceStore();
    Map<String, IClientObject> result
      = ( Map<String, IClientObject> )serviceStore.getAttribute( CLIENT_OBJECT_MAP_KEY );
    if( result == null ) {
      result = new HashMap<String, IClientObject>();
      serviceStore.setAttribute( CLIENT_OBJECT_MAP_KEY, result );
    }
    return result;
  }

  private ClientObjectFactory() {
    // prevent instantiation
  }

}
