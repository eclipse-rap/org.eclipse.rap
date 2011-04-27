/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;


class MeasurementUtil {
  private static final String ITEMS = MeasurementUtil.class.getName() + "#MeasurementItems";
  private static final MeasurementItem[] EMTY_ITEMS = new MeasurementItem[ 0 ];
  
  static void addItemToMeasure( MeasurementItem newItem ) {
    MeasurementItem[] oldItems = getItemsToMeasure();
    HttpServletRequest request = ContextProvider.getRequest();
    String value = request.getParameter( String.valueOf( newItem.hashCode() ) );
    if( value == null && !contains( oldItems, newItem ) ) {
      MeasurementItem[] items = concatenate( oldItems, newItem );
      setItemsToMeasure( items );
    }
  }

  static boolean hasItemsToMeasure() {
    return getItemsToMeasure().length != 0;
  }
  
  static MeasurementItem[] getItemsToMeasure() {
    MeasurementItem[] result = ( MeasurementItem[] )getStateInfo().getAttribute( ITEMS );
    if( result == null ) {
      result = EMTY_ITEMS;
    }
    return result;
  }
  
  
  ////////////////////////////////////////////////////////////
  // helping methods, package private for testing purpose only

  static void setItemsToMeasure( MeasurementItem[] items ) {
    getStateInfo().setAttribute( ITEMS, items );
  }

  static boolean contains( MeasurementItem[] items, MeasurementItem item ) {
    boolean result = false;
    for( int i = 0; !result && i < items.length; i++ ) {
      result = item.equals( items[ i ] );
    }
    return result;
  }

  static MeasurementItem[] concatenate( MeasurementItem[] items, MeasurementItem item ) {
    MeasurementItem[] result = new MeasurementItem[ items.length + 1 ];
    System.arraycopy( items, 0, result, 0, items.length );
    result[ items.length ] = item;
    return result;
  }

  private static IServiceStateInfo getStateInfo() {
    return ContextProvider.getStateInfo();
  }
}