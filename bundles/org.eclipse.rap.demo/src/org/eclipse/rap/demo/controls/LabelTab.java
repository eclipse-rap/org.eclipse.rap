/**
 * 
 */
package org.eclipse.rap.demo.controls;

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
    createFontChooser( new Control[] { label1, label2, label3 } );
  }

  void createExampleControls( Composite top ) {
    top.setLayout( new RowLayout() );
    int style = getStyle();
    label1 = new Label( top, style );
    label1.setText( "Eins " );
    label2 = new Label( top, style );
    label2.setText( "Zwei" );
    label3 = new Label( top, style );
    label3.setText( "Drei" );
  }

}
