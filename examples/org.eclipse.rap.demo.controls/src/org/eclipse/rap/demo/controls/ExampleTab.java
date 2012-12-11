/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.theme.ThemeUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.widgets.IWidgetGraphicsAdapter;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


@SuppressWarnings("restriction")
abstract class ExampleTab implements Serializable {

  protected final List<Control> controls;

  private Composite exampleComp;
  protected Composite styleComp;
  protected Color[] bgColors;
  protected Color[] fgColors;
  private Font font;
  private int fgIndex;
  private int bgIndex;
  private int rbIndex;
  private boolean showBgImage = false;
  private boolean showBgGradient = false;

  private boolean visible = true;
  private boolean enabled = true;
  private Text text;
  private final StringBuffer content = new StringBuffer();
  private FontDialog fontChooser;
  private ColorChooser fgColorChooser;
  private ColorChooser bgColorChooser;
  private int defaultStyle = SWT.NONE;
  private final Set<String> properties = new HashSet<String>();

  public static final Color BG_COLOR_GREEN = Graphics.getColor( 154, 205, 50 );
  public static final Color BG_COLOR_BLUE = Graphics.getColor( 105, 89, 205 );
  public static final Color BG_COLOR_BROWN = Graphics.getColor( 192, 172, 137 );
  public static final Color FG_COLOR_RED = Graphics.getColor( 194, 0, 23 );
  public static final Color FG_COLOR_BLUE = Graphics.getColor( 28, 96, 141 );
  public static final Color FG_COLOR_ORANGE = Graphics.getColor( 249, 158, 0 );

  public static final Color BGG_COLOR_GREEN = Graphics.getColor( 0, 255, 0 );
  public static final Color BGG_COLOR_BLUE = Graphics.getColor( 0, 0, 255 );

  private static final String[] SWT_CURSORS = {
    "null",
    "CURSOR_ARROW",
    "CURSOR_WAIT",
    "CURSOR_APPSTARTING",
    "CURSOR_CROSS",
    "CURSOR_HELP",
    "CURSOR_NO",
    "CURSOR_SIZEALL",
    "CURSOR_SIZENESW",
    "CURSOR_SIZENS",
    "CURSOR_SIZENWSE",
    "CURSOR_SIZEWE",
    "CURSOR_SIZEN",
    "CURSOR_SIZES",
    "CURSOR_SIZEE",
    "CURSOR_SIZEW",
    "CURSOR_SIZENE",
    "CURSOR_SIZESE",
    "CURSOR_SIZESW",
    "CURSOR_SIZENW",
    "CURSOR_IBEAM",
    "CURSOR_HAND",
    "CURSOR_UPARROW"
  };
  private final String name;
  private Shell shell;

  public ExampleTab( String name ) {
    this.name = name;
    controls = new ArrayList<Control>();
  }

  public String getName() {
    return name;
  }

  public void createContents( Composite parent ) {
    shell = parent.getShell();
    createSashForm( parent );
    initColors();
    createExampleControls( exampleComp );
    createStyleControls( styleComp );
    exampleComp.layout();
    styleComp.layout();
  }

  protected void createNew() {
    controls.clear();
    destroyExampleControls();
    createExampleControls( exampleComp );
    updateVisible();
    updateEnabled();
    if( fgColorChooser != null ) {
      updateFgColor();
    }
    if( bgColorChooser != null ) {
      updateBgColor();
    }
    updateBgImage();
    updateBgGradient();
    if( fontChooser != null ) {
      // Control control = ( Control )controls.get( 0 );
      // font = control.getFont();
      // if( font != null ) {
      //   fontChooser.setFont( font );
      // }
      updateFont();
    }
    exampleComp.layout();
  }

