/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.custom.SashForm;
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.graphics.Color;
import org.eclipse.rap.rwt.graphics.Font;
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.widgets.*;

abstract class ExampleTab {
  
  private static final int MAX_COLORS = 4;
  private final TabFolder folder;
  private final List controls;

  private Composite exmplComp;
  protected Composite styleComp;
  private Color[] bgColors;
  private Color[] fgColors;
  private Font font;
  private int fgIndex;
  private int bgIndex;

  private boolean visible = true;
  private boolean enabled = true;
  private Text text;
  private StringBuffer content = new StringBuffer();
  private FontChooser fontChooser;
  private ColorChooser fgColorChooser;
  private ColorChooser bgColorChooser;
  private int defaultStyle = RWT.NONE;
  
  public ExampleTab( final TabFolder folder, final String title ) {
    this.folder = folder;
    TabItem item = new TabItem( folder, RWT.NONE );
    item.setText( title );
    Control sashForm = createForm();
    item.setControl( sashForm );
    initColors();
    controls = new ArrayList();
    createExampleControls( exmplComp );
    createStyleControls();
  }
  
  protected void createNew() {
    controls.clear();
    destroyExampleControls( );
    createExampleControls( exmplComp );
    updateVisible();
    updateEnabled();
    if( fgColorChooser != null ) {
      updateFgColor();
    }
    if( bgColorChooser != null ) {
      updateBgColor();
    }
    if( fontChooser != null ) {
      // Control control = ( Control )controls.get( 0 );
      // font = control.getFont();
      // if( font != null ) {
      //   fontChooser.setFont( font );
      // }
      updateFont();
    }
    exmplComp.layout();
  }

  private Control createForm() {
    SashForm vertSashForm = new SashForm( folder, RWT.VERTICAL );
    SashForm horSashForm = new SashForm( vertSashForm, RWT.HORIZONTAL );
    Composite leftComp = new Composite( horSashForm, RWT.NONE );
    Composite rightComp = new Composite( horSashForm, RWT.NONE );
    Composite footComp = new Composite( vertSashForm, RWT.NONE );
    createLeft( leftComp );
    createRight( rightComp );
    createFoot( footComp );
    horSashForm.setWeights( new int[] { 60, 40 } );
    vertSashForm.setWeights( new int[] { 95, 5 } );
    return vertSashForm;
  }
  
  private void createLeft( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    Group exmplGroup = new Group( parent, RWT.NONE );
    exmplGroup.setLayout( new FillLayout() );
    exmplComp = new Composite( exmplGroup, RWT.NONE );
  }
  
  private void createRight( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    Group styleGroup = new Group( parent, RWT.NONE );
    styleGroup.setText( "Styles and Parameters" );
    styleGroup.setLayout( new FillLayout() );
    styleComp = new Composite( styleGroup, RWT.NONE );
    styleComp.setLayout( new RowLayout( RWT.VERTICAL ) );
  }
  
  private void createFoot( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    text = new Text( parent, RWT.BORDER | RWT.READ_ONLY | RWT.MULTI );
    text.setText( "" );
  }
  
  private void initColors() {
    bgColors = new Color[ MAX_COLORS ];
    fgColors = new Color[ MAX_COLORS ];
    bgColors[ 0 ] = null;
    bgColors[ 1 ] = Color.getColor( 139, 37, 0 );
    bgColors[ 2 ] = Color.getColor( 105, 89, 205 );
    bgColors[ 3 ] = Color.getColor( 139, 121, 94 );
    fgColors[ 0 ] = null;
    fgColors[ 1 ] = Color.getColor( 255, 140, 0 );
    fgColors[ 2 ] = Color.getColor( 255, 215, 0 );
    fgColors[ 3 ] = Color.getColor( 154, 205, 50 );
  }

  protected abstract void createStyleControls( );
  
  protected abstract void createExampleControls( final Composite top );
  
  /**
   * TODO [rst] Refactor ExampleTab to evaluate style controls before example
   *      controls are created.
   */
  protected void setDefaultStyle( final int style ) {
    this.defaultStyle = style;
  }
  
  protected Button createStyleButton( final String fieldName ) {
    return createStyleButton( fieldName, false );
  }
  
