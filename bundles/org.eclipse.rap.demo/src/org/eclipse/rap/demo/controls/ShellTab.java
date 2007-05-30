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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

public class ShellTab extends ExampleTab {

  private static final String ICON_IMAGE_PATH = "resources/newfile_wiz.gif";
  
  private java.util.List shells;
  private boolean createInvisible = false;
  private boolean createAsDialog = false;
  private boolean createMenu = false;
  private boolean showClientArea = false;
  private Image shellImage;

  public ShellTab( final TabFolder folder ) {
    super( folder, "Shell" );
    shells = new ArrayList();
    setDefaultStyle( SWT.SHELL_TRIM );
  }

  protected void createStyleControls( ) {
    createStyleButton( "BORDER" );
    createStyleButton( "SHELL_TRIM", true );
    createStyleButton( "DIALOG_TRIM" );
    createStyleButton( "APPLICATION_MODAL" );
    createStyleButton( "TITLE" );
    createStyleButton( "MIN" );
    createStyleButton( "MAX" );
    createStyleButton( "CLOSE" );
    createStyleButton( "RESIZE" );
    createStyleButton( "TOOL" );
    createStyleButton( "ON_TOP" );
    final Button invisibleButton = createPropertyButton( "Create invisible" );
    invisibleButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        createInvisible = invisibleButton.getSelection();
      }
    } );
    final Button dialogButton = createPropertyButton( "Create as dialog" );
    dialogButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        createAsDialog = dialogButton.getSelection();
      }
    } );
    final Button menuButton = createPropertyButton( "Add menu" );
    menuButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        createMenu = menuButton.getSelection();
      }
    } );
    final Button clientAreaButton = createPropertyButton( "Show client area" );
    clientAreaButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showClientArea = clientAreaButton.getSelection();
      }
    } );
    // TODO [rh] uncomment once changing images on Shell works 