  private Control createSashForm( Composite parent ) {
    SashForm vertSashForm = new SashForm( parent, SWT.VERTICAL );
    SashForm horSashForm = new SashForm( vertSashForm, SWT.HORIZONTAL );
    createLeft( horSashForm );
    createRight( horSashForm );
    createFoot( vertSashForm );
    horSashForm.setWeights( new int[] { 50, 50 } );
    vertSashForm.setWeights( new int[] { 93, 7 } );
    return vertSashForm;
  }

  private void createLeft( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setBackground( parent.getDisplay().getSystemColor( SWT.COLOR_WHITE ) );
    composite.setLayout( new FormLayout() );
    Label header = new Label( composite, SWT.CENTER );
    header.setFont( getHeaderFont( parent ) );
    header.setText( getName() );
    header.setLayoutData( createLayoutDataForHeader() );
    exampleComp = new Composite( composite, SWT.NONE );
    exampleComp.setLayoutData( createLayoutDataForExampleArea( header ) );
  }

  private void createRight( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setBackground( parent.getDisplay().getSystemColor( SWT.COLOR_WHITE ) );
    composite.setLayout( new FormLayout() );
    Label header = new Label( composite, SWT.LEFT );
    header.setText( "Styles and Parameters" );
    header.setLayoutData( createLayoutDataForHeader() );
    styleComp = new Composite( composite, SWT.NONE );
    styleComp.setLayoutData( createLayoutDataForExampleArea( header ) );
    styleComp.setLayout( new RowLayout( SWT.VERTICAL ) );
  }

  private void createFoot( Composite parent ) {
    text = new Text( parent, SWT.READ_ONLY | SWT.MULTI );
    text.setText( "---" );
  }

  private static Font getHeaderFont( Composite parent ) {
    String fontName = parent.getFont().getFontData()[ 0 ].getName();
    return new Font( parent.getDisplay(), fontName, 18, SWT.BOLD );
  }

  private static Object createLayoutDataForHeader() {
    FormData formData = new FormData();
    formData.top = new FormAttachment( 0, 5 );
    formData.left = new FormAttachment( 0, 10 );
    formData.right = new FormAttachment( 100, -10 );
    return formData;
  }

  private static Object createLayoutDataForExampleArea( Control control ) {
    FormData formData = new FormData();
    formData.top = new FormAttachment( 0, 35 );
    formData.left = new FormAttachment( 0, 5 );
    formData.right = new FormAttachment( 100, -5 );
    formData.bottom = new FormAttachment( 100, -5 );
    return formData;
  }

  private void initColors() {
    bgColors = new Color[] {
      null,
      BG_COLOR_GREEN,
      BG_COLOR_BLUE,
      BG_COLOR_BROWN
    };
    fgColors = new Color[] {
      null,
      FG_COLOR_RED,
      FG_COLOR_BLUE,
      FG_COLOR_ORANGE
    };
  }

  protected abstract void createStyleControls( Composite parent);

  protected abstract void createExampleControls( Composite parent );

  // TODO [rst] Refactor ExampleTab to evaluate style controls before example controls are created
  protected void setDefaultStyle( int style ) {
    defaultStyle = style;
  }

  protected Button createStyleButton( String fieldName, int style ) {
    return createStyleButton( fieldName, style, false );
  }

