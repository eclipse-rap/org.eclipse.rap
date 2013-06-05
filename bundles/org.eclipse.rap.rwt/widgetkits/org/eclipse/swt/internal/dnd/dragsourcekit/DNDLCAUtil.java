/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.dnd.dragsourcekit;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;


public final class DNDLCAUtil {

  private DNDLCAUtil() {
    // prevent instantiation
  }

  public static JsonArray convertTransferTypes( Transfer[] transfer ) {
    JsonArray array = new JsonArray();
    for( int i = 0; i < transfer.length; i++ ) {
      TransferData[] supported = transfer[ i ].getSupportedTypes();
      for( int j = 0; j < supported.length; j++ ) {
        array.add( Integer.toString( supported[ j ].type ) );
      }
    }
    return array;
  }

  public static JsonArray convertOperations( int operations ) {
    JsonArray array = new JsonArray();
    if( ( operations & DND.DROP_COPY ) != 0  ) {
      array.add( "DROP_COPY" );
    }
    if( ( operations & DND.DROP_MOVE ) != 0  ) {
      array.add( "DROP_MOVE" );
    }
    if( ( operations & DND.DROP_LINK ) != 0  ) {
      array.add( "DROP_LINK" );
    }
    return array;
  }

}
