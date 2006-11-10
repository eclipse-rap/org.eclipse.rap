/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.widgets;


public class Combo extends AbstractList {

  private int selectionIndex = -1;
  
  void setNullSelection(){
    
  }

  public Combo( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
  }

  
  public int getSelectionIndex() {
    return selectionIndex;
  }

  public void setSelectionIndex( int selectionIndex ) {
    checkIndex( selectionIndex );
    this.selectionIndex = selectionIndex;
  }

  
}
