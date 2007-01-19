/**
 * 
 */
package org.eclipse.rap.demo.controls;

import java.util.ArrayList;
import java.util.Iterator;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class ShellTab extends ExampleTab {

  private ArrayList shells;

  public ShellTab( TabFolder folder ) {
    super( folder, "Shell" );
    shells = new ArrayList();
  }

  void createStyleControls( ) {
    createStyleButton( "BORDER" );
    createStyleButton( "SHELL_TRIM" );
    createStyleButton( "DIALOG_TRIM" );
    createStyleButton( "TITLE" );
    createStyleButton( "MIN" );
    createStyleButton( "MAX" );
    createStyleButton( "CLOSE" );
    createStyleButton( "RESIZE" );
    createStyleButton( "TOOL" );
    createStyleButton( "ON_TOP" );
  }

  void createExampleControls( final Composite top ) {
    top.setLayout( new RowLayout( RWT.VERTICAL ) );
    final int style = getStyle();
    
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

  private void createShell( int style ) {
    final Shell shell = new Shell( folder.getDisplay(), style );
    shell.setBounds( 100, 100, 300, 200 );
    Button closeButton = new Button( shell, RWT.PUSH );
    closeButton.setText( "Close" );
    closeButton.setBounds( 100, 100, 100, 25 );
    closeButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        shell.close();
        shell.dispose();
      }
    } );
    shell.setText( "Test Shell" );
    shell.open();
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
