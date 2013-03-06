/*******************************************************************************
 * Copyright (c) 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;


public class MnemonicsTab extends ExampleTab {

  private static final String DEFAULT_ACTIVATOR = "CTRL+ALT";
  private static final String[] DEFAULT_SHORTCUT = new String[]{ "CTRL+ALT+Y" };
  protected boolean useCTabFolder;

  public MnemonicsTab() {
    super( "Mnemonics" );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createActivatorControls( parent );
    createShortcutControls( parent );
    createCTabFolderButton( parent );
  }

  private void createShortcutControls( Composite parent ) {
    final Display display = parent.getDisplay();
    display.setData( RWT.ACTIVE_KEYS, DEFAULT_SHORTCUT );
    display.setData( RWT.CANCEL_KEYS, DEFAULT_SHORTCUT );
    final Text shortcutText = new Text( parent, SWT.BORDER );
    shortcutText.setText( DEFAULT_SHORTCUT[ 0 ] );
    shortcutText.setLayoutData( new RowData( 110, SWT.DEFAULT ) );
    Button setShortcut = new Button( parent, SWT.PUSH );
    setShortcut.setText( "set global shortcut" );
    setShortcut.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        String[] shortcut = new String[]{ shortcutText.getText() };
        display.setData( RWT.ACTIVE_KEYS, shortcut );
        display.setData( RWT.CANCEL_KEYS, shortcut );
      }
    } );
    display.addFilter( SWT.KeyDown, new Listener() {
      public void handleEvent( Event event ) {
        log( event.toString() );
      }
    } );
  }

  private void createActivatorControls( Composite parent ) {
    final Display display = parent.getDisplay();
    display.setData( RWT.MNEMONIC_ACTIVATOR, DEFAULT_ACTIVATOR );
    final Text activatorText = new Text( parent, SWT.BORDER );
    activatorText.setText( DEFAULT_ACTIVATOR );
    activatorText.setLayoutData( new RowData( 110, SWT.DEFAULT ) );
    Button setActivator = new Button( parent, SWT.PUSH );
    setActivator.setText( "set activator" );
    setActivator.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        display.setData( RWT.MNEMONIC_ACTIVATOR, activatorText.getText() );
      }
    } );
  }

  private void createCTabFolderButton( Composite parent ) {
    final Button ctabFolderButton = new Button( parent, SWT.TOGGLE );
    ctabFolderButton.setText( "use CTabFolder" );
    ctabFolderButton.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        useCTabFolder = ctabFolderButton.getSelection();
        createNew();
      }
    } );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    parent.setLayout( new FillLayout() );
    if( useCTabFolder ) {
      CTabFolder folder = new CTabFolder( parent, getStyle() );
      CTabItem[] tabItems = new CTabItem[ 3 ];
      for( int i = 0; i < 3; i++ ) {
        tabItems[ i ] = new CTabItem( folder, SWT.NONE );
        tabItems[ i ].setText( "CTabItem &" + ( i + 1) );
        Composite content = createItemContent( folder, i );
        tabItems[ i ].setControl( content );
      }
      folder.setSelection( 0 );
    } else {
      TabFolder folder = new TabFolder( parent, getStyle() );
      TabItem[] tabItems = new TabItem[ 3 ];
      for( int i = 0; i < 3; i++ ) {
        tabItems[ i ] = new TabItem( folder, SWT.NONE );
        tabItems[ i ].setText( "TabItem &" + ( i + 1 ) );
        Composite content = createItemContent( folder, i );
        tabItems[ i ].setControl( content );
      }
    }
  }

  private Composite createItemContent( Composite folder, int index ) {
    Composite content = new Composite( folder, SWT.NONE );
    content.setLayout( new GridLayout( 4, false ) );
    switch( index ) {
      case 0:
        createButtonExample( content );
      break;
      case 1:
        createToolBarExample( content );
      break;
      case 2:
        createMenuExample( content );
      break;
    }
    return content;
  }

  private void createMenuExample( final Composite content ) {
    final Button button = new Button( content, SWT.PUSH );
    button.setText( "Open Shell Menu" );
    button.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        Shell shell = new Shell( content.getShell(), SWT.BORDER  );
        createMenuBar( shell );
        shell.setLocation( content.toDisplay( 0, 0 ) );
        shell.setSize( content.getSize() );
        shell.open();
      }
    } );
  }

  private void createMenuBar( final Shell shell) {
    String[] items = new String[] {
      "&File", "&Edit", "E&xit"
    };
    Menu bar = new Menu( shell, SWT.BAR );
    shell.setMenuBar( bar );
    for( String text : items ) {
      MenuItem menuItem = new MenuItem( bar, SWT.CASCADE );
      menuItem.setText( text );
      Menu dropdown = new Menu( shell, SWT.DROP_DOWN );
      menuItem.setMenu( dropdown );
    }
    createMenuItem( bar.getItem( 0 ).getMenu(), SWT.PUSH, "Push &One" );
    createMenuItem( bar.getItem( 0 ).getMenu(), SWT.PUSH, "Push &Two" );
    createMenuItem( bar.getItem( 0 ).getMenu(), SWT.CHECK, "&Check" );
    createMenuItem( bar.getItem( 0 ).getMenu(), SWT.RADIO, "Radio &X" );
    createMenuItem( bar.getItem( 0 ).getMenu(), SWT.RADIO, "Radio &Y" );
    createMenuItem( bar.getItem( 0 ).getMenu(), SWT.RADIO, "Radio &Z" );
    createMenuItem( bar.getItem( 1 ).getMenu(), SWT.PUSH, "Push &Three" );
    MenuItem casc = createMenuItem( bar.getItem( 1 ).getMenu(), SWT.CASCADE, "&Submenu" );
    Menu submenu = new Menu( shell, SWT.DROP_DOWN );
    casc.setMenu( submenu );
    createMenuItem( submenu, SWT.CHECK, "Ch&eck" );
    createMenuItem( submenu, SWT.RADIO, "Radio &8" );
    createMenuItem( submenu, SWT.RADIO, "Radio &9" );
    MenuItem close = createMenuItem( bar.getItem( 2 ).getMenu(), SWT.PUSH, "Close &Shell" );
    close.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        shell.dispose();
      }
    } );
  }

  private MenuItem createMenuItem( Menu menu, int style, String text ) {
    final MenuItem item = new MenuItem( menu, style );
    item.setText( text );
    item.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event e ) {
        log( item.getText() );
      }
    } );
    return item;
  }

  private void createButtonExample( Composite content ) {
    createButton( content, SWT.PUSH, "Push &One" );
    createButton( content, SWT.PUSH, "Push &Two" );
    createButton( content, SWT.TOGGLE, "To&ggle" );
    createButton( content, SWT.CHECK, "&Checkbox" );
    createButton( content, SWT.RADIO, "Radio &X" );
    createButton( content, SWT.RADIO, "Radio &Y" );
    createButton( content, SWT.RADIO, "Radio &Z" );
  }

  private void createToolBarExample( Composite content ) {
    Label label = new Label( content, SWT.NONE );
    label.setText( "ToolBar:" );
    ToolBar bar = new ToolBar( content, SWT.BORDER );
    createToolItem( bar, SWT.PUSH, "Push &Three" );
    createToolItem( bar, SWT.CHECK, "Toggl&e" );
    createToolItem( bar, SWT.RADIO, "Radio &8" );
    createToolItem( bar, SWT.RADIO, "Radio &9" );
  }

  private void createButton( Composite content, int style, String text ) {
    final Button button = new Button( content, style );
    button.setText( text );
    button.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        log( button.getText() );
      }
    } );
  }

  private void createToolItem( ToolBar bar, int style, String text ) {
    final ToolItem item = new ToolItem( bar, style );
    item.setText( text );
    item.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        log( item.getText() );
      }
    } );
  }
}
