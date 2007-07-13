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
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;

public class CoolBarTab extends ExampleTab {

  private final Image image1;
  private final Image image2;
  private final Image image3;
  private final Image image4;
  private CoolBar coolBar;

  public CoolBarTab( final CTabFolder topFolder ) {
    super( topFolder, "CoolBar" );
    ClassLoader loader = getClass().getClassLoader();
    image1 = Image.find( "resources/newfile_wiz.gif", loader );
    image2 = Image.find( "resources/newfolder_wiz.gif", loader );
    image3 = Image.find( "resources/newprj_wiz.gif", loader );
    image4 = Image.find( "resources/search_src.gif", loader );
  }

  protected void createStyleControls( final Composite parent ) {
    // TODO [rst] Allow for vertical CoolBars
//  createStyleButton( "HORIZONTAL", SWT.HORIZONTAL );
//  createStyleButton( "VERTICAL", SWT.VERTICAL );
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "FLAT", SWT.FLAT );
    createVisibilityButton();
    createEnablementButton();
    createLockedButton( parent );
  }

  protected void createExampleControls( final Composite parent ) {
    int style = getStyle();
    coolBar = new CoolBar( parent, style );
    
    // Create toolBar1 to be displayed in the first CoolItem
    final CoolItem coolItem1 = new CoolItem( coolBar, style );
    final ToolBar toolBar1 = createToolBar( coolBar, SWT.NONE );
    coolItem1.setControl( toolBar1 );
  
    // Create toolBar2 to be displayed in the second CoolItem
    final CoolItem coolItem2 = new CoolItem( coolBar, style );
    ToolBar toolBar2 = createToolBar( coolBar, SWT.NONE );
    coolItem2.setControl( toolBar2 );
    
    // Register CoolBar
    registerControl( coolBar );

    parent.addControlListener( new ControlAdapter() {
      public void controlResized( final ControlEvent e ) {
        computeSize( toolBar1, coolItem1, coolItem2 );
      }
    } );
    computeSize( toolBar1, coolItem1, coolItem2 );
  }

  private void computeSize( final ToolBar toolBar1,
                            final CoolItem coolItem1,
                            final CoolItem coolItem2 )
  {
    Point size = toolBar1.computeSize( SWT.DEFAULT, SWT.DEFAULT );
    // TODO [fappel]: check whether size adjustment is really needed or a bug . 
    size = new Point( size.x + ( int )( size.x * 0.1 ), size.y );
    coolItem1.setSize( size );
    coolItem2.setSize( size );
    coolBar.setSize( coolBar.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
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
