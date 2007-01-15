/**
 * 
 */
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class SashTab extends ExampleTab {

  public SashTab( TabFolder parent ) {
    super( parent, "Sash" );
  }

  void createStyleControls( ) {
    createStyleButton( "BORDER" );
    createStyleButton( "SMOOTH" );
    createStyleButton( "VERTICAL" );
    createStyleButton( "HORIZONTAL" );
  }

  void createExampleControls( Composite top ) {
    top.setLayout( new RowLayout() );
    int style = getStyle();
    Label label = new Label( top, RWT.BORDER );
    label.setText( "Label" );
    new Sash( top, style );
    Label label2 = new Label( top, RWT.BORDER );
    label2.setText( "Label" );
  }

}
