/**
 * 
 */
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class LabelTab extends ExampleTab {

  public LabelTab( TabFolder parent ) {
    super( parent, "Label" );
  }

  void createStyleControls( ) {
    createStyleButton( "BORDER" );
    createStyleButton( "SEPARATOR" );
  }

  void createExampleControls( Composite top ) {
    top.setLayout( new RowLayout() );
    int style = getStyle();
    Label label1 = new Label( top, style );
    label1.setText( "Eins " );
    Label label2 = new Label( top, style );
    label2.setText( "Zwei" );
    Label label3 = new Label( top, style );
    label3.setText( "Drei" );
  }

}
