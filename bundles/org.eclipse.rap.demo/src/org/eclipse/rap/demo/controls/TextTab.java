/**
 * 
 */
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.layout.RowData;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class TextTab extends ExampleTab {

  private Text text;

  public TextTab( TabFolder folder ) {
    super( folder, "Text" );
  }

  void createStyleControls( ) {
    createStyleButton( "BORDER" );
    createStyleButton( "WRAP" );
    // TODO [rh] bring READ_ONLY to work, than we can demonstrate it here
    //       createStyleButton( "READ_ONLY" );
    createVisibilityButton();
    createEnablementButton();
    createFontChooser();
  }

  void createExampleControls( Composite top ) {
    top.setLayout( new RowLayout() );
    int style = getStyle();
    text = new Text( top, style );
    text.setLayoutData( new RowData(200, 200) );
    text.setText( "Lorem ipsum dolor sit amet, consectetur adipisicing "
                   + "elit, sed do eiusmod tempor incididunt ut labore et "
                   + "dolore magna aliqua." );
    registerControl( text );
  }

}
