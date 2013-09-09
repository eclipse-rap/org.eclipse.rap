/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class RowTemplate implements Serializable {

  private final List<Cell> cells;

  public RowTemplate() {
    cells = new ArrayList<Cell>();
  }

  void addCell( Cell cell ) {
    checkCell( cell );
    cells.add( cell );
  }

  private void checkCell( Cell cell ) {
    if( cell == null ) {
      throw new IllegalArgumentException( "Cell must not be null" );
    }
  }

  List<Cell> getCells() {
    return new ArrayList<Cell>( cells );
  }
}
