/**
 * 
 */
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class TreeTab extends ExampleTab {

  private Tree tree;

  public TreeTab( final TabFolder folder ) {
    super( folder, "Tree" );
  }

  void createStyleControls( ) {
    createStyleButton( "BORDER" );
    createStyleButton( "CHECK" );
    createFontChooser( new Control[] { tree } );
  }

  void createExampleControls( final Composite parent ) {
    parent.setLayout( new RowLayout() );
    int style = getStyle();
    tree = new Tree( parent, style );
    tree.setLayoutData( new RowData( 150, 150 ) );
    for( int i = 0; i < 4; i++ ) {
      TreeItem item = new TreeItem( tree, RWT.NONE );
      item.setText( "Node_" + ( i + 1 ) );
      if( i < 3 ) {
        TreeItem subitem = new TreeItem( item, RWT.NONE );
        subitem.setText( "Subnode_" + ( i + 1 ) );
      }
    }
  }
}