  protected Button createStyleButton( String name, int style, boolean checked ) {
    Button button = new Button( styleComp, SWT.CHECK );
    button.setText( name );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        createNew();
      }
    } );
    button.setData( "style", new Integer( style ) );
    button.setSelection( checked );
    return button;
  }

  protected Button createPropertyButton( String text ) {
    return createPropertyButton( text, SWT.CHECK );
  }

  protected Button createPropertyButton( String text, int style ) {
    Button button = new Button( styleComp, style );
    button.setText( text );
    return button;
  }

  protected Button createPropertyCheckbox( String text, String prop ) {
    return createPropertyCheckbox( text, prop, false );
  }

  protected Button createPropertyCheckbox( String text, final String prop, boolean checked ) {
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( text );
    button.setSelection( checked );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        if( button.getSelection() ) {
          properties.add( prop );
        } else {
          properties.remove( prop );
        }
        createNew();
      }
    } );
    return button;
  }

  public final boolean hasCreateProperty( String name ) {
    return properties.contains( name );
  }

  /**
   * Creates a checkbutton to show / hide the registered controls.
   *
   * @return the created checkbutton
   */
  protected Button createVisibilityButton( ) {
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "Visible" );
    button.setSelection( visible );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
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
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "Enabled" );
    button.setSelection( enabled );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        enabled = button.getSelection();
        updateEnabled();
      }
    } );
    return button;
  }

  /**
   * Creates a button to change the foreground color of all registered
   * controls.
   *
   * @return the created button
   */
  protected Button createFgColorButton() {
    fgColorChooser = new ColorChooser();
    final Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Foreground" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        fgIndex = ( fgIndex + 1 ) % fgColors.length;
        updateFgColor();
      }

    } );
    return button;
  }

  /**
   * Creates a button to change the background color of all registered
   * controls.
   *
   * @return the created button
   */
  protected Button createBgColorButton() {
    bgColorChooser = new ColorChooser();
    final Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Background" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        bgIndex = ( bgIndex + 1 ) % fgColors.length;
        updateBgColor();
      }
    } );
    return button;
  }

  /**
   * Creates a button to change the background gradient of all registered
   * controls.
   *
   * @return the created button
   */
  protected Button createBgGradientButton() {
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "Background Gradient" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        showBgGradient = button.getSelection();
        updateBgGradient();
      }
    } );
    return button;
  }

  /**
   * Creates a checkbox that controls whether a background image is set on the
   * registered controls.
   *
   * @return the created checkbox
   */
  protected Button createBgImageButton() {
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "Background Image" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        showBgImage = button.getSelection();
        updateBgImage();
      }
    } );
    return button;
  }

  protected Button createFontChooser() {
    final Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Font" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        fontChooser = new FontDialog( getShell(), SWT.NONE );
        Control control = controls.get( 0 );
        fontChooser.setFontList( control.getFont().getFontData() );
        if( fontChooser.open() != null ) {
          font = new Font( control.getDisplay(), fontChooser.getFontList() );
        }
        updateFont();
      }
    } );
    return button;
  }

  /**
   * Experimental. Switching themes at runtime does not yet work properly.
   */
  protected void createThemeSwitcher( final Composite parent ) {
    final Button button = new Button( parent, SWT.PUSH );
    button.setText( "Theme Switcher" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        Shell shell = new Shell( parent.getShell(), SWT.DIALOG_TRIM );
        shell.setText( "Theme Switcher" );
        shell.setLayout( new GridLayout() );
        Button themeButton = new Button( shell, SWT.PUSH );
        themeButton.setText( "Switch Theme" );
        themeButton.addSelectionListener( new SelectionAdapter() {
          String[] availableThemeIds = ThemeUtil.getAvailableThemeIds();
          @Override
          public void widgetSelected( SelectionEvent e ) {
            int index = 0;
            String currThemeId = ThemeUtil.getCurrentThemeId();
            for( int i = 0; i < availableThemeIds.length; i++ ) {
              if( currThemeId.equals( availableThemeIds[ i ] ) ) {
                index = ( i + 1 ) % availableThemeIds.length;
              }
            }
            String newThemeId = availableThemeIds[ index ];
            ThemeUtil.setCurrentThemeId( ContextProvider.getUISession(), newThemeId );
          }
        } );
        shell.pack();
        shell.open();
      }
    } );
  }

  /**
   * Creates a combo that controls whether a cursor is set on the
   * registered controls.
   *
   * @return the created combo
   */
  protected Combo createCursorCombo() {
    Composite group = new Composite( styleComp, SWT.NONE );
    group.setLayout( new GridLayout( 2, false ) );
    new Label( group, SWT.NONE ).setText( "Cursor" );
    final Combo combo = new Combo( group, SWT.READ_ONLY );
    combo.setItems( SWT_CURSORS );
    combo.select( 0 );
    combo.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        String selection = null;
        int index = combo.getSelectionIndex();
        if( index > 0 ) {
          selection = combo.getItem( index );
        }
        updateCursor( selection );
      }
    } );
    return combo;
  }

  /**
   * Creates a text that controls whether a border radius is set on the
   * registered controls.
   */
  protected void cteateRoundedBorderGroup() {
    Group group = new Group( styleComp, SWT.NONE );
    group.setText( "Rounded Border" );
    group.setLayout( new GridLayout( 2, false ) );
    new Label( group, SWT.NONE ).setText( "Width" );
    final Text textWidth = new Text( group, SWT.SINGLE | SWT.BORDER );
    textWidth.setLayoutData( new GridData( 20, SWT.DEFAULT ) );
    new Label( group, SWT.NONE ).setText( "Color" );
    final Button buttonColor = new Button( group, SWT.PUSH );
    buttonColor.setLayoutData( new GridData( 20, 20 ) );
    buttonColor.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        rbIndex = ( rbIndex + 1 ) % bgColors.length;
        if( bgColors[ rbIndex ] == null ) {
          buttonColor.setText( "" );
        } else {
          buttonColor.setText( "\u2588" );
        }
        buttonColor.setForeground( bgColors[ rbIndex ] );
      }
    } );
    new Label( group, SWT.NONE ).setText( "Radius " );
    Composite radiusGroup = new Composite( group, SWT.NONE );
    radiusGroup.setLayout( new GridLayout( 4, false ) );
    new Label( radiusGroup, SWT.NONE ).setText( "T-L" );
    final Text textTopLeft = new Text( radiusGroup, SWT.SINGLE | SWT.BORDER );
    textTopLeft.setLayoutData( new GridData( 20, SWT.DEFAULT ) );
    new Label( radiusGroup, SWT.NONE ).setText( "T-R" );
    final Text textTopRight = new Text( radiusGroup, SWT.SINGLE | SWT.BORDER );
    textTopRight.setLayoutData( new GridData( 20, SWT.DEFAULT ) );
    new Label( radiusGroup, SWT.NONE ).setText( "B-L" );
    final Text textBottomLeft = new Text( radiusGroup, SWT.SINGLE | SWT.BORDER );
    textBottomLeft.setLayoutData( new GridData( 20, SWT.DEFAULT ) );
    new Label( radiusGroup, SWT.NONE ).setText( "B-R" );
    final Text textBottomRight = new Text( radiusGroup, SWT.SINGLE | SWT.BORDER );
    textBottomRight.setLayoutData( new GridData( 20, SWT.DEFAULT ) );
    Button button = new Button( group, SWT.PUSH );
    button.setText( "Set" );
    button.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent e ) {
        int width = parseInt( textWidth.getText() );
        Color color = buttonColor.getForeground();
        int topLeft = parseInt( textTopLeft.getText() );
        int topRight = parseInt( textTopRight.getText() );
        int bottomRight = parseInt( textBottomRight.getText() );
        int bottomLeft = parseInt( textBottomLeft.getText() );
        updateRoundedBorder( width,
                             color,
                             topLeft,
                             topRight,
                             bottomRight,
                             bottomLeft );
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
    control.addDisposeListener( new DisposeListener() {
      public void widgetDisposed( DisposeEvent event ) {
        controls.remove( control );
      }
    } );
  }

  protected void log( String msg ) {
    content.insert( 0, msg.trim() + text.getLineDelimiter() );
    text.setText( content.toString() );
  }

  protected Image loadImage( String name ) {
    InputStream stream = getClass().getClassLoader().getResourceAsStream( name );
    Image result = new Image( shell.getDisplay(), stream );
    try {
      stream.close();
    } catch( IOException ioe ) {
      throw new RuntimeException( ioe );
    }
    return result;
  }

  private void destroyExampleControls() {
    Control[] controls = exampleComp.getChildren();
    for( int i = 0; i < controls.length; i++ ) {
      controls[ i ].dispose();
    }
  }

  protected int getStyle() {
    int result = SWT.NONE;
    Control[] ctrls = styleComp.getChildren();
    if( ctrls.length == 0 ) {
      result = defaultStyle;
    } else {
      for( int i = 0; i < ctrls.length; i++ ) {
        if( ctrls[ i ] instanceof Button ) {
          Button button = ( Button )ctrls[ i ];
          if( button.getSelection() ) {
            Object data = button.getData( "style" );
            if( data instanceof Integer ) {
              int style = ( ( Integer )data ).intValue();
              result |= style;
            }
          }
        }
      }
    }
    return result;
  }

  private void updateVisible( ) {
    Iterator<Control> iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = iter.next();
      control.setVisible( visible );
    }
  }

  private void updateEnabled( ) {
    Iterator<Control> iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = iter.next();
      control.setEnabled( enabled );
    }
  }

  private void updateFgColor() {
    Iterator<Control> iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = iter.next();
      control.setForeground( fgColors[ fgIndex ] );
    }
  }

  private void updateBgColor() {
    Iterator<Control> iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = iter.next();
      control.setBackground( bgColors[ bgIndex ] );
    }
  }

  private void updateBgGradient() {
    Iterator<Control> iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = iter.next();
      Object adapter = control.getAdapter( IWidgetGraphicsAdapter.class );
      IWidgetGraphicsAdapter gfxAdapter = ( IWidgetGraphicsAdapter )adapter;
      if( showBgGradient ) {
        Color[] gradientColors = new Color[] {
          BGG_COLOR_BLUE,
          BGG_COLOR_GREEN,
          BGG_COLOR_BLUE,
          BGG_COLOR_GREEN,
          BGG_COLOR_BLUE
        };
        int[] percents = new int[] { 0, 25, 50, 75, 100 };
        gfxAdapter.setBackgroundGradient( gradientColors, percents, true );
      } else {
        gfxAdapter.setBackgroundGradient( null, null, true );
      }
    }
  }

  private void updateBgImage() {
    for( Control control : controls ) {
      Image image = Util.loadImage( control.getDisplay(), "resources/pattern.png" );
      control.setBackgroundImage( showBgImage ? image : null );
    }
  }

  private void updateFont() {
    Iterator<Control> iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = iter.next();
      control.setFont( font );
    }
    // Force layout
    if( controls.size() > 0 ) {
      Composite parent = controls.get( 0 ).getParent();
      parent.layout( true, true );
    }
  }

  private void updateCursor( String selection ) {
    Cursor cursor = null;
    Class<SWT> swtClass = SWT.class;
    if( selection != null ) {
      try {
        Field field = swtClass.getField( selection );
        int cursorStyle = field.getInt( swtClass );
        cursor = Display.getCurrent().getSystemCursor( cursorStyle );
      } catch( Exception e ) {
        e.printStackTrace();
      }
    }
    Iterator<Control> iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = iter.next();
      control.setCursor( cursor );
    }
  }

  private void updateRoundedBorder( int width,
                                    Color color,
                                    int topLeft,
                                    int topRight,
                                    int bottomRight,
                                    int bottomLeft )
  {
    Iterator<Control> iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = iter.next();
      Object adapter = control.getAdapter( IWidgetGraphicsAdapter.class );
      IWidgetGraphicsAdapter gfxAdapter = ( IWidgetGraphicsAdapter )adapter;
      gfxAdapter.setRoundedBorder( width,
                                   color,
                                   topLeft,
                                   topRight,
                                   bottomRight,
                                   bottomLeft );
    }
  }

  private int parseInt( String text ) {
    int result;
    try {
      result = Integer.parseInt( text );
    } catch( NumberFormatException e ) {
      result = -1;
    }
    return result;
  }

  protected Shell getShell() {
    return shell;
  }

  protected boolean checkControl( Control control ) {
    return control != null && !control.isDisposed();
  }

}
