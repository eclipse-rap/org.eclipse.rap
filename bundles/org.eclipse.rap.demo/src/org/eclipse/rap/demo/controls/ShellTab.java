/**
 * 
 */
package org.eclipse.rap.demo.controls;

import java.util.ArrayList;
import java.util.Iterator;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.events.SelectionListener;
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
    openShellButton.addSelectionListener( new SelectionListener() {
      public void widgetSelected( SelectionEvent event ) {
        createShell(style);
      }} );

    Button closeAllButton = new Button( top, RWT.PUSH );
    closeAllButton.setText( "Close All Shells" );
    closeAllButton.setLayoutData( new RowData( 150, 25 ) );
    closeAllButton.addSelectionListener( new SelectionListener() {
      public void widgetSelected( SelectionEvent event ) {
        closeShells(style);
      }} );
  }

  private void createShell(int style) {
    final Shell shell = new Shell( folder.getDisplay(), style );
    shell.setBounds( 100, 100, 300, 200 );
    Button closeButton = new Button( shell, RWT.PUSH );
    closeButton.setText( "Close" );
    closeButton.setBounds( 100, 150, 100, 25 );
    closeButton.addSelectionListener( new SelectionListener() {
      public void widgetSelected( SelectionEvent event ) {
        shell.close();
        shell.dispose();
      }} );
    shells.add( shell );
  }

  private void closeShells( int style ) {
    Iterator iter = shells.iterator();
    while( iter.hasNext() ) {
      Shell shell = ( Shell )iter.next();
      shell.close();
      shell.dispose();
    }
    shells.clear();
  }

}
