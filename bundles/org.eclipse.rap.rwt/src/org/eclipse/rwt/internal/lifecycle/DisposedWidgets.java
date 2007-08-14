/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.lifecycle;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.swt.widgets.Widget;


public final class DisposedWidgets {
  
  private static final String DISPOSAL_LIST 
    = DisposedWidgets.class.getName() + "#disposalList";

  public static void add( final Widget widget ) {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    List disposalList = ( List )stateInfo.getAttribute( DISPOSAL_LIST );
    if( disposalList == null ) {
      disposalList = new LinkedList();
      stateInfo.setAttribute( DISPOSAL_LIST, disposalList );
    }
    disposalList.add( widget );
  }

  public static Widget[] getAll() {
    Widget[] result;
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    List disposalList = ( List )stateInfo.getAttribute( DISPOSAL_LIST );
    if( disposalList == null ) {
      result = new Widget[ 0 ];
    } else {
      result = new Widget[ disposalList.size() ];
      disposalList.toArray( result );
    }
    return result;
  }

  private DisposedWidgets() {
    // prevent instantiation
  }
}
