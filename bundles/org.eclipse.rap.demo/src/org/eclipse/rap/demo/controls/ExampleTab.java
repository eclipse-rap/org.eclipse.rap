/**
 * 
 */
package org.eclipse.rap.demo.controls;

import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.custom.SashForm;
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.graphics.Color;
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.widgets.*;

abstract class ExampleTab {
  
  protected final TabFolder folder;
  private final SelectionListener styleListener;
  private final List controls;

  private Composite exmplComp;
  protected Composite styleComp;
  private Color[] bgColors;
  private Color[] fgColors;
  private int fgIndex;
  private int bgIndex;

  private boolean visible = true;
  private boolean enabled = true;
  private Text text;
  private StringBuffer content = new StringBuffer();
  
  public ExampleTab( final TabFolder folder, final String title ) {
    this.folder = folder;
    TabItem item = new TabItem( folder, RWT.NONE );
    item.setText( title );
    Control sashForm = createForm();
    item.setControl( sashForm );
    initColors();
    controls = new ArrayList();
    styleListener = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        controls.clear();
        destroyExampleControls( );
        createExampleControls( exmplComp );
        setVisible( visible );
        setEnabled( enabled );
        exmplComp.layout();
      }
    };
    createExampleControls( exmplComp );
    createStyleControls();
  }
  
  private Control createForm() {
    SashForm vertSashForm = new SashForm( folder, RWT.VERTICAL );
    SashForm horSashForm = new SashForm( vertSashForm, RWT.HORIZONTAL );
    Composite leftComp = new Composite( horSashForm, RWT.NONE );
    Composite rightComp = new Composite( horSashForm, RWT.NONE );
    Composite footComp = new Composite( vertSashForm, RWT.NONE );
    FillLayout layout = new FillLayout();
    layout.marginWidth = 5;
    layout.marginHeight = 5;
    leftComp.setLayout( layout );
    rightComp.setLayout( layout );
    footComp.setLayout( layout );
    exmplComp = new Composite( leftComp, RWT.NONE );
    styleComp = new Composite( rightComp, RWT.NONE );
    styleComp.setLayout( new RowLayout( RWT.VERTICAL ) );
    horSashForm.setWeights( new int[] { 60, 40 } );
    vertSashForm.setWeights( new int[] { 90, 10 } );
    text = new Text( footComp, RWT.BORDER | RWT.READ_ONLY | RWT.MULTI );
    text.setText( "" );
    return vertSashForm;
  }
  
  private void initColors() {
    bgColors= new Color[3];
    fgColors = new Color[3];
    bgColors[0] = Color.getColor(139, 37, 0);
    bgColors[1] = Color.getColor(105, 89, 205);
    bgColors[2] = Color.getColor(139, 121, 94);
    fgColors[0] = Color.getColor(255, 140, 0);
    fgColors[1] = Color.getColor(255, 215, 0);
    fgColors[2] = Color.getColor(154, 205, 50);
  }

  abstract void createStyleControls( );
  
  abstract void createExampleControls( final Composite top );

  protected Button createStyleButton( final String fieldName ) {
    int style = RWT.NONE;
    try {
      Field field = RWT.class.getField( fieldName );
      style = field.getInt( null );
    } catch( NoSuchFieldException e ) {
    } catch( IllegalAccessException e ) {
      System.err.println( "Cannot access style flag: RWT." + fieldName );
    }
    Button button = createStyleButton( "RWT." + fieldName, style );
    button.setEnabled( style != RWT.NONE );
    return button;
  }

  protected void createFontChooser() {
    new FontChooser( styleComp, controls );
  }

  private Button createStyleButton( final String name, final int style ) {
    Button button = new Button( styleComp, RWT.CHECK );
    button.setText( name );
    button.addSelectionListener( styleListener );
    button.setData( "style", new Integer( style ) );
    // preferred size does not work:
    button.setLayoutData( new RowData( 100, 20 ) );
    return button;
  }

  protected Button createPropertyButton( final String name ) {
    Button button = new Button( styleComp, RWT.CHECK );
    button.setText( name );
    // preferred size does not work:
    button.setLayoutData( new RowData( 100, 20 ) );
    return button;
  }
  
  /**
   * Creates a checkbutton to show / hide the registered controls.
   * 
   * @return the created checkbutton
   */
  protected Button createVisibilityButton( ) {
    final Button button = new Button( styleComp, RWT.CHECK );
    button.setText( "Visble" );
    button.setSelection( visible ); 
    button.setLayoutData( new RowData( 100, 20 ) );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        visible = button.getSelection();
        setVisible( visible );
      }
    } );
    return button;
  }

  /**
   * Creates a checkbutton to enable / disabled the registered controls.
   * 
   * @return the created checkbutton.
   */
  protected Button createEnablementButton( ) {
    final Button button = new Button( styleComp, RWT.CHECK );
    button.setText( "Enabled" );
    button.setSelection( enabled );
    button.setLayoutData( new RowData( 100, 20 ) );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        enabled = button.getSelection();
        setEnabled( enabled );
      }
    } );
    return button;
  }

  /**
   * Creates a checkbutton to change the foreground color of the registered
   * controls.
   * 
   * @return the created checkbutton.
   */
  protected Button createFgColorButton( ) {
    final Button button = new Button( styleComp, RWT.PUSH );
    button.setText( "Fg Color" );
    button.setLayoutData( new RowData( 100, 20 ) );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        changeFgColor();
      }
    } );
    return button;
  }

  /**
   * Creates a checkbutton to change the background color of the registered
   * controls.
   * 
   * @return the created checkbutton.
   */
  protected Button createBgColorButton( ) {
    final Button button = new Button( styleComp, RWT.PUSH );
    button.setText( "Bg Color" );
    button.setLayoutData( new RowData( 100, 20 ) );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        changeBgColor();
      }
    } );
    return button;
  }

  /**
   * Adds a control to the list of registered controls. Registered controls can
   * be hidden and disabled by the checkbuttons in the property area. This
   * method is to be called within <code>createExampleControls</code>.
   * 
   * @param control A control that should be remote controlled.
   */
  protected void registerControl( Control control ) {
    controls.add( control );
  }
  
  protected void log(String msg) {
    content.append( msg.trim() );
    content.append( "\n" );
    System.out.println( "LOG: " + msg.trim() );
    text.setText( content.toString() );
  }

  private void destroyExampleControls() {
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

  private void setVisible( boolean visible ) {
    Iterator iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = ( Control )iter.next();
      control.setVisible( visible );
    }
  }
  
  private void setEnabled( boolean enabled ) {
    Iterator iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = ( Control )iter.next();
      control.setEnabled( enabled );
    }
  }
  
  private void changeFgColor() {
    Color color = null;
    if( fgIndex >= 0 && fgIndex < fgColors.length ) {
      color = fgColors[fgIndex++];
    } else {
      fgIndex = 0;
    }
    Iterator iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = ( Control )iter.next();
      control.setForeground( color );
    }
  }

  private void changeBgColor() {
    Color color = null;
    if( bgIndex >= 0 && bgIndex < bgColors.length ) {
      color = bgColors[bgIndex++];
    } else {
      bgIndex = 0;
    }
    Iterator iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = ( Control )iter.next();
      control.setBackground( color );
    }
  }

}
