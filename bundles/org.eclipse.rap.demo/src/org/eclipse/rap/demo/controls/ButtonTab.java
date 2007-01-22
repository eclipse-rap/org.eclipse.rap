/**
 * 
 */
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionAdapter;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class ButtonTab extends ExampleTab {

  private static final String BUTTON_IMAGE
    = "org/eclipse/rap/demo/controls/button-image.gif";
  
  private Button button1;
  private Button check1;
  private Button radio1;
  private Button radio2;
  private Button radio3;

  public ButtonTab( TabFolder folder ) {
    super( folder, "Button" );
  }

  void createStyleControls() {
    createStyleButton( "BORDER" );
    createStyleButton( "FLAT" );
    createStyleButton( "LEFT" );
    createStyleButton( "CENTER" );
    createStyleButton( "RIGHT" );
    createVisibilityButton();
    createEnablementButton();
    final Button imageButton = new Button( styleComp, RWT.CHECK );
    imageButton.setText( "Push Button with Image" );
    imageButton.setLayoutData( new RowData( 80, 20 ) );
    imageButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        if( imageButton.getSelection() ) {
          ClassLoader classLoader = getClass().getClassLoader();
          button1.setImage( Image.find( BUTTON_IMAGE, classLoader ) );
        } else {
          button1.setImage( null );
        }
      }
    } );
    createFontChooser();
  }

  void createExampleControls( Composite top ) {
    top.setLayout( new RowLayout( RWT.VERTICAL ) );
    int style = getStyle();
    RowData data = new RowData( 100, 25 );
    button1 = new Button( top, style | RWT.PUSH );
    button1.setText( "Button" );
    button1.setLayoutData( data );
//    button1.addSelectionListener( new SelectionAdapter() {
//      public void widgetSelected( SelectionEvent event ) {
//        log( "Button 1 pressed" );
//      }
//    } );
    check1 = new Button( top, style | RWT.CHECK );
    check1.setText( "Check" );
    check1.setLayoutData( data );
    radio1 = new Button( top, style | RWT.RADIO );
    radio1.setText( "Radio 1" );
    radio1.setLayoutData( data );
    radio2 = new Button( top, style | RWT.RADIO );
    radio2.setText( "Radio 2" );
    radio2.setLayoutData( data );
    radio3 = new Button( top, style | RWT.RADIO );
    radio3.setText( "Radio 3" );
    radio3.setLayoutData( data );
    registerControl( button1 );
    registerControl( check1 );
    registerControl( radio1 );
    registerControl( radio2 );
    registerControl( radio3 );
  }

}
