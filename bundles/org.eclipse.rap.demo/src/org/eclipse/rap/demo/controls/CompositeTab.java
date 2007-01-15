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
  }

  void createExampleControls( Composite top ) {
    FillLayout layout = new FillLayout();
    layout.marginWidth = 20;
    layout.marginHeight = 20;
    top.setLayout( layout );
    int style = getStyle();
    final Composite comp = new Composite( top, style );
    comp.setBackground( Color.getColor( 240, 250, 190 ) );
//    comp.addControlListener( new ControlListener() {
//      public void controlMoved( ControlEvent event ) {
//      }
//      public void controlResized( ControlEvent event ) {
//        System.out.println( "Composite Bounds:      " + comp.getBounds() );
//        System.out.println( "Composite Client Area: " + comp.getClientArea() );
//      }} );
  }

}
