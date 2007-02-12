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
import org.eclipse.rap.rwt.custom.*;
import org.eclipse.rap.rwt.graphics.Point;
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.lifecycle.IEntryPoint;
import org.eclipse.rap.rwt.widgets.*;

public class LayoutDemo implements IEntryPoint {

  private Button buRight;

  public Display createUI() {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.SHELL_TRIM );
    shell.setBounds( 10, 10, 800, 600 );
    createContents( shell );
    shell.setText( "RWT Layout Demo" );
    shell.layout();
    shell.pack();
    shell.open();
    return display;
  }

  private void createContents( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    final TabFolder topFolder = new TabFolder( parent, RWT.NONE );
    new RowLayoutTab( topFolder );
    new StackedLayoutTab( topFolder );
    createTabCBannerLayout( topFolder );
    createTabViewFormLayout( topFolder );
    topFolder.setSelection( 0 );
  }

  /**
   * Creates a tab to display a CBannerLayout.
   * 
   * TODO [rst] make this a class of its own
   */
  private void createTabCBannerLayout( final TabFolder folder ) {
    TabItem item = new TabItem( folder, RWT.NONE );
    item.setText( "CBannerLayout" );
    Composite comp = new Composite( folder, RWT.NONE );
    item.setControl( comp );
    comp.setLayout( new GridLayout() );
    // CBanner
    final CBanner cb = new CBanner( comp, RWT.NONE );
    cb.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    // left
    Button buLeft = new Button( cb, RWT.NONE );
    buLeft.setText( "Left" );
    cb.setLeft( buLeft );
    // right
    buRight = new Button( cb, RWT.NONE );
    buRight.setText( "Right" );
    cb.setRight( buRight );
    cb.setRightWidth( 100 );
    cb.setRightMinimumSize( new Point( 50, RWT.DEFAULT ) );
    final Button chkSimple = new Button( comp, RWT.CHECK );
    chkSimple.setText( "Classic" );
    chkSimple.setSelection( true );
  }

  /**
   * Creates a tab to display a ViewFormLayout.
   * 
   * TODO [rst] make this a class of its own
   */
  private void createTabViewFormLayout( final TabFolder folder ) {
    TabItem item = new TabItem( folder, RWT.NONE );
    item.setText( "ViewFormLayout" );
    Composite comp = new Composite( folder, RWT.NONE );
    item.setControl( comp );
    comp.setLayout( new FillLayout() );
    ViewForm viewform = new ViewForm( comp, RWT.BORDER );
    Text text = new Text( viewform, RWT.WRAP );
    text.setText( "Hello World!  " );
    viewform.setContent( text );
    // top left
    Label left = new Label( viewform, RWT.WRAP );
    left.setText( "Label - Label - Label - Label - Label - Label - Label" );
    left.setToolTipText( "top left" );
    viewform.setTopLeft( left );
    // top center
    // Button center = new Button(viewform, RWT.NONE);
    // center.setText("Center");
    // center.setToolTipText("top center");
    // viewform.setTopCenter(center);
    ToolBar toolbar = new ToolBar( viewform, RWT.HORIZONTAL );
    ToolItem item1 = new ToolItem( toolbar, RWT.NONE );
    item1.setText( "Item1" );
    ToolItem item2 = new ToolItem( toolbar, RWT.NONE );
    item2.setText( "Item2" );
    viewform.setTopCenter( toolbar );
    // top right
    Button right = new Button( viewform, RWT.NONE );
    right.setText( "Close" );
    viewform.setTopRight( right );
  }
}
