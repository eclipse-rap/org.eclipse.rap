/**
 * 
 */
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.layout.FillLayout;
import org.eclipse.rap.rwt.widgets.*;

public class TableTab extends ExampleTab {

  private static final int COLUMNS = 5;
  private static final int ROWS = 8;

  public TableTab( TabFolder folder ) {
    super( folder, "Table" );
  }

  void createStyleControls( ) {
    createStyleButton( "BORDER" );
    createVisibilityButton();
    createEnablementButton();
  }

  void createExampleControls( Composite top ) {
    top.setLayout( new FillLayout() );
    
    int style = getStyle();
    Table table = new Table( top, style );

    for( int i = 0; i < COLUMNS; i++ ) {
      TableColumn tableColumn = new TableColumn( table, RWT.NONE );
      tableColumn.setText( "Col " + i );
        tableColumn.setWidth( i == 0 ? 50 : 100 );
    }

    for( int i = 0; i < ROWS; i++ ) {
      TableItem tableItem = new TableItem( table, RWT.NONE );
      for( int j = 0; j < COLUMNS; j++ ) {
        tableItem.setText( j, "Item" + i + "-" + j );
      }
    }
    table.setSelection( 0 );
    registerControl( table );
  }

}
