/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import java.util.ArrayList;
import java.util.Iterator;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.graphics.*;
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.widgets.*;

public class ShellTab extends ExampleTab {

  private static final String ICON_IMAGE_PATH = "resources/newfile_wiz.gif";
  private ArrayList shells;
  private boolean invisible = false;
  private Image shellIconImage;

  public ShellTab( TabFolder folder ) {
    super( folder, "Shell" );
    shells = new ArrayList();
  }

  void createStyleControls( ) {
    createStyleButton( "BORDER" );
    createStyleButton( "SHELL_TRIM" );
    createStyleButton( "DIALOG_TRIM" );
    createStyleButton( "APPLICATION_MODAL" );
    createStyleButton( "TITLE" );
    createStyleButton( "MIN" );
    createStyleButton( "MAX" );
    createStyleButton( "CLOSE" );
    createStyleButton( "RESIZE" );
    createStyleButton( "TOOL" );
    createStyleButton( "ON_TOP" );
    final Button invisibleButton = createPropertyButton( "create invisible" );
    invisibleButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        invisible = invisibleButton.getSelection();
      }
    } );
  }

  void createExampleControls( final Composite top ) {
    top.setLayout( new RowLayout( RWT.VERTICAL ) );
    final int style = getStyle();
    
    if( shellIconImage == null ) {
      ClassLoader classLoader = getClass().getClassLoader();
      shellIconImage = Image.find( ICON_IMAGE_PATH, classLoader );
    }
    
    Button openShellButton = new Button( top, RWT.PUSH );
    openShellButton.setText( "Open Shell" );
    openShellButton.setLayoutData( new RowData( 150, 25 ) );
    openShellButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        createShell(style);
      }} );

    Button showAllButton = new Button( top, RWT.PUSH );
    showAllButton.setText( "Show All Shells" );
    showAllButton.setLayoutData( new RowData( 150, 25 ) );
    showAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        setShellsVisible( true );
      }
    } );

    Button hideAllButton = new Button( top, RWT.PUSH );
    hideAllButton.setText( "Hide All Shells" );
    hideAllButton.setLayoutData( new RowData( 150, 25 ) );
    hideAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        setShellsVisible( false );
      }
    } );
    
    Button enableAllButton = new Button( top, RWT.PUSH );
    enableAllButton.setText( "Enable All Shells" );
    enableAllButton.setLayoutData( new RowData( 150, 25 ) );
    enableAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        setShellsEnabled( true );
      }
    } );

    Button disableAllButton = new Button( top, RWT.PUSH );
    disableAllButton.setText( "Disable All Shells" );
    disableAllButton.setLayoutData( new RowData( 150, 25 ) );
    disableAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        setShellsEnabled( false );
      }
    } );
    
    Button closeAllButton = new Button( top, RWT.PUSH );
    closeAllButton.setText( "Close All Shells" );
    closeAllButton.setLayoutData( new RowData( 150, 25 ) );
    closeAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        closeShells();
      }} );
  }

//  private void createShell( int style ) {
//    final Shell shell = new Shell( folder.getDisplay(), style );
////    shell.setBounds( 100, 100, 300, 200 );
//    shell.setLocation( 100, 100 );
//    shell.setLayout( new RowLayout() );
//    // added composite to be able to make the bounds of the client area visible
////    Composite comp = new Composite( shell, RWT.NONE );
////    comp.setBackground( Color.getColor( 255, 255, 255 ) );
////    comp.setLayout( new FormLayout() );
//    Button closeButton = new Button( shell, RWT.PUSH );
//    closeButton.setText( "Close This Window" );
//    closeButton.setBackground( Color.getColor( 25, 55, 55 ) );
////    FormData formData = new FormData();
////    formData.height = 25;
////    formData.left = new FormAttachment( 30 );
////    formData.right = new FormAttachment( 70 );
////    formData.bottom = new FormAttachment( 80 );
////    formData.width = 100;
////    formData.height = 25;
////    closeButton.setLayoutData( formData );
//    closeButton.setLayoutData( new RowData( 200, 80 ) );
//    closeButton.addSelectionListener( new SelectionAdapter() {
//      public void widgetSelected( SelectionEvent event ) {
//        shell.close();
//      }
//    } );
//    int num = shells.size() + 1;
//    shell.setText( "Test Shell " + num );
//    shell.setImage( shellIconImage );
////    shell.setSize( 300, 200 );
////    shell.layout();
////    Point pref = shell.computeSize( RWT.DEFAULT, RWT.DEFAULT, true );
//    shell.pack();
//    if( !invisible ) {
//      shell.open();
//    }
//    shells.add( shell );
//  }

  private void createShell( int style ) {
    final Shell shell = new Shell( folder.getDisplay(), style );
//    shell.setLayout( new FillLayout() );
    shell.setBounds( 100, 100, 300, 200 );
    final Composite comp = new Composite( shell, RWT.NONE );
    comp.setBackground( Color.getColor( 25, 55, 55 ) );
    Rectangle ca = shell.getClientArea();
    comp.setBounds( ca.x, ca.y, ca.width, ca.height );
//    comp.setLayoutData( new RowData( 200, 200 ) );
//    Button closeButton = new Button( comp, RWT.PUSH );
//    closeButton.setText( "Close This Window" );
//    closeButton.setBackground( Color.getColor( 55, 55, 55 ) );
//    closeButton.setLayoutData( new RowData( 200, 80 ) );
//    closeButton.addSelectionListener( new SelectionAdapter() {
//      public void widgetSelected( SelectionEvent event ) {
//        shell.close();
//      }
//    } );
    shell.addControlListener( new ControlAdapter() {
      public void controlResized( ControlEvent event ) {
        super.controlResized( event );
        Rectangle ca = shell.getClientArea();
        comp.setBounds( ca.x, ca.y, ca.width, ca.height );
      }
    } );
    int num = shells.size() + 1;
    shell.setText( "Test Shell " + num );
    shell.setImage( shellIconImage );
//    shell.pack();
    shell.layout();
    if( !invisible ) {
      shell.open();
    }
    shells.add( shell );
  }

  private void closeShells() {
    Iterator iter = shells.iterator();
    while( iter.hasNext() ) {
      Shell shell = ( Shell )iter.next();
      shell.close();
      shell.dispose();
    }
    shells.clear();
  }
  
  private void setShellsVisible( boolean visible ) {
    Iterator iter = shells.iterator();
    while( iter.hasNext() ) {
      Shell shell = ( Shell )iter.next();
      shell.setVisible( visible );
    }
  }
  
  private void setShellsEnabled( boolean enabled ) {
    Iterator iter = shells.iterator();
    while( iter.hasNext() ) {
      Shell shell = ( Shell )iter.next();
      shell.setEnabled( enabled );
    }
  }

}
