/**
 * 
 */
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class TreeTab extends ExampleTab {

  public TreeTab( TabFolder folder ) {
    super( folder, "Tree" );
  }

  void createStyleControls( ) {
    createStyleButton( "BORDER" );
    createStyleButton( "CHECK" );
  }

  void createExampleControls( Composite top ) {
    top.setLayout( new RowLayout() );
    int style = getStyle();
    Tree tree1 = new Tree( top, style );
    tree1.setLayoutData( new RowData(150, 150) );
    for (int i = 0; i < 4; i++) {
        TreeItem item = new TreeItem (tree1, RWT.NONE);
        item.setText("Node_" + (i + 1));
        if (i < 3) {
            TreeItem subitem = new TreeItem (item, RWT.NONE);
            subitem.setText("Subnode_" + (i + 1));
        }
    }
  }

}
