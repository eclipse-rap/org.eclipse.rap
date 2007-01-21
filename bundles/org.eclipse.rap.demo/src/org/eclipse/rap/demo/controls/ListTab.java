/**
 * 
 */
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class ListTab extends ExampleTab {

  private List list;

  public ListTab( final TabFolder folder ) {
    super( folder, "List" );
  }

  void createStyleControls() {
    createStyleButton( "BORDER" );
    createStyleButton( "H_SCROLL" );
    createStyleButton( "V_SCROLL" );
    createVisibilityButton();
    createEnablementButton();
    createFontChooser();
  }

  void createExampleControls( final Composite parent ) {
    parent.setLayout( new RowLayout() );
    int style = getStyle();
    list = new List( parent, style );
    list.setLayoutData( new RowData( 200, 200 ) );
    String text 
      = "A very long item that demonstrates horizontal scrolling in a List";
    list.add( text );
    for( int i = 1; i <= 25; i++ ) {
      list.add( "Item " + i );
    }
    registerControl( list );
  }
}
