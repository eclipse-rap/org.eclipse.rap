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
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class CoolBarTab extends ExampleTab {

  Image image1, image2, image3, image4;
  private CoolBar coolBar;
  
  public CoolBarTab( final TabFolder folder ) {
    super( folder, "CoolBar" );
  }

  void createStyleControls() {
    createStyleButton( "BORDER" );
    createStyleButton( "FLAT" );
    createVisibilityButton();
    createEnablementButton();
  }

  void createExampleControls( final Composite top ) {
    top.setLayout( new RowLayout() );
    int style = getStyle();
    int toolBarStyle = (style & (RWT.HORIZONTAL | RWT.VERTICAL));
 
    ClassLoader loader = getClass().getClassLoader();
    image1 = Image.find( "resources/newfile_wiz.gif", loader );
    image2 = Image.find( "resources/newfolder_wiz.gif", loader );
    image3 = Image.find( "resources/newprj_wiz.gif", loader );
    image4 = Image.find( "resources/search_src.gif", loader );
    
    coolBar = new CoolBar( top, style );
    coolBar.setLayoutData( new RowData( 260, 65 ) );
    
    // Create the push button toolbar cool item
    ToolBar toolBar = createToolBar( coolBar, toolBarStyle );
    toolBar .setSize( 250, 25 );

    CoolItem pushItem = new CoolItem (coolBar, style);
    pushItem.setSize( 250, 25 );
    pushItem.setControl (toolBar);

    ToolBar toolBar2 = createToolBar( coolBar, toolBarStyle );
    toolBar2.setSize( 250, 25 );

    CoolItem pushItem2 = new CoolItem (coolBar, style);
    pushItem2.setSize( 250, 25 );
    pushItem2.setControl( toolBar2 );
    
    registerControl( coolBar );
  }

  private ToolBar createToolBar( final Composite parent, final int style ) {
    ToolBar toolBar = new ToolBar( parent, style );
    //toolBar.setLayoutData( new RowData( 500, 50) );
    ToolItem item1 = new ToolItem (toolBar, RWT.PUSH);
    item1.setText( "new" );
    item1.setImage( image1 );
    ToolItem item2 = new ToolItem( toolBar, RWT.PUSH );
    item2.setText( "open" );
    item2.setImage( image2 );
    ToolItem item3 = new ToolItem( toolBar, RWT.PUSH );
    item3.setText( "save as" );
    item3.setImage( image3 );
    new ToolItem (toolBar, RWT.SEPARATOR);
    ToolItem item4 = new ToolItem( toolBar, RWT.PUSH );
    item4.setText( "print" );
    item4.setImage( image4 );
    //toolBar.pack();
    return toolBar;
  }

//  private CoolItem createItem( CoolBar coolBar, int style ) {
//    int toolBarStyle = (style & (RWT.HORIZONTAL | RWT.VERTICAL));
//    ToolBar toolBar = createToolBar( coolBar, toolBarStyle );
//    Point tbSize = toolBar.getSize();
//    CoolItem coolItem = new CoolItem( coolBar, RWT.NONE );
//    coolItem.setControl( toolBar );
//    Point tbPrefSize = toolBar.computeSize( tbSize.x, tbSize.y, false );
//    coolItem.setPreferredSize( tbPrefSize );
//    return coolItem; 
//  }

}
