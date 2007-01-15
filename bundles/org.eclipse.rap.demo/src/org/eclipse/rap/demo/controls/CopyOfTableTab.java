/**
 * 
 */
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.widgets.*;

public class CopyOfTableTab extends ExampleTab {

  private static final int COLUMNS = 5;
  private static final int ROWS = 8;

  public CopyOfTableTab( TabFolder folder ) {
    super( folder, "Table" );
  }

  void createStyleControls( ) {
    createStyleButton( "BORDER" );
  }

  void createExampleControls( Composite top ) {
    FillLayout layout = new FillLayout();
    layout.marginWidth = 10;
    layout.marginHeight = 10;
    top.setLayout( new FormLayout() );
    
    int style = getStyle();
    Table table = new Table( top, style );

    FormData formData = new FormData();
    table.setLayoutData( formData );
    formData.top = new FormAttachment( 0, 5 );
    formData.left = new FormAttachment( 0, 5 );
    formData.right = new FormAttachment( 100, -5 );
    formData.bottom = new FormAttachment( 100, -5 );
    
    for( int i = 0; i < COLUMNS; i++ ) {
      TableColumn tableColumn = new TableColumn( table, RWT.NONE );
      tableColumn.setText( "Col " + i );
        tableColumn.setWidth( i == 0 ? 50 : 30 );
    }

    for( int i = 0; i < ROWS; i++ ) {
      TableItem tableItem = new TableItem( table, RWT.NONE );
      for( int j = 0; j < COLUMNS; j++ ) {
        tableItem.setText( j, "Item" + i + "-" + j );
      }
    }
//    table1.setSelection( 0 );

  }

}
