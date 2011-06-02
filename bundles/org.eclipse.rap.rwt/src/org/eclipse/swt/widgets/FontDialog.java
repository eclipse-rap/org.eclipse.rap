/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Ralf Zahn (ARS) - initial API and implementation
 *   EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;

import java.util.*;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.RWTMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

/**
 * Instances of this class allow the user to select a font from all available
 * fonts in the system.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is intended to be subclassed <em>only</em> within the
 * SWT implementation.
 * </p>
 *
 * @since 1.3
 */
public class FontDialog extends Dialog {

  private static final int BUTTON_WIDTH = 60;

  private Shell shell;
  private FontData fontData;
  private RGB rgb;
  private boolean committed;
  private Text fontFamilyText;
  private List fontFamilyList;
  private Spinner fontSizeSpinner;
  private Button boldCheckbox;
  private Button italicCheckbox;
  private Label colorLabel;
  private Label previewLabel;

  /**
   * Constructs a new instance of this class given only its parent.
   *
   * @param parent a shell which will be the parent of the new instance
   * @exception IllegalArgumentException <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *              </ul>
   * @exception SWTException <ul>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the parent</li>
   *              <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed
   *              subclass</li>
   *              </ul>
   */
  public FontDialog( final Shell parent ) {
    this( parent, SWT.APPLICATION_MODAL );
  }

  /**
   * Constructs a new instance of this class given its parent and a style value
   * describing its behavior and appearance.
   * <p>
   * The style value is either one of the style constants defined in class
   * <code>SWT</code> which is applicable to instances of this class, or must be
   * built by <em>bitwise OR</em>'ing together (that is, using the
   * <code>int</code> "|" operator) two or more of those <code>SWT</code> style
   * constants. The class description lists the style constants that are
   * applicable to the class. Style bits are also inherited from superclasses.
   * </p>
   *
   * @param parent a shell which will be the parent of the new instance
   * @param style the style of dialog to construct
   * @exception IllegalArgumentException <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *              </ul>
   * @exception SWTException <ul>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the parent</li>
   *              <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed
   *              subclass</li>
   *              </ul>
   */
  public FontDialog( final Shell parent, final int style ) {
    super( parent, checkStyle( parent, style ) );
    checkSubclass();
    setText( RWTMessages.getMessage( "RWT_FontDialogTitle" ) );
  }

  /**
   * Returns a FontData set describing the font that was selected in the dialog,
   * or null if none is available.
   *
   * @return the FontData for the selected font, or null
   */
  public FontData[] getFontList() {
    FontData[] result = null;
    if( fontData != null ) {
      result = new FontData[ 1 ];
      result[ 0 ] = fontData;
    }
    return result;
  }

  /**
   * Sets the set of FontData objects describing the font to be selected by
   * default in the dialog, or null to let the platform choose one.
   *
   * @param fontData the set of FontData objects to use initially, or null to
   *          let the platform select a default when open() is called
   * @see Font#getFontData
   */
  public void setFontList( final FontData[] fontData ) {
    if( fontData != null && fontData.length > 0 ) {
      this.fontData = fontData[ 0 ];
    } else {
      this.fontData = null;
    }
  }

  /**
   * Returns an RGB describing the color that was selected in the dialog, or
   * null if none is available.
   *
   * @return the RGB value for the selected color, or null
   * @see PaletteData#getRGBs
   */
  public RGB getRGB() {
    return rgb;
  }

  /**
   * Sets the RGB describing the color to be selected by default in the dialog,
   * or null to let the platform choose one.
   *
   * @param rgb the RGB value to use initially, or null to let the platform
   *          select a default when open() is called
   * @see PaletteData#getRGBs
   */
  public void setRGB( final RGB rgb ) {
    this.rgb = rgb;
  }

  /**
   * Makes the dialog visible and brings it to the front of the display.
   *
   * @return a FontData object describing the font that was selected, or null if
   *         the dialog was cancelled or an error occurred
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the dialog has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the dialog</li>
   *              </ul>
   */
  public FontData open() {
    initializeDefaults();
    createShell();
    createControls();
    updateControls();
    addChangeListeners();
    layoutAndCenterShell();
    openShell();
    return fontData;
  }

