/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class TreeTab extends ExampleTab {

  private Tree tree;

  public TreeTab( TabFolder folder ) {
    super( folder, "Tree" );
  }

  void createStyleControls( ) {
    createStyleButton( "BORDER" );
    createStyleButton( "CHECK" );
    createVisibilityButton();
    createEnablementButton();
    createFontChooser();
  }

  void createExampleControls( Composite top ) {
    top.setLayout( new RowLayout() );
    int style = getStyle();
    tree = new Tree( top, style );
    tree.setLayoutData( new RowData( 200, 200 ) );
    for (int i = 0; i < 4; i++) {
        TreeItem item = new TreeItem (tree, RWT.NONE);
        item.setText("Node_" + (i + 1));
        if (i < 3) {
            TreeItem subitem = new TreeItem (item, RWT.NONE);
            subitem.setText("Subnode_" + (i + 1));
        }
    }
    registerControl( tree );
  }

}
