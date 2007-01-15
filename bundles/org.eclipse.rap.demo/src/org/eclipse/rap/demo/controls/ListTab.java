/**
 * 
 */
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class ListTab extends ExampleTab {

  public ListTab( TabFolder folder ) {
    super( folder, "List" );
  }

  void createStyleControls( ) {
    createStyleButton( "BORDER" );
  }

  void createExampleControls( Composite top ) {
    top.setLayout( new RowLayout() );
    int style = getStyle();
    List list1 = new List( top, style );
    list1.setLayoutData( new RowData(150, 150) );
    list1.setItems( new String[] { "Test List ", "Item 2", "Item 3" } );
  }

}
