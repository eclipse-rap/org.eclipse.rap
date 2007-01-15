/**
 * 
 */
package org.eclipse.rap.demo.controls;

import java.lang.reflect.Field;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.custom.SashForm;
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.graphics.Color;
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.widgets.*;

abstract class ExampleTab {
  
  protected final TabFolder folder;
  private final Composite exmplComp;
  private final Composite styleComp;
  private final SelectionListener listener;

  public ExampleTab( final TabFolder folder, final String title ) {
    this.folder = folder;
    TabItem item = new TabItem( folder, RWT.NONE );
    item.setText( title );
    SashForm sashForm = new SashForm( folder, RWT.HORIZONTAL );
    item.setControl( sashForm );

    exmplComp = new Composite( sashForm, RWT.NONE );
    styleComp = new Composite( sashForm, RWT.NONE );
    styleComp.setLayout( new RowLayout( RWT.VERTICAL ) );
    sashForm.setWeights( new int[] { 70, 30} );
    listener = new SelectionListener() {
      public void widgetSelected( SelectionEvent event ) {
        destroyExampleControls( );
        createExampleControls( exmplComp );
        exmplComp.layout();
      }
    };
    createStyleControls( );
    createExampleControls( exmplComp );
  }
  
  abstract void createStyleControls( );
  
  abstract void createExampleControls( final Composite top );

  protected Button createStyleButton( final String fieldName ) {
    int style = RWT.NONE;
    try {
      Field field = RWT.class.getField( fieldName );
      style = field.getInt( null );
    } catch( NoSuchFieldException e ) {
      System.err.println("No such style flag: RWT." + fieldName);
    } catch( IllegalAccessException e ) {
      System.err.println("Cannot access style flag: RWT." + fieldName);
    }
    Button button = createStyleButton( "RWT." + fieldName, style );
//    TODO [rst] when enabled works, change to:
//    button.setEnabled( style != RWT.NONE );
    button.setVisible( style != RWT.NONE );
    return button;
  }

  private Button createStyleButton( final String name, final int style ) {
    Button button = new Button( styleComp, RWT.CHECK );
    button.setText( name );
    button.addSelectionListener( listener );
    button.setData( "style", new Integer( style ) );
    // preferred size does not work:
    button.setLayoutData( new RowData( 100, 20 ) );
    return button;
  }
  
  protected void destroyExampleControls() {
    Control[] ctrls = exmplComp.getChildren();
    for( int i = 0; i < ctrls.length; i++ ) {
      try {
        ctrls[ i ].dispose();
      } catch( Exception e ) {
        System.out.println( e );
      }
    }
  }

  protected int getStyle() {
    int result = RWT.NONE;
    Control[] ctrls = styleComp.getChildren();
    for( int i = 0; i < ctrls.length; i++ ) {
      if( ctrls[ i ] instanceof Button ) {
        Button button = ( Button )ctrls[ i ];
        if (button.getSelection()) {
          Object data = button.getData("style");
          if( data != null && data instanceof Integer ) {
            int style = (( Integer )data).intValue();
            result |= style;
          }
        }
      }
    }
    return result;
  }

  /**
   * TODO [rst] Integrate facility for changing colors into styleComp
   */
  private void createTabColor( TabFolder folder ) {
    TabItem item = new TabItem( folder, RWT.NONE );
    item.setText( "Color" );
    Composite comp = new Composite( folder, RWT.NONE );
    item.setControl( comp );
    comp.setLayout( new RowLayout() );
    // colors
    final int count = 3;
    final Color[] bgColors = new Color[count];
    final Color[] fgColors = new Color[count];
    bgColors[0] = Color.getColor(139, 37, 0);
    bgColors[1] = Color.getColor(105, 89, 205);
    bgColors[2] = Color.getColor(139, 121, 94);
    fgColors[0] = Color.getColor(255, 140, 0);
    fgColors[1] = Color.getColor(255, 215, 0);
    fgColors[2] = Color.getColor(154, 205, 50);
    // label
    final Label label = new Label( comp, RWT.WRAP );
    label.setLayoutData( new RowData( 100, 100 ) );
    label.setText( "Label" ); // TODO: Label without text fails!
    label.setBackground( bgColors[0] );
    label.setForeground( bgColors[1] );
    // button
    final Button button = new Button( comp, RWT.PUSH );
    button.setLayoutData( new RowData( 100, 100 ) );
    button.setText( "Button" );
    button.setBackground( bgColors[0] );
    button.setForeground( Color.getColor( 0, 128, 0 ) );
    // switch foreground button
    Button switchFgButton = new Button( comp, RWT.PUSH );
    switchFgButton.setText( "Switch Foreground" );
    switchFgButton.setLayoutData( new RowData( 100, 30 ) );
    final int fgIndex[] = { 0 };
    switchFgButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        int i = ++fgIndex[0] % count;
        label.setForeground( fgColors[i] );
        button.setForeground( fgColors[i] );
      }
    } );
    // switch background button
    Button switchBgButton = new Button( comp, RWT.PUSH );
    switchBgButton.setText( "Switch Background" );
    switchBgButton.setLayoutData( new RowData( 100, 30 ) );
    final int bgIndex[] = { 0 };
    switchBgButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        int i = ++bgIndex[0] % count;
        label.setBackground( bgColors[i] );
        button.setBackground( bgColors[i] );
      }
    } );    
  }

}
