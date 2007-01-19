/**
 * 
 */
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionAdapter;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Color;
import org.eclipse.rap.rwt.layout.FillLayout;
import org.eclipse.rap.rwt.widgets.*;

public class ContainmentTab extends ExampleTab {

  private static Color color1;
  private static Color color2;
  private static Color color3;
  private Composite comp2;
  private Composite comp3;
  private Composite comp1;

  static {
    color1 = Color.getColor( 154, 205, 50 );
    color2 = Color.getColor( 105, 89, 205 );
    color3 = Color.getColor( 240, 250, 190 );
  }

  public ContainmentTab( TabFolder parent ) {
    super( parent, "Containment" );
  }

  void createStyleControls( ) {
    Button visibleButton = createVisibilityButton();
    Button enabledButton = createEnablementButton();
    // change button labels
    visibleButton.setText( "Blue Visible" );
    enabledButton.setText( "Blue Enabled" );
    // add listener for sysout
    visibleButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        changeVisible();
      }
    } );
    enabledButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        changeEnabled();
      }
    } );
  }

  void createExampleControls( Composite top ) {
    top.setLayout( new FillLayout() );
    int style = getStyle();
    FillLayout layout = new FillLayout();
    layout.marginWidth = 20;
    layout.marginHeight = 20;
    comp1 = new Composite( top, style );
    comp1.setBackground( color1 );
    comp1.setLayout( layout );
    comp2 = new Composite( comp1, style );
    comp2.setBackground( color2 );
    comp2.setLayout( layout );
    comp3 = new Composite( comp2, style );
    comp3.setBackground( color3 );
    comp3.setLayout( layout );
    Button button = new Button( comp3, RWT.PUSH );
    button.setText( "Button" );
    registerControl( comp2 );
  }

  private void changeVisible() {
    System.out.println();
    System.out.println( "Comp1" );
    System.out.println( "  getParent: " + comp1.getParent() );
    System.out.println( "  isVisible: " + comp1.isVisible() );
    System.out.println( "  getVisible: " + comp1.getVisible() );
    System.out.println( "Comp2" );
    System.out.println( "  getParent: " + comp2.getParent() );
    System.out.println( "  isVisible: " + comp2.isVisible() );
    System.out.println( "  getVisible: " + comp2.getVisible() );
    System.out.println( "Comp3" );
    System.out.println( "  getParent: " + comp3.getParent() );
    System.out.println( "  isVisible: " + comp3.isVisible() );
    System.out.println( "  getVisible: " + comp3.getVisible() );
  }

  private void changeEnabled() {
    System.out.println();
    System.out.println( "Comp1" );
    System.out.println( "  getParent: " + comp1.getParent() );
    System.out.println( "  isEnabled: " + comp1.isEnabled() );
    System.out.println( "  getEnabled: " + comp1.getEnabled() );
    System.out.println( "Comp2" );
    System.out.println( "  getParent: " + comp2.getParent() );
    System.out.println( "  isEnabled: " + comp2.isEnabled() );
    System.out.println( "  getEnabled: " + comp2.getEnabled() );
    System.out.println( "Comp3" );
    System.out.println( "  getParent: " + comp3.getParent() );
    System.out.println( "  isEnabled: " + comp3.isEnabled() );
    System.out.println( "  getEnabled: " + comp3.getEnabled() );
  }

}
