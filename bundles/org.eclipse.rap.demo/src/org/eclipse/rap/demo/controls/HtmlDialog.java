package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.browser.Browser;
import org.eclipse.rap.rwt.events.SelectionAdapter;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.widgets.*;

public class HtmlDialog {

  private Shell shell;
  private final String text;

  public HtmlDialog( final Shell parent, final String title, final String text )
  {
    int style = RWT.APPLICATION_MODAL | RWT.DIALOG_TRIM | RWT.RESIZE;
    shell = new Shell( parent, style );
    this.text = text;
    shell.setText( title  );
    shell.setBounds( 200, 50, 400, 600 );
    shell.setLayout( new FillLayout() );
    Composite composite = new Composite( shell, RWT.NONE );
    createContents( composite );
  }

  public void open() {
    shell.layout();
    shell.open();
  }
  
  public void close() {
    shell.close();
  }
  
  public void setSize( final int width, final int height ) {
    shell.setSize( width, height );
  }
  
  public void setLocation( final int x, final int y ) {
    shell.setLocation( x, y );
  }
  
  private void createContents( final Composite parent ) {
    Layout layout = new GridLayout();
    parent.setLayout( layout );
    // browser
    Browser browser = new Browser( parent, RWT.BORDER );
    browser.setText( text );
    GridData browserData = new GridData( GridData.FILL_BOTH );
    browser.setLayoutData( browserData );
    // button bar
    Button closeButton = new Button( parent, RWT.PUSH );
    closeButton.setText( "Close" );
    shell.setDefaultButton( closeButton );
    closeButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        close();
      }
    } );
    GridData buttonData = new GridData( GridData.HORIZONTAL_ALIGN_END );
    closeButton.setLayoutData( buttonData );
  }
}