  private void initializeDefaults() {
    if( fontData == null ) {
      Display display = parent.getDisplay();
      FontData systemFontData = display.getSystemFont().getFontData()[ 0 ];
      String fontName = getFirstFontName( systemFontData.getName() );
      int fontHeight = systemFontData.getHeight();
      int fontStyle = systemFontData.getStyle();
      fontData = new FontData( fontName, fontHeight, fontStyle );
    }
    if( rgb == null ) {
      rgb = new RGB( 0, 0, 0 );
    }
  }

  static String getFirstFontName( final String fontName ) {
    String result = fontName;
    int index = result.indexOf( ',' );
    if( index != -1 ) {
      result = result.substring( 0, index );
    }
    result = result.trim();
    if( result.length() > 2 ) {
      char firstChar = result.charAt( 0 );
      char lastChar = result.charAt( result.length() - 1 );
      boolean isQuoted =  ( firstChar == '\'' && lastChar == '\'' )
                       || ( firstChar == '"' && lastChar == '"' );
      if( isQuoted ) {
        result = result.substring( 1, result.length() - 1 );
      }
    }
    return result;
  }

  private void createShell() {
    int style = SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL;
    shell = new Shell( parent, style );
    shell.setText( getText() );
    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( final ShellEvent e ) {
        handleShellClose();
      }
    } );
  }

  private void layoutAndCenterShell() {
    Point prefSize = shell.computeSize( SWT.DEFAULT, SWT.DEFAULT );
    // leave some space in preview area for larger fonts
    prefSize.y += 50;
    shell.setSize( prefSize );
    Rectangle parentSize = parent.getBounds();
    int locationX = ( parentSize.width - prefSize.x ) / 2 + parentSize.x;
    int locationY = ( parentSize.height - prefSize.y ) / 2 + parentSize.y;
    shell.setLocation( locationX, locationY );
  }

  private void openShell() {
    shell.open();
    Display display = parent.getDisplay();
    while( !shell.isDisposed() ) {
      if( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    shell = null;
  }

  private void createControls() {
    GridLayout mainLayout = new GridLayout( 2, true );
    mainLayout.marginWidth = 10;
    mainLayout.marginHeight = 10;
    mainLayout.horizontalSpacing = 10;
    mainLayout.verticalSpacing = 10;
    shell.setLayout( mainLayout );
    createLeftArea( shell );
    createRightArea( shell );
    createPreviewArea( shell );
    createButtonArea( shell );
    fillAvailableFonts();
  }

  private void createLeftArea( final Composite parent ) {
    Composite leftArea = createVerticalArea( parent );
    createFontFamilyGroup( leftArea );
  }

  private void createRightArea( final Composite parent ) {
    Composite rightArea = createVerticalArea( parent );
    createFontSizeGroup( rightArea );
    createFontStyleGroup( rightArea );
    createFontColorGroup( rightArea );
  }

  private static Composite createVerticalArea( Composite parent ) {
    Composite result = new Composite( parent, SWT.NONE );
    result.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
    GridLayout layout = new GridLayout();
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    result.setLayout( layout );
    return result;
  }

  private void createFontFamilyGroup( final Composite parent ) {
    Group result = new Group( parent, SWT.NONE );
    result.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    result.setText( RWTMessages.getMessage( "RWT_FontDialogFontFamilyTitle" ) );
    result.setLayout( new GridLayout() );
    fontFamilyText = new Text( result, SWT.BORDER );
    GridData textData = new GridData( SWT.FILL, SWT.CENTER, true, false );
    fontFamilyText.setLayoutData( textData );
    fontFamilyList = new List( result, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER );
    GridData listData = new GridData( SWT.FILL, SWT.FILL, true, true );
    fontFamilyList.setLayoutData( listData );
    fontFamilyList.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        int selectionIndex = fontFamilyList.getSelectionIndex();
        if( selectionIndex != -1 ) {
          fontFamilyText.setText( fontFamilyList.getItem( selectionIndex ) );
        }
      }
    } );
  }

  private void createFontSizeGroup( final Composite parent ) {
    Group result = new Group( parent, SWT.NONE );
    result.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
    result.setText( RWTMessages.getMessage( "RWT_FontDialogFontSizeTitle" ) );
    result.setLayout( new GridLayout() );
    fontSizeSpinner = new Spinner( result, SWT.BORDER );
    fontSizeSpinner.setDigits( 0 );
    fontSizeSpinner.setMinimum( 0 );
    fontSizeSpinner.setMaximum( 200 );
    GridData spinnerData = new GridData( SWT.FILL, SWT.FILL, true, true );
    fontSizeSpinner.setLayoutData( spinnerData );
  }

  private void createFontStyleGroup( final Composite parent ) {
    Group result = new Group( parent, SWT.NONE );
    result.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    result.setText( RWTMessages.getMessage( "RWT_FontDialogFontStyleTitle" ) );
    GridLayout layout = new GridLayout();
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    result.setLayout( layout );
    boldCheckbox = new Button( result, SWT.CHECK );
    String bold = RWTMessages.getMessage( "RWT_FontDialogFontStyleBold" );
    boldCheckbox.setText( bold );
    FontData normalFont = boldCheckbox.getFont().getFontData()[ 0 ];
    boldCheckbox.setFont( Graphics.getFont( normalFont.getName(),
                                            normalFont.getHeight(),
                                            SWT.BOLD ) );
    italicCheckbox = new Button( result, SWT.CHECK );
    String italic = RWTMessages.getMessage( "RWT_FontDialogFontStyleItalic" );
    italicCheckbox.setText( italic );
    italicCheckbox.setFont( Graphics.getFont( normalFont.getName(),
                                              normalFont.getHeight(),
                                              SWT.ITALIC ) );
  }

  private void createFontColorGroup( final Composite parent ) {
    Group result = new Group( parent, SWT.NONE );
    result.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
    result.setText( RWTMessages.getMessage( "RWT_FontDialogFontColorTitle" ) );
    result.setLayout( new GridLayout( 2, false ) );
    colorLabel = new Label( result, SWT.BORDER );
    colorLabel.setLayoutData( new GridData( 20, 20 ) );
    Button changeColorButton = new Button( result, SWT.PUSH );
    String text = RWTMessages.getMessage( "RWT_FontDialogFontColorSelect" );
    changeColorButton.setText( text );
    changeColorButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        openColorDialog();
      }
    } );
  }

  private void openColorDialog() {
    ColorDialog dialog = new ColorDialog( shell );
    dialog.setRGB( rgb );
    RGB selected = dialog.open();
    if( selected != null ) {
      rgb = selected;
      updateControls();
    }
  }

  private void addChangeListeners() {
    SelectionListener selectionListener = new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        updateFontData();
      }
    };
    fontSizeSpinner.addSelectionListener( selectionListener );
    boldCheckbox.addSelectionListener( selectionListener );
    italicCheckbox.addSelectionListener( selectionListener );
    fontFamilyText.addModifyListener( new ModifyListener() {
      public void modifyText( final ModifyEvent event ) {
        String text = fontFamilyText.getText();
        selectFontFamilyInList( text );
        updateFontData();
      }
    } );
  }

  private void createPreviewArea( final Composite parent ) {
    Composite previewArea = new Composite( parent, SWT.BORDER );
    GridData areaData = new GridData( SWT.FILL, SWT.FILL, true, true );
    areaData.minimumWidth = 300;
    areaData.horizontalSpan = 2;
    previewArea.setLayoutData( areaData );
    previewArea.setLayout( new GridLayout() );
    previewLabel = new Label( previewArea, SWT.CENTER );
    GridData labelData = new GridData( SWT.FILL, SWT.CENTER, true, true );
    previewLabel.setLayoutData( labelData );
    String previewText = RWTMessages.getMessage( "RWT_FontDialogPreviewText" );
    previewLabel.setText( previewText );
    Display display = parent.getDisplay();
    Color bgColor = display.getSystemColor( SWT.COLOR_LIST_BACKGROUND );
    previewArea.setBackground( bgColor );
    previewArea.setBackgroundMode( SWT.INHERIT_DEFAULT );
  }

  private void createButtonArea( final Composite parent ) {
    Composite buttonComposite = new Composite( parent, SWT.NONE );
    GridData layoutData = new GridData( SWT.RIGHT, SWT.CENTER, false, false );
    layoutData.horizontalSpan = 2;
    buttonComposite.setLayoutData( layoutData );
    GridLayout buttonCompLayout = new GridLayout( 2, true );
    buttonCompLayout.marginWidth = 0;
    buttonCompLayout.marginHeight = 0;
    buttonComposite.setLayout( buttonCompLayout );
    String okText = SWT.getMessage( "SWT_OK" );
    Button okButton = createButton( buttonComposite, okText );
    parent.getShell().setDefaultButton( okButton );
    okButton.forceFocus();
    String cancelText = SWT.getMessage( "SWT_Cancel" );
    Button cancelButton = createButton( buttonComposite, cancelText );
    okButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        committed = true;
        shell.close();
      }
    } );
    cancelButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        shell.close();
      }
    } );
  }

  private static Button createButton( final Composite parent, final String text ) {
    Button result = new Button( parent, SWT.PUSH );
    result.setText( text );
    GridData data = new GridData( GridData.HORIZONTAL_ALIGN_FILL );
    int widthHint = convertHorizontalDLUsToPixels( result, BUTTON_WIDTH );
    Point minSize = result.computeSize( SWT.DEFAULT, SWT.DEFAULT );
    data.widthHint = Math.max( widthHint, minSize.x );
    result.setLayoutData( data );
    return result;
  }

  private void handleShellClose() {
    if( !committed ) {
      fontData = null;
      rgb = null;
    }
    committed = false;
  }

  private void fillAvailableFonts() {
    Collection<String> fontFamilies = new HashSet<String>();
    FontData[] fontList = parent.getDisplay().getFontList( null, true );
    if( fontList != null ) {
      for( int i = 0; i < fontList.length; i++ ) {
        fontFamilies.add( fontList[ i ].getName() );
      }
    }
    String[] availableFontNames = fontFamilies.toArray( new String[ fontFamilies.size() ] );
    Arrays.sort( availableFontNames );
    fontFamilyList.setItems( availableFontNames );
  }

  private void updateControls() {
    String fontName = fontData.getName();
    if( !fontFamilyText.getText().equals( fontName ) ) {
      fontFamilyText.setText( fontName );
    }
    selectFontFamilyInList( fontName );
    fontSizeSpinner.setSelection( fontData.getHeight() );
    boldCheckbox.setSelection( ( fontData.getStyle() & SWT.BOLD ) != 0 );
    italicCheckbox.setSelection( ( fontData.getStyle() & SWT.ITALIC ) != 0 );
    updatePreview();
  }

  private void selectFontFamilyInList( final String fontFamily ) {
    fontFamilyList.deselectAll();
    String[] items = fontFamilyList.getItems();
    for( int i = 0; i < items.length; i++ ) {
      String item = items[ i ].toLowerCase( Locale.ENGLISH );
      if( fontFamily.toLowerCase( Locale.ENGLISH ).equals( item ) ) {
        fontFamilyList.select( i );
      }
    }
  }

  private void updatePreview() {
    if( previewLabel != null ) {
      Font font = Graphics.getFont( fontData );
      previewLabel.setFont( font );
      Color color = Graphics.getColor( rgb );
      previewLabel.setForeground( color );
      colorLabel.setBackground( color );
      previewLabel.getParent().layout( true );
    }
  }

  private void updateFontData() {
    String name = fontData.getName();
    if( fontFamilyText.getText().length() > 0 ) {
      name = fontFamilyText.getText();
    }
    int height = fontSizeSpinner.getSelection();
    int style = SWT.NORMAL;
    if( boldCheckbox.getSelection() ) {
      style |= SWT.BOLD;
    }
    if( italicCheckbox.getSelection() ) {
      style |= SWT.ITALIC;
    }
    fontData = new FontData( name, height, style );
    updateControls();
  }
}
