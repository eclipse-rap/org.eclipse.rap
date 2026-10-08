/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.custom;

import org.eclipse.swt.internal.SWTEventListener;
import org.eclipse.swt.internal.events.EventTypes;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TypedListener;


class TypedCTabFolderListener extends TypedListener {

  TypedCTabFolderListener( SWTEventListener listener ) {
    super( listener );
  }
  
  @Override
  public void handleEvent( Event event ) {
    CTabFolderEvent tabFolderEvent = new CTabFolderEvent( event );
    switch( event.type ) {
      case EventTypes.CTAB_FOLDER_CLOSE: {
        CTabFolder2Listener listener = ( CTabFolder2Listener )getEventListener();
        listener.close( tabFolderEvent );
        event.doit = tabFolderEvent.doit;
        break;
      }
      case EventTypes.CTAB_FOLDER_MINIMIZE: {
        CTabFolder2Listener listener = ( CTabFolder2Listener )getEventListener();
        listener.minimize( tabFolderEvent );
        break;
      }
      case EventTypes.CTAB_FOLDER_MAXIMIZE: {
        CTabFolder2Listener listener = ( CTabFolder2Listener )getEventListener();
        listener.maximize( tabFolderEvent );
        break;
      }
      case EventTypes.CTAB_FOLDER_RESTORE: {
        CTabFolder2Listener listener = ( CTabFolder2Listener )getEventListener();
        listener.restore( tabFolderEvent );
        break;
      }
      case EventTypes.CTAB_FOLDER_SHOW_LIST: {
        CTabFolder2Listener listener = ( CTabFolder2Listener )getEventListener();
        listener.showList( tabFolderEvent );
        event.doit = tabFolderEvent.doit;
        break;
      }
    }
  }
}
