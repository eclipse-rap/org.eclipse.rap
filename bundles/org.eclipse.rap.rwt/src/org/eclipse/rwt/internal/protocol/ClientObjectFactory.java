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

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
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

  private static final String SYNCHRONIZER_MAP_KEY = "synchronizerMapKey";

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
  @SuppressWarnings( "unchecked" )
  public static IClientObject getForWidget( Widget widget ) {
    IClientObject result;
    if( !isValidThread( widget ) ) {
      throw new IllegalStateException( "Illegal thread access" );
    }
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    Map<Widget, IClientObject> map
      = ( Map<Widget, IClientObject> )stateInfo.getAttribute( SYNCHRONIZER_MAP_KEY );
    if( map == null ) {
      map = new HashMap<Widget, IClientObject>();
      stateInfo.setAttribute( SYNCHRONIZER_MAP_KEY, map );
    }
    if( map.containsKey( widget ) ) {
      result = map.get( widget );
    } else {
      result = new ClientObject( widget );
      map.put( widget, result );
    }
    return result;
  }

  private static boolean isValidThread( Widget widget ) {
    Display display = widget.getDisplay();
    IDisplayAdapter adapter = ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class );
    return adapter.isValidThread();
  }

  private ClientObjectFactory() {
    // prevent instantiation
  }

}
