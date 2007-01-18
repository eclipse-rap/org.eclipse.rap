/**
 * 
 */
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class TextTab extends ExampleTab {

  private Text text1;

  public TextTab( TabFolder folder ) {
    super( folder, "Text" );
  }

  void createStyleControls( ) {
    createStyleButton( "BORDER" );
    createStyleButton( "PASSWORD" );
    createStyleButton( "WRAP" );
    // TODO [rh] bring READ_ONLY to work, than we can demonstrate it here
//    createStyleButton( "READ_ONLY" );
    createFontChooser( new Control[] { text1 } );
  }

  void createExampleControls( Composite top ) {
    top.setLayout( new RowLayout() );
    int style = getStyle();
    text1 = new Text( top, style );
    text1.setLayoutData( new RowData(150, 150) );
    text1.setText( "Lorem ipsum dolor sit amet, consectetur adipisicing "
                   + "elit, sed do eiusmod tempor incididunt ut labore et "
                   + "dolore magna aliqua." );
  }

}
