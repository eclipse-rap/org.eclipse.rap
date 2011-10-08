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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;


/**
 * The only purpose of the {@link ClientObjectFactory} is to instantiate different
 * {@link IClientObject}s regarding to their associated type e.g. Widget, Themestore and so on.
 *
 * @see IClientObject
 *
 * @since 1.5
 */
public final class ClientObjectFactory {

  private static final String CLIENT_OBJECT_MAP_KEY = "synchronizerMapKey";

  /**
   * Creates a {@link IClientObject} for a specific Widget. The returned instance
   * is unique for the time a Request exists. Every {@link Widget} can have
   * one {@link IClientObject}. The relationship between these two is a 1:1
   * relationship.
   *
   * @param widget The server side {@link Widget} instance.
   *
   * @return a request specific {@link IClientObject} instance for the passed
   * {@link Widget}.
   */
  public static IClientObject getForWidget( Widget widget ) {
    if( !isValidThread( widget ) ) {
      throw new IllegalStateException( "Illegal thread access" );
    }
    return getForId( WidgetUtil.getId( widget ) );
  }

  /**
   * Creates a {@link IClientObject} for a specific Display. The returned instance
   * is unique for the time a Request exists. The relationship between these two is a 1:1
   * relationship.
   *
   * @param display The server side {@link Display} instance.
   *
   * @return a request specific {@link IClientObject} instance for the passed
   * {@link Display}.
   */
  public static IClientObject getForDisplay( Display display ) {
    ParamCheck.notNull( display, "display" );
    if( !isValidThread( display ) ) {
      throw new IllegalStateException( "Illegal thread access" );
    }
    return getForId( DisplayUtil.getId( display ) );
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
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    Map<String, IClientObject> result
      = ( Map<String, IClientObject> )stateInfo.getAttribute( CLIENT_OBJECT_MAP_KEY );
    if( result == null ) {
      result = new HashMap<String, IClientObject>();
      stateInfo.setAttribute( CLIENT_OBJECT_MAP_KEY, result );
    }
    return result;
  }
  
  private static boolean isValidThread( Widget widget ) {
    return isValidThread( widget.getDisplay() );
  }
  
  private static boolean isValidThread( Display display ) {
    IDisplayAdapter adapter = ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class );
    return adapter.isValidThread();
  }

  private ClientObjectFactory() {
    // prevent instantiation
  }
}
