/**
 * 
 */
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.graphics.Color;
import org.eclipse.rap.rwt.layout.FillLayout;
import org.eclipse.rap.rwt.widgets.Composite;
import org.eclipse.rap.rwt.widgets.TabFolder;

public class CompositeTab extends ExampleTab {

  public CompositeTab( TabFolder parent ) {
    super( parent, "Composite" );
  }

  void createStyleControls( ) {
    createStyleButton( "BORDER" );
    createVisibilityButton();
  }

  void createExampleControls( Composite top ) {
    top.setLayout( new FillLayout() );
    int style = getStyle();
    final Composite comp = new Composite( top, style );
    Color bgColor = Color.getColor( 240, 250, 190 );
    comp.setBackground( bgColor );
    registerControl( comp );
//    comp.addControlListener( new ControlAdapter() {
//      public void controlResized( ControlEvent event ) {
//        System.out.println( "Composite Bounds:      " + comp.getBounds() );
//        System.out.println( "Composite Client Area: " + comp.getClientArea() );
//      }} );
  }

}
