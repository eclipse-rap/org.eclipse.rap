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
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.events.SelectionListener;
import org.eclipse.rap.rwt.graphics.Point;
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.lifecycle.IEntryPoint;
import org.eclipse.rap.rwt.widgets.*;

public class LayoutDemo implements IEntryPoint {

  private Text text;
  private Button buRight;

  public Display createUI() {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.SHELL_TRIM );
    shell.setBounds( 10, 10, 800, 600 );
    shell.setLayout( new FillLayout() );
    SashForm sashForm = new SashForm( shell, RWT.VERTICAL );
    Composite compMain = new Composite( sashForm, RWT.NONE );
    Composite compFoot = new Composite( sashForm, RWT.NONE );
    sashForm.setWeights( new int[]{
      70, 30
    } );
    createMainPart( compMain );
    createFootPart( compFoot );
    shell.layout();
    shell.setText( "Layout Demo" );
    shell.open();
    return display;
  }

  // MAIN PART
  private void createMainPart( Composite parent ) {
    parent.setLayout( new FillLayout() );
    final TabFolder topFolder = new TabFolder( parent, RWT.NONE );
    createTabStackedLayout( topFolder );
    createTabCBannerLayout( topFolder );
    createTabViewFormLayout( topFolder );
    topFolder.setSelection( 0 );
  }

  /** Creates a tab to display a StackedLayout */
  private void createTabStackedLayout( TabFolder folder ) {
    TabItem item = new TabItem( folder, RWT.NONE );
    item.setText( "StackedLayout" );
    final Composite comp = new Composite( folder, RWT.NONE );
    item.setControl( comp );
    comp.setLayout( new RowLayout( RWT.VERTICAL ) );
    // stacked composite
    final int COUNT = 5;
    final Composite stackedComp = new Composite( comp, RWT.NONE );
    stackedComp.setLayoutData( new RowData( 100, 100 ) );
    final StackLayout layout = new StackLayout();
    stackedComp.setLayout( layout );
    final Button[] bArray = new Button[ COUNT ];
    for( int i = 0; i < COUNT; i++ ) {
      bArray[ i ] = new Button( stackedComp, RWT.PUSH );
      bArray[ i ].setText( "Button " + i );
    }
    layout.topControl = bArray[ 0 ];
    // switch button
    Button button = new Button( comp, RWT.PUSH );
    button.setText( "Show Next Button" );
    button.setLayoutData( new RowData( 100, 30 ) );
    final int[] index = new int[ 1 ];
    button.addSelectionListener( new SelectionListener() {

      public void widgetSelected( SelectionEvent event ) {
        index[ 0 ] = ( index[ 0 ] + 1 ) % COUNT;
        layout.topControl = bArray[ index[ 0 ] ];
        // log("Stacked item " + index[0]);
        stackedComp.layout();
      }
    } );
  }

  /** Creates a tab to display a CBannerLayout */
  private void createTabCBannerLayout( TabFolder folder ) {
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
    // System.out.println( "after creation: " + buRight.getBounds() );
    // buRight.computeSize(400, RWT.DEFAULT, true);
    // System.out.println( "after computesize: " + buRight.getBounds() );
    buRight.setText( "Right" );
    cb.setRight( buRight );
    cb.setRightWidth( 100 );
    cb.setRightMinimumSize( new Point( 50, RWT.DEFAULT ) );
    // bottom
    // Button buBottom = new Button(cb, RWT.NONE);
    // buBottom.setText("Bottom");
    // cb.setBottom(buBottom);
    // checkbox to switch between classical and new design
    final Button chkSimple = new Button( comp, RWT.CHECK );
    chkSimple.setText( "Classic" );
    chkSimple.setSelection( true );
    chkSimple.addSelectionListener( new SelectionListener() {

      public void widgetSelected( SelectionEvent e ) {
        cb.setSimple( chkSimple.getSelection() );
        // log("Simple " + (buSimple.getSelection() ? "on" : "off"));
      }
    } );
  }

  /** Creates a tab to display a ViewFormLayout */
  private void createTabViewFormLayout( TabFolder folder ) {
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

  // FOOT PART
  private void createFootPart( Composite parent ) {
    FillLayout footLayout = new FillLayout();
    parent.setLayout( footLayout );
    text = new Text( parent, RWT.MULTI );
    text.setText( "" );
  }

}
