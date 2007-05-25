/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

public class CoolBarTab extends ExampleTab {

  private final Image image1;
  private final Image image2;
  private final Image image3;
  private final Image image4;
  private CoolBar coolBar;
  
  public CoolBarTab( final TabFolder folder ) {
    super( folder, "CoolBar" );
    ClassLoader loader = getClass().getClassLoader();
    image1 = Image.find( "resources/newfile_wiz.gif", loader );
    image2 = Image.find( "resources/newfolder_wiz.gif", loader );
    image3 = Image.find( "resources/newprj_wiz.gif", loader );
    image4 = Image.find( "resources/search_src.gif", loader );
  }

  protected void createStyleControls() {
    createStyleButton( "BORDER" );
    createStyleButton( "FLAT" );
    createVisibilityButton();
    createEnablementButton();
    createLockedButton( styleComp );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new RowLayout() );
    int style = getStyle();
    coolBar = new CoolBar( parent, style );
    coolBar.setLayoutData( new RowData( 500, 65 ) );
    // Create toolBar1 to be displayed in the first CoolItem
    ToolBar toolBar1 = createToolBar( coolBar, SWT.NONE );
    toolBar1.setSize( 250, 25 );
    CoolItem coolItem1 = new CoolItem( coolBar, style );
    coolItem1.setSize( 250, 25 );
    coolItem1.setControl( toolBar1 );
    // Create toolBar2 to be displayed in the second CoolItem
    ToolBar toolBar2 = createToolBar( coolBar, SWT.NONE );
    toolBar2.setSize( 250, 25 );
    CoolItem coolItem2 = new CoolItem( coolBar, style );
    coolItem2.setSize( 250, 25 );
    coolItem2.setControl( toolBar2 );
    // Register CoolBar
    registerControl( coolBar );
  }

  private ToolBar createToolBar( final Composite parent, final int id ) {
    int style = ( getStyle() & ( SWT.HORIZONTAL | SWT.VERTICAL ) );
    ToolBar toolBar = new ToolBar( parent, style );
    ToolItem item1 = new ToolItem( toolBar, SWT.PUSH );
    item1.setText( "new" + id );
    item1.setImage( image1 );
    ToolItem item2 = new ToolItem( toolBar, SWT.PUSH );
    item2.setText( "open" );
    item2.setImage( image2 );
    ToolItem item3 = new ToolItem( toolBar, SWT.PUSH );
    item3.setText( "save as" );
    item3.setImage( image3 );
    new ToolItem( toolBar, SWT.SEPARATOR );
    ToolItem item4 = new ToolItem( toolBar, SWT.PUSH );
    item4.setText( "print" );
    item4.setImage( image4 );
    return toolBar;
  }

  private void createLockedButton( final Composite parent ) {
    final Button button = new Button( parent, SWT.CHECK );
    button.setText( "Locked" );
    button.setSelection( coolBar.getLocked() );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        coolBar.setLocked( button.getSelection() );
      }
    } );
  }
}
