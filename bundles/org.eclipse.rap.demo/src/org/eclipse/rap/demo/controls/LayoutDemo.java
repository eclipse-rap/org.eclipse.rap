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
import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.lifecycle.IEntryPoint;
import org.eclipse.swt.widgets.*;

public class LayoutDemo implements IEntryPoint {

  private Button buRight;

  public Display createUI() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.SHELL_TRIM );
    shell.setBounds( 10, 10, 800, 600 );
    createContents( shell );
    shell.setText( "SWT Layout Demo" );
    ClassLoader classLoader = getClass().getClassLoader();
    Image image = Image.find( "resources/shell.gif", classLoader );
    shell.setImage( image  );
    shell.layout();
    shell.open();
    return display;
  }

  private void createContents( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    final TabFolder topFolder = new TabFolder( parent, SWT.NONE );
    ExampleTab tab;
    tab = new FillLayoutTab( topFolder );
    tab.createContents();
    tab = new RowLayoutTab( topFolder );
    tab.createContents();
    tab = new GridLayoutTab( topFolder );
    tab.createContents();
    tab = new StackLayoutTab( topFolder );
    tab.createContents();
    tab = new NestedLayoutsTab( topFolder );
    tab.createContents();
    tab = new TextSizeTab( topFolder );
    tab.createContents();
    topFolder.setSelection( 0 );
  }

  /**
   * Creates a tab to display a CBannerLayout.
   * 
   * TODO [rst] make this a class of its own
   */
  private void createTabCBannerLayout( final TabFolder folder ) {
    TabItem item = new TabItem( folder, SWT.NONE );
    item.setText( "CBannerLayout" );
    Composite comp = new Composite( folder, SWT.NONE );
    item.setControl( comp );
    comp.setLayout( new GridLayout() );
    // CBanner
    final CBanner cb = new CBanner( comp, SWT.NONE );
    cb.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    // left
    Button buLeft = new Button( cb, SWT.NONE );
    buLeft.setText( "Left" );
    cb.setLeft( buLeft );
    // right
    buRight = new Button( cb, SWT.NONE );
    buRight.setText( "Right" );
    cb.setRight( buRight );
    cb.setRightWidth( 100 );
    cb.setRightMinimumSize( new Point( 50, SWT.DEFAULT ) );
    final Button chkSimple = new Button( comp, SWT.CHECK );
    chkSimple.setText( "Classic" );
    chkSimple.setSelection( true );
  }

  /**
   * Creates a tab to display a ViewFormLayout.
   * 
   * TODO [rst] make this a class of its own
   */
  private void createTabViewFormLayout( final TabFolder folder ) {
    TabItem item = new TabItem( folder, SWT.NONE );
    item.setText( "ViewFormLayout" );
    Composite comp = new Composite( folder, SWT.NONE );
    item.setControl( comp );
    comp.setLayout( new FillLayout() );
    ViewForm viewform = new ViewForm( comp, SWT.BORDER );
    Text text = new Text( viewform, SWT.WRAP );
    text.setText( "Hello World!  " );
    viewform.setContent( text );
    // top left
    Label left = new Label( viewform, SWT.WRAP );
    left.setText( "Label - Label - Label - Label - Label - Label - Label" );
    left.setToolTipText( "top left" );
    viewform.setTopLeft( left );
    // top center
    // Button center = new Button(viewform, SWT.NONE);
    // center.setText("Center");
    // center.setToolTipText("top center");
    // viewform.setTopCenter(center);
    ToolBar toolbar = new ToolBar( viewform, SWT.HORIZONTAL );
    ToolItem item1 = new ToolItem( toolbar, SWT.NONE );
    item1.setText( "Item1" );
    ToolItem item2 = new ToolItem( toolbar, SWT.NONE );
    item2.setText( "Item2" );
    viewform.setTopCenter( toolbar );
    // top right
    Button right = new Button( viewform, SWT.NONE );
    right.setText( "Close" );
    viewform.setTopRight( right );
  }
}
