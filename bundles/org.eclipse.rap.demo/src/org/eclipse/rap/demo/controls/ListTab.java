/**
 * 
 */
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class ListTab extends ExampleTab {

  private List list;

  public ListTab( TabFolder folder ) {
    super( folder, "List" );
  }

  void createStyleControls( ) {
    createStyleButton( "BORDER" );
    createFontChooser( new Control[] { list } );
  }

  void createExampleControls( Composite top ) {
    top.setLayout( new RowLayout() );
    int style = getStyle();
    list = new List( top, style );
    list.setLayoutData( new RowData(150, 150) );
    list.setItems( new String[] { "Test List ", "Item 2", "Item 3" } );
  }

}
