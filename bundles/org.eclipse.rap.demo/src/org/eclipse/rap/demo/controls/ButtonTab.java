/**
 * 
 */
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class ButtonTab extends ExampleTab {

  private Button button1;
  private Button check1;
  private Button radio1;
  private Button radio2;
  private Button radio3;

  public ButtonTab( TabFolder folder ) {
    super( folder, "Button" );
  }

  void createStyleControls( ) {
    createStyleButton( "BORDER" );
    createStyleButton( "FLAT" );
    Control[] controls = new Control[] { 
      button1, 
      check1, 
      radio1, 
      radio2, 
      radio3 
    };
    createFontChooser( controls );
  }

  void createExampleControls( Composite top ) {
    top.setLayout( new RowLayout( RWT.VERTICAL ) );
    int style = getStyle();
    RowData data = new RowData( 100, 25 );
    button1 = new Button( top, style | RWT.PUSH );
    button1.setText( "Button" );
    button1.setLayoutData( data );
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
  }

}