  protected Button createStyleButton( final String fieldName,
                                      final boolean checked ) {
    int style = RWT.NONE;
    try {
      Field field = RWT.class.getField( fieldName );
      style = field.getInt( null );
    } catch( NoSuchFieldException e ) {
    } catch( IllegalAccessException e ) {
      System.err.println( "Cannot access style flag: RWT." + fieldName );
    }
    Button button = createStyleButton( "RWT." + fieldName, style, checked );
    button.setEnabled( style != RWT.NONE );
    return button;
  }
  
  private Button createStyleButton( final String name,
                                    final int style,
                                    final boolean checked ) {
    Button button = new Button( styleComp, RWT.CHECK );
    button.setText( name );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        createNew();
      }
    } );
    button.setData( "style", new Integer( style ) );
    // preferred size does not work:
    button.setLayoutData( new RowData( 100, 20 ) );
    button.setSelection( checked );
    return button;
  }

  protected Button createPropertyButton( final String text ) {
    return createPropertyButton( text, RWT.CHECK );
  }
  
  protected Button createPropertyButton( final String text, final int style ) {
    Button button = new Button( styleComp, style );
    button.setText( text );
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
      public void widgetSelected( final SelectionEvent event ) {
        visible = button.getSelection();
        updateVisible();
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
      public void widgetSelected( final SelectionEvent event ) {
        enabled = button.getSelection();
        updateEnabled();
      }
    } );
    return button;
  }

  /**
   * Creates a button to change the foreground color of the registered
   * controls.
   * 
   * @return the created checkbutton.
   */
  protected Button createFgColorButton( ) {
    fgColorChooser = new ColorChooser();
    final Button button = new Button( styleComp, RWT.PUSH );
    button.setText( "Fg Color" );
    button.setLayoutData( new RowData( 100, 20 ) );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        fgIndex = (fgIndex + 1) % MAX_COLORS;
        updateFgColor();
      }

    } );
    return button;
  }

  /**
   * Creates a button to change the background color of the registered
   * controls.
   * 
   * @return the created checkbutton.
   */
  protected Button createBgColorButton( ) {
    bgColorChooser = new ColorChooser();
    final Button button = new Button( styleComp, RWT.PUSH );
    button.setText( "Bg Color" );
    button.setLayoutData( new RowData( 100, 20 ) );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        bgIndex = (bgIndex + 1) % MAX_COLORS;
        updateBgColor();
      }
    } );
    return button;
  }
  
  protected void createFontChooser() {
    fontChooser = new FontChooser( styleComp );
    Control control = ( Control )controls.get( 0 );
    fontChooser.setFont( control.getFont() );
    fontChooser.setChangeRunnable( new Runnable() {
      public void run() {
        font = fontChooser.getFont();
        updateFont();
      }
    } );
  }

  /**
   * Adds a control to the list of registered controls. Registered controls can
   * be hidden and disabled by the checkbuttons in the property area. This
   * method is to be called within <code>createExampleControls</code>.
   * 
   * @param control A control that should be remote controlled.
   */
  protected void registerControl( final Control control ) {
    controls.add( control );
  }
  
  protected void log(final String msg) {
    content.append( msg.trim() );
    content.append( text.getLineDelimiter() );
    System.out.println( "LOG: " + msg.trim() );
    text.setText( content.toString() );
    // TODO: scroll to last line 
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
    if( ctrls.length == 0 ) {
      result = defaultStyle;
    } else {
      for( int i = 0; i < ctrls.length; i++ ) {
        if( ctrls[ i ] instanceof Button ) {
          Button button = ( Button )ctrls[ i ];
          if (button.getSelection()) {
            Object data = button.getData( "style" );
            if( data != null && data instanceof Integer ) {
              int style = (( Integer )data).intValue();
              result |= style;
            }
          }
        }
      }
    }
    return result;
  }

  private void updateVisible( ) {
    Iterator iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = ( Control )iter.next();
      control.setVisible( visible );
    }
  }
  
  private void updateEnabled( ) {
    Iterator iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = ( Control )iter.next();
      control.setEnabled( enabled );
    }
  }
  
  private void updateFgColor() {
    Iterator iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = ( Control )iter.next();
      control.setForeground( fgColors[fgIndex] );
    }
  }

  private void updateBgColor() {
    Iterator iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = ( Control )iter.next();
      control.setBackground( bgColors[bgIndex] );
    }
  }
  
  private void updateFont() {
    Iterator iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = ( Control )iter.next();
      control.setFont( font );
    }
  }
  
  protected Shell getShell() {
    return folder.getShell();
  }
}
