/**
 * 
 */
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class LabelTab extends ExampleTab {

  private Label label1;
  private Label label2;
  private Label label3;

  public LabelTab( TabFolder parent ) {
    super( parent, "Label" );
  }

  void createStyleControls( ) {
    createStyleButton( "BORDER" );
    createStyleButton( "SEPARATOR" );
    createVisibilityButton( );
    createEnablementButton( );
    createFgColorButton();
    createBgColorButton();
    createFontChooser();
  }

  void createExampleControls( Composite top ) {
    top.setLayout( new RowLayout() );
    int style = getStyle();
    RowData data = new RowData( 80, 20 );
    label1 = new Label( top, style );
    label1.setText( "Lable One" );
    label1.setLayoutData( data );
    label2 = new Label( top, style );
    label2.setText( "Label Two" );
    label2.setLayoutData( data );
    label3 = new Label( top, style );
    label3.setText( "Label Three" );
    label3.setLayoutData( data );
    registerControl( label1 );
    registerControl( label2 );
    registerControl( label3 );
  }

}