//    String text = "Show/hide image";
//    final Button showHideImage = createPropertyButton( text, SWT.PUSH );
//    String toolTip 
//      = "Shows or hides the image of the most recently opened shell";
//    showHideImage.setToolTipText( toolTip );
//    showHideImage.addSelectionListener( new SelectionAdapter() {
//      public void widgetSelected( final SelectionEvent e ) {
//        if( shells.size() > 0 ) {
//          Shell shell = ( Shell )shells.get( shells.size() - 1 );
//          if( shell.getImage() == null ) {
//            shell.setImage( shellImage );
//          } else {
//            shell.setImage( null );
//          }
//        }
//      }
//    } );
  }

  protected void createExampleControls( final Composite top ) {
    top.setLayout( new RowLayout( SWT.VERTICAL ) );
    if( shellImage == null ) {
      ClassLoader classLoader = getClass().getClassLoader();
      shellImage = Image.find( ICON_IMAGE_PATH, classLoader );
    }
    Button openShellButton = new Button( top, SWT.PUSH );
    openShellButton.setText( "Open Shell" );
    openShellButton.setLayoutData( new RowData( 150, 25 ) );
    openShellButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        createShell();
      }} );

    Button showAllButton = new Button( top, SWT.PUSH );
    showAllButton.setText( "Show All Shells" );
    showAllButton.setLayoutData( new RowData( 150, 25 ) );
    showAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        setShellsVisible( true );
      }
    } );

    Button hideAllButton = new Button( top, SWT.PUSH );
    hideAllButton.setText( "Hide All Shells" );
    hideAllButton.setLayoutData( new RowData( 150, 25 ) );
    hideAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        setShellsVisible( false );
      }
    } );
    
    Button enableAllButton = new Button( top, SWT.PUSH );
    enableAllButton.setText( "Enable All Shells" );
    enableAllButton.setLayoutData( new RowData( 150, 25 ) );
    enableAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        setShellsEnabled( true );
      }
    } );

    Button disableAllButton = new Button( top, SWT.PUSH );
    disableAllButton.setText( "Disable All Shells" );
    disableAllButton.setLayoutData( new RowData( 150, 25 ) );
    disableAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        setShellsEnabled( false );
      }
    } );
    
    Button closeAllButton = new Button( top, SWT.PUSH );
    closeAllButton.setText( "Close All Shells" );
    closeAllButton.setLayoutData( new RowData( 150, 25 ) );
    closeAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        closeShells();
      }} );
  }

  private void createShell() {
    final int style = getStyle();
    final Shell shell;
    if( createAsDialog ) {
      shell = new Shell( getShell(), style );      
    } else {
      shell = new Shell( getShell().getDisplay(), style );
    }
    shell.setLocation( getNextShellLocation() );
    if( true ) {
      createShellContents1( shell );      
    } else {
      createShellContents2( shell );      
    }
    int num = shells.size() + 1;
    shell.setText( "Test Shell " + num );
    shell.setImage( shellImage );
    if( !createInvisible ) {
      shell.open();
    }
    shells.add( shell );
  }
  
  /*
   * Creates a shell with a size of 300 x 200 px and displays the bounds of its
   * client area.
   */
  private void createShellContents1( final Shell shell ) {
    shell.setSize( 300, 200 );
    if( createMenu ) {
      createMenuBar( shell );
    }
    final Composite comp1 = new Composite( shell, SWT.NONE );
    final Composite comp2 = new Composite( shell, SWT.NONE );
    comp2.moveAbove( comp1 );
    if( showClientArea ) {
      comp1.setBackground( Color.getColor( 200, 0, 0 ) );
      comp2.setBackground( Color.getColor( 200, 200, 200 ) );
    }
    Rectangle ca = shell.getClientArea();
    comp1.setBounds( ca.x, ca.y, ca.width, ca.height );
    comp2.setBounds( ca.x + 1, ca.y + 1, ca.width - 2, ca.height - 2 );
    Button closeButton = new Button( shell, SWT.PUSH );
    closeButton.setText( "Close This Window" );
    closeButton.moveAbove( comp2 );
    int centerX = ( ca.width - ca.x ) / 2;
    closeButton.setBounds( centerX - 55, ca.height - 45, 110, 25 );
    closeButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        shell.close();
      }
    } );
    shell.addControlListener( new ControlAdapter() {
      public void controlResized( final ControlEvent event ) {
        Rectangle ca = shell.getClientArea();
        comp1.setBounds( ca.x, ca.y, ca.width, ca.height );
        comp2.setBounds( ca.x + 1, ca.y + 1, ca.width - 2, ca.height - 2 );
      }
    } );
  }

  /*
   * Alternative implementation:
   * Creates a shell with that contains only a button with a predefined size of
   * 140 x 40 px. Can be used to test the Shell.computeTrim mehtod.
   */
  private void createShellContents2( final Shell shell ) {
    if( createMenu ) {
      createMenuBar( shell );
    }
    RowLayout layout = new RowLayout();
    layout.marginLeft = 0;
    layout.marginTop = 0;
    layout.marginRight = 0;
    layout.marginBottom = 0;
    shell.setLayout( layout );
    Button closeButton = new Button( shell, SWT.PUSH );
    closeButton.setText( "Close This Window" );
    closeButton.setBackground( Color.getColor( 25, 55, 55 ) );
    closeButton.setLayoutData( new RowData( 140, 40 ) );
    closeButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        shell.close();
      }
    } );
  }
  
  private void createMenuBar( final Shell shell ) {
    // menu bar
    Menu menuBar = new Menu( shell, SWT.BAR );
    shell.setMenuBar( menuBar );
    MenuItem fileItem = new MenuItem( menuBar, SWT.CASCADE );
    fileItem.setText( "File" );
    MenuItem editItem = new MenuItem( menuBar, SWT.CASCADE );
    editItem.setText( "Edit" );
    MenuItem searchItem = new MenuItem( menuBar, SWT.CASCADE );
    searchItem.setText( "Search" );
    MenuItem disabledItem = new MenuItem( menuBar, SWT.CASCADE );
    disabledItem.setText( "Disabled" );
    disabledItem.setEnabled( false );
    new MenuItem( menuBar, SWT.CASCADE ).setText( "Item 6" );
    new MenuItem( menuBar, SWT.CASCADE ).setText( "Item 7" );
    new MenuItem( menuBar, SWT.CASCADE ).setText( "Item 8" );
    new MenuItem( menuBar, SWT.CASCADE ).setText( "Item 9" );
    // file menu
    Menu fileMenu = new Menu( shell, SWT.DROP_DOWN );
    fileItem.setMenu( fileMenu );
    MenuItem newItem = new MenuItem( fileMenu, SWT.PUSH );
    newItem.setText( "New" );
    newItem.setImage( Image.find( "resources/newfile_wiz.gif" ) );
    new MenuItem( fileMenu, SWT.PUSH ).setText( "Open" );
    new MenuItem( fileMenu, SWT.PUSH ).setText( "Close" );
    // edit menu
    Menu editMenu = new Menu( shell, SWT.DROP_DOWN );
    editItem.setMenu( editMenu );
    MenuItem item;
    new MenuItem( editMenu, SWT.PUSH ).setText( "Copy" );
    new MenuItem( editMenu, SWT.PUSH ).setText( "Paste" );
    new MenuItem( editMenu, SWT.SEPARATOR );
    // cascade menu
    item = new MenuItem( editMenu, SWT.CASCADE );
    item.setText( "Insert" );
    Menu cascadeMenu = new Menu( shell, SWT.DROP_DOWN );
    item.setMenu( cascadeMenu );
    new MenuItem( cascadeMenu, SWT.PUSH ).setText( "Date" );
    new MenuItem( cascadeMenu, SWT.PUSH ).setText( "Line Break" );
    // search 
    Menu searchMenu = new Menu( shell, SWT.DROP_DOWN );
    searchItem.setMenu( searchMenu );
    new MenuItem( searchMenu, SWT.PUSH ).setText( "Enabled" );
    item = new MenuItem( searchMenu, SWT.PUSH );
    item.setText( "Disabled" );
    item.setEnabled( false );
    new MenuItem( searchMenu, SWT.PUSH ).setText( "Push" );
    new MenuItem( searchMenu, SWT.SEPARATOR );
    item = new MenuItem( searchMenu, SWT.CHECK );
    item.setText( "Check" );
    item = new MenuItem( searchMenu, SWT.RADIO );
    item.setText( "Radio 1" );
    item = new MenuItem( searchMenu, SWT.RADIO );
    item.setText( "Radio 2" );
    item = new MenuItem( searchMenu, SWT.RADIO );
    item.setText( "Radio 3" );
    item.setEnabled( false );
    // disabled
    Menu disabledMenu = new Menu( shell, SWT.DROP_DOWN );
    disabledMenu.setEnabled( false );
    disabledItem.setMenu( disabledMenu );
    new MenuItem( disabledMenu, SWT.PUSH ).setText( "Import" );
    new MenuItem( disabledMenu, SWT.PUSH ).setText( "Export" );
  }
  
  private Point getNextShellLocation() {
    Point result = getShell().getLocation();
    int count = shells.size() % 12;
    result.x += 50 + count * 10;
    result.y += 50 + count * 10;
    return result ;
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
  
  private void setShellsVisible( final boolean visible ) {
    Iterator iter = shells.iterator();
    while( iter.hasNext() ) {
      Shell shell = ( Shell )iter.next();
      shell.setVisible( visible );
    }
  }
  
  private void setShellsEnabled( final boolean enabled ) {
    Iterator iter = shells.iterator();
    while( iter.hasNext() ) {
      Shell shell = ( Shell )iter.next();
      shell.setEnabled( enabled );
    }
  }

}
