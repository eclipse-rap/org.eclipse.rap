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

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

public class ShellTab extends ExampleTab {

  private static final String ICON_IMAGE_PATH = "resources/newfile_wiz.gif";

  private java.util.List shells;
  private Image shellImage;

  private Button createInvisibleButton;
  private Button createAsDialogButton;
  private Button createWithMenuButton;
  private Button showClientAreaButton;
//  private Button customBgColorButton;

  public ShellTab( final CTabFolder topFolder ) {
    super( topFolder, "Shell" );
    shells = new ArrayList();
    setDefaultStyle( SWT.SHELL_TRIM );
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "SHELL_TRIM", SWT.SHELL_TRIM, true );
    createStyleButton( "DIALOG_TRIM", SWT.DIALOG_TRIM );
    createStyleButton( "APPLICATION_MODAL", SWT.APPLICATION_MODAL );
    createStyleButton( "TITLE", SWT.TITLE );
    createStyleButton( "MIN", SWT.MIN );
    createStyleButton( "MAX", SWT.MAX );
    createStyleButton( "CLOSE", SWT.CLOSE );
    createStyleButton( "RESIZE", SWT.RESIZE );
//    createStyleButton( "TOOL", SWT.TOOL );
    createStyleButton( "ON_TOP", SWT.ON_TOP );
    createInvisibleButton = createPropertyButton( "Create invisible" );
    createAsDialogButton = createPropertyButton( "Create as dialog" );
    createWithMenuButton = createPropertyButton( "Add menu" );
    showClientAreaButton = createPropertyButton( "Show client area" );
//    customBgColorButton = createPropertyButton( "Custom background" );
  }

  protected void createExampleControls( final Composite top ) {
    top.setLayout( new RowLayout( SWT.VERTICAL ) );
    if( shellImage == null ) {
      ClassLoader classLoader = getClass().getClassLoader();
      shellImage = Graphics.getImage( ICON_IMAGE_PATH, classLoader );
    }
    Button openShellButton = new Button( top, SWT.PUSH );
    openShellButton.setText( "Open Shell" );
    openShellButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        createShell();
      }} );

    Button showAllButton = new Button( top, SWT.PUSH );
    showAllButton.setText( "Show All Shells" );
    showAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        setShellsVisible( true );
      }
    } );

    Button hideAllButton = new Button( top, SWT.PUSH );
    hideAllButton.setText( "Hide All Shells" );
    hideAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        setShellsVisible( false );
      }
    } );

    Button MaximizeAllButton = new Button( top, SWT.PUSH );
    MaximizeAllButton.setText( "Maximize All Shells" );
    MaximizeAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        setShellsMaximized( true );
      }
    } );

    Button minimizeAllButton = new Button( top, SWT.PUSH );
    minimizeAllButton.setText( "Minimize All Shells" );
    minimizeAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        setShellsMinimized( true );
      }
    } );

    Button restoreAllButton = new Button( top, SWT.PUSH );
    restoreAllButton.setText( "Restore All Shells" );
    restoreAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        setShellsMinimized( false );
        setShellsMaximized( false );
      }
    } );

    Button enableAllButton = new Button( top, SWT.PUSH );
    enableAllButton.setText( "Enable All Shells" );
    enableAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        setShellsEnabled( true );
      }
    } );

    Button disableAllButton = new Button( top, SWT.PUSH );
    disableAllButton.setText( "Disable All Shells" );
    disableAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        setShellsEnabled( false );
      }
    } );

    Button closeAllButton = new Button( top, SWT.PUSH );
    closeAllButton.setText( "Close All Shells" );
    closeAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        closeShells();
      }} );
  }

  private void createShell() {
    final int style = getStyle();
    final Shell shell;
    if( createAsDialogButton.getSelection() ) {
      shell = new Shell( getShell(), style );
    } else {
      shell = new Shell( getShell().getDisplay(), style );
    }
//    if( customBgColorButton.getSelection() ) {
//      shell.setBackground( BG_COLOR_BROWN );
//    }
    shell.setLocation( getNextShellLocation() );
    if( true ) {
      createShellContents1( shell );
    } else {
      createShellContents2( shell );
    }
    int num = shells.size() + 1;
    shell.setText( "Test Shell " + num );
    shell.setImage( shellImage );
    if( !createInvisibleButton.getSelection() ) {
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
    if( createWithMenuButton.getSelection() ) {
      createMenuBar( shell );
    }
    final Composite comp1 = new Composite( shell, SWT.NONE );
    final Composite comp2 = new Composite( shell, SWT.NONE );
    comp2.moveAbove( comp1 );
    if( showClientAreaButton.getSelection() ) {
      comp1.setBackground( Graphics.getColor( 200, 0, 0 ) );
      comp2.setBackground( Graphics.getColor( 200, 200, 200 ) );
    }
    Rectangle ca = shell.getClientArea();
    comp1.setBounds( ca.x, ca.y, ca.width, ca.height );
    comp2.setBounds( ca.x + 1, ca.y + 1, ca.width - 2, ca.height - 2 );
    Button closeButton = new Button( shell, SWT.PUSH );
    closeButton.setText( "Close This Window" );
    closeButton.pack();
    closeButton.moveAbove( comp2 );
    int centerX = ( ca.width - ca.x ) / 2;
    closeButton.setLocation( centerX - closeButton.getSize().x / 2, ca.height - 45 );
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
    if( createWithMenuButton.getSelection() ) {
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
    closeButton.setBackground( Graphics.getColor( 25, 55, 55 ) );
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
    newItem.setImage( Graphics.getImage( "resources/newfile_wiz.gif" ) );
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

  private void setShellsMinimized( final boolean minimized ) {
    Iterator iter = shells.iterator();
    while( iter.hasNext() ) {
      Shell shell = ( Shell )iter.next();
      shell.setMinimized( minimized );
    }
  }

  private void setShellsMaximized( final boolean maximized ) {
    Iterator iter = shells.iterator();
    while( iter.hasNext() ) {
      Shell shell = ( Shell )iter.next();
      shell.setMaximized( maximized );
    }
  }

}
