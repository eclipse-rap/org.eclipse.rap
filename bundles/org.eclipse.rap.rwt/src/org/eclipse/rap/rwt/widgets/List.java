/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.widgets;

import org.eclipse.rap.rwt.RWT;


public class List extends AbstractList {

  private int[] selectionIndices = new int[ 0 ];
  private int[] EMPTY_SELECTION = new int[ 0 ];

  final void setNullSelection() {
    selectionIndices = EMPTY_SELECTION;
  }

  public List( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
  }

  static int checkStyle( final int style ) {
    int result = RWT.NONE;
    result = checkBits( style, RWT.SINGLE, RWT.MULTI, 0, 0, 0, 0 );
    return result;
  }

  public int getSelectionIndex() {
    int result = -1;
    if( selectionIndices.length > 0 ) {
      result = selectionIndices[ 0 ];
    }
    return result;
  }

  public int[] getSelectionIndices() {
    int[] result = new int[ selectionIndices.length ];
    System.arraycopy( selectionIndices, 0, result, 0, selectionIndices.length );
    return result;
  }

  public void setSelection( int selectionIndex ) {
    checkIndex( selectionIndex );
    selectionIndices = new int[ 1 ];
    selectionIndices[ 0 ] = selectionIndex;
  }

  public void setSelection( int[] selectionIndices ) {
    for( int i = 0; i < selectionIndices.length; i++ ) {
      checkIndex( selectionIndices[ i ] );
    }
    this.selectionIndices = new int[ selectionIndices.length ];
    System.arraycopy( selectionIndices,
                      0,
                      this.selectionIndices,
                      0,
                      selectionIndices.length );
  }
}
