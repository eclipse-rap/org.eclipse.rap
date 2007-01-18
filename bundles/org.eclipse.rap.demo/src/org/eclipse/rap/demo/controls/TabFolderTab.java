/**
 * 
 */
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class TabFolderTab extends ExampleTab {

  private TabFolder folder;

  public TabFolderTab( TabFolder parent ) {
    super( parent, "TabFolder" );
  }

  void createStyleControls( ) {
    createStyleButton( "BORDER" );
    createFontChooser( new Control[] { folder } );
  }

  void createExampleControls( Composite top ) {
    top.setLayout( new RowLayout() );
    int style = getStyle();

    folder = new TabFolder( top, style );
    folder.setLayoutData( new RowData( 250, 200) );
    for( int i = 0; i < 3; i++ ) {
      TabItem item = new TabItem( folder, style );
      item.setText( "Tab " + (i+1) );
      Text content = new Text(folder, RWT.WRAP | RWT.MULTI);
      content.setText( "Lorem ipsum dolor sit amet, consectetur adipisicing "
                       + "elit, sed do eiusmod tempor incididunt ut labore et "
                       + "dolore magna aliqua." );
      item.setControl(content);
    }
    folder.setSelection( 0 );
  }

}
