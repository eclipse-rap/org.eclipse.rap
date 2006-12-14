/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.demo;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.custom.CBanner;
import org.eclipse.rap.rwt.custom.SashForm;
import org.eclipse.rap.rwt.custom.StackLayout;
import org.eclipse.rap.rwt.custom.ViewForm;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.events.SelectionListener;
import org.eclipse.rap.rwt.graphics.Color;
import org.eclipse.rap.rwt.graphics.Point;
import org.eclipse.rap.rwt.layout.FillLayout;
import org.eclipse.rap.rwt.layout.GridData;
import org.eclipse.rap.rwt.layout.GridLayout;
import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.lifecycle.IEntryPoint;
import org.eclipse.rap.rwt.widgets.Button;
import org.eclipse.rap.rwt.widgets.Composite;
import org.eclipse.rap.rwt.widgets.Display;
import org.eclipse.rap.rwt.widgets.Label;
import org.eclipse.rap.rwt.widgets.Shell;
import org.eclipse.rap.rwt.widgets.TabFolder;
import org.eclipse.rap.rwt.widgets.TabItem;
import org.eclipse.rap.rwt.widgets.Text;
import org.eclipse.rap.rwt.widgets.ToolBar;
import org.eclipse.rap.rwt.widgets.ToolItem;

public class LayoutDemo implements IEntryPoint {

  private Text text;
  private Button buRight;

  public Display createUI() {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.NONE );
    shell.setBounds( 10, 10, 500, 300 );
    shell.setLayout( new FillLayout() );
    SashForm sashForm = new SashForm( shell, RWT.VERTICAL );
    Composite compMain = new Composite( sashForm, RWT.NONE );
    Composite compFoot = new Composite( sashForm, RWT.NONE );
    sashForm.setWeights( new int[]{
      70, 30
    } );
    createMainPart( compMain );
    createFootPart( compFoot );
    // shell.layout();
    // System.out.println( "after layput: " + buRight.getBounds() );s
    return display;
  }

  // MAIN PART
  private void createMainPart( Composite parent ) {
    parent.setLayout( new FillLayout() );
    final TabFolder topFolder = new TabFolder( parent, RWT.NONE );
    createTabStackedLayout( topFolder );
    createTabCBannerLayout( topFolder );
    createTabViewFormLayout( topFolder );
    createTabColor( topFolder );
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

  /** Creates a tab to display a Color */
  private void createTabColor( TabFolder folder ) {
    TabItem item = new TabItem( folder, RWT.NONE );
    item.setText( "Color" );
    Composite comp = new Composite( folder, RWT.NONE );
    item.setControl( comp );
    comp.setLayout( new RowLayout() );
    // colors
    final int count = 3;
    final Color[] bgColors = new Color[count];
    final Color[] fgColors = new Color[count];
    bgColors[0] = Color.getColor(139, 37, 0);
    bgColors[1] = Color.getColor(105, 89, 205);
    bgColors[2] = Color.getColor(139, 121, 94);
    fgColors[0] = Color.getColor(255, 140, 0);
    fgColors[1] = Color.getColor(255, 215, 0);
    fgColors[2] = Color.getColor(154, 205, 50);
    // label
    final Label label = new Label( comp, RWT.WRAP );
    label.setLayoutData( new RowData( 100, 100 ) );
    label.setText( "Label" ); // TODO: Label without text fails!
    label.setBackground( bgColors[0] );
    label.setForeground( bgColors[1] );
    // button
    final Button button = new Button( comp, RWT.PUSH );
    button.setLayoutData( new RowData( 100, 100 ) );
    button.setText( "Button" );
    button.setBackground( bgColors[0] );
    button.setForeground( Color.getColor( 0, 128, 0 ) );
    // switch foreground button
    Button switchFgButton = new Button( comp, RWT.PUSH );
    switchFgButton.setText( "Switch Foreground" );
    switchFgButton.setLayoutData( new RowData( 100, 30 ) );
    final int fgIndex[] = { 0 };
    switchFgButton.addSelectionListener( new SelectionListener() {
      public void widgetDefaultSelected( final SelectionEvent e ) { 
        // do nothing
      }
      public void widgetSelected( SelectionEvent e ) {
        int i = ++fgIndex[0] % count;
        label.setForeground( fgColors[i] );
        button.setForeground( fgColors[i] );
      }
    } );
    // switch background button
    Button switchBgButton = new Button( comp, RWT.PUSH );
    switchBgButton.setText( "Switch Background" );
    switchBgButton.setLayoutData( new RowData( 100, 30 ) );
    final int bgIndex[] = { 0 };
    switchBgButton.addSelectionListener( new SelectionListener() {
      public void widgetDefaultSelected( SelectionEvent e ) { 
        // do nothing
      }
      public void widgetSelected( SelectionEvent e ) {
        int i = ++bgIndex[0] % count;
        label.setBackground( bgColors[i] );
        button.setBackground( bgColors[i] );
      }
    } );
    
  }

  // FOOT PART
  private void createFootPart( Composite parent ) {
    FillLayout footLayout = new FillLayout();
    parent.setLayout( footLayout );
    text = new Text( parent, RWT.MULTI );
    text.setText( "" );
  }

  //	private void log(String msg) {
  //		text.append(msg + text.getLineDelimiter());
  //	}
  //
  //	private void clear() {
  //		text.setText("");
  //	}

}
