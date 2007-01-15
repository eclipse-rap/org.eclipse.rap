/**
 * 
 */
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.custom.CBanner;
import org.eclipse.rap.rwt.layout.RowLayout;
import org.eclipse.rap.rwt.widgets.*;

public class CBannerTab extends ExampleTab {

  public CBannerTab( TabFolder parent ) {
    super( parent, "CBanner" );
  }

  void createStyleControls( ) {
    createStyleButton( "BORDER" );
  }

  void createExampleControls( Composite top ) {
    top.setLayout( new RowLayout(RWT.VERTICAL) );
    int style = getStyle();
    
    CBanner banner = new CBanner( top, style );
//    banner.setBackground( Color.getColor( 240, 250, 190 ) );
    
    Label rightLabel = new Label( banner, RWT.NONE );
    rightLabel.setText( "Right" );
//    rightLabel.setBackground( Color.getColor( 250, 250, 250 ) );
    banner.setRight( rightLabel );
    
    Label leftLabel = new Label( banner, RWT.NONE );
    leftLabel.setText( "Left" );
//    leftLabel.setBackground( Color.getColor( 250, 250, 250 ) );
    banner.setLeft( leftLabel );
    
//    Label bottomLabel = new Label( banner, RWT.NONE );
//    bottomLabel.setText( "Bottom" );
//    bottomLabel.setBackground( Color.getColor( 250, 250, 250 ) );
//    banner.setBottom( bottomLabel );
//    
//    Label contentLabel = new Label( banner, RWT.BORDER );
//    contentLabel.setToolTipText( "Content" );
  }

}
