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

package org.eclipse.rap.demo;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.widgets.*;
import org.eclipse.rap.ui.part.ViewPart;


public class DemoTableViewPart extends ViewPart {

  private static final int ROWS = 40;
  private static final int COLUMNS = 10;

  public void createPartControl( final Composite parent ) {
    parent.setLayout( new FormLayout() );
    final Table table = new Table( parent, RWT.NONE );
    FormData formData = new FormData();
    table.setLayoutData( formData );
    formData.top = new FormAttachment( 0, 5 );
    formData.left = new FormAttachment( 0, 5 );
    formData.right = new FormAttachment( 100, -5 );
    formData.bottom = new FormAttachment( 100, -5 );
    for( int i = 0; i < COLUMNS; i++ ) {
      TableColumn tableColumn = new TableColumn( table, RWT.NONE );
      tableColumn.setText( "Column" + i );
      if( i == 2 ) {
        tableColumn.setWidth( 190 );
      } else {
        tableColumn.setWidth( 70 );
      }
    }
    for( int i = 0; i < ROWS; i++ ) {
      TableItem tableItem = new TableItem( table, RWT.NONE );
      for( int j = 0; j < COLUMNS; j++ ) {
        tableItem.setText( j, "Item" + i + "-" + j );
      }
    }

  }
}
