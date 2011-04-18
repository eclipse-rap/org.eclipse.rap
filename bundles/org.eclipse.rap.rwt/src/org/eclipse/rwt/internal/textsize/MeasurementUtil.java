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

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;


public class MeasurementUtil {
  private static final String ITEMS = MeasurementUtil.class.getName() + "#MeasurementItems";
  private static final MeasurementItem[] EMTY_ITEMS = new MeasurementItem[ 0 ];
  
  static void addMeasurementItem( MeasurementItem newItem ) {
    MeasurementItem[] oldItems = getMeasurementItems();
    if( !contains( oldItems, newItem ) ) {
      MeasurementItem[] items = concatenate( oldItems, newItem );
      setMeasurementItems( items );
      MeasurementHandler.register();
    }
  }

  static MeasurementItem[] getMeasurementItems() {
    MeasurementItem[] result = ( MeasurementItem[] )getStateInfo().getAttribute( ITEMS );
    if( result == null ) {
      result = EMTY_ITEMS;
    }
    return result;
  }

  static void setMeasurementItems( MeasurementItem[] items ) {
    getStateInfo().setAttribute( ITEMS, items );
  }

  static boolean isEquals( MeasurementItem item1, MeasurementItem item2 ) {
    return    item2.getTextToMeasure().equals( item1.getTextToMeasure() )
           && item2.getFontData().equals( item1.getFontData() )
           && item2.getWrapWidth() == item1.getWrapWidth();
  }

  static boolean contains( MeasurementItem[] items, MeasurementItem item ) {
    boolean result = false;
    for( int i = 0; !result && i < items.length; i++ ) {
      result = isEquals( item, items[ i ] );
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