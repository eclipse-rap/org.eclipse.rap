/******************************************************************************* 
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.PaletteData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

/**
 * Instances of this class allow the user to select a color from a predefined
 * set of available colors.
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
 * @see <a href="http://www.eclipse.org/swt/examples.php">SWT Example:
 *      ControlExample, Dialog tab</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 * @since 1.2
 */
public class ColorDialog extends Dialog {

  private class PaletteListener extends MouseAdapter {

    private RGB rgb;

    public PaletteListener( final RGB rgb ) {
      this.rgb = rgb;
    }

    public void mouseDown( final MouseEvent e ) {
      setColorFromPalette( rgb );
    }
  }
  
  private class SpinnerListener implements ModifyListener {

    private Spinner spin;
    private int colorIndex;

    public SpinnerListener( final Spinner spin, final int colorIndex ) {
      this.spin = spin;
      this.colorIndex = colorIndex;
    }

    public void modifyText( ModifyEvent event ) {
      setColorFomSpinner( colorIndex, spin.getSelection() );
    }
  }
  
  // Layout
  private static final int BUTTON_WIDTH = 60;
  private static final int HORIZONTAL_DIALOG_UNIT_PER_CHAR = 4;
  private static final int PALETTE_BOX_SIZE = 12;
  private static final int PALETTE_BOXES_IN_ROW = 14;
  private static final int COLOR_DISPLAY_BOX_SIZE = 76;
  private static final int MAX_RGB_COMPONENT_VALUE = 255;
  
  // Color components
  private static final int RED = 0;
  private static final int GREEN = 1;
  private static final int BLUE = 2;
  
  // Palette colors
  private static final RGB[] PALETTE_COLORS = new RGB[]{
    new RGB( 0, 0, 0 ),
    new RGB( 70, 70, 70 ),
    new RGB( 120, 120, 120 ),
    new RGB( 153, 0, 48 ),
    new RGB( 237, 28, 36 ),
    new RGB( 255, 126, 0 ),
    new RGB( 255, 194, 14 ),
    new RGB( 255, 242, 0 ),
    new RGB( 168, 230, 29 ),
    new RGB( 34, 177, 76 ),
    new RGB( 0, 183, 239 ),
    new RGB( 77, 109, 243 ),
    new RGB( 47, 54, 153 ),
    new RGB( 111, 49, 152 ),
    new RGB( 255, 255, 255 ),
    new RGB( 220, 220, 220 ),
    new RGB( 180, 180, 180 ),
    new RGB( 156, 90, 60 ),
    new RGB( 255, 163, 177 ),
    new RGB( 229, 170, 122 ),
    new RGB( 245, 228, 156 ),
    new RGB( 255, 249, 189 ),
    new RGB( 211, 249, 188 ),
    new RGB( 157, 187, 97 ),
    new RGB( 153, 217, 234 ),
    new RGB( 112, 154, 209 ),
    new RGB( 84, 109, 142 ),
    new RGB( 181, 165, 213 )
  };
  
  private Shell shell;
  private RGB rgb;
  private Label colorDisplay;
  private Spinner redSpin; 
  private Spinner blueSpin;
  private Spinner greenSpin;

  /**
   * Constructs a new instance of this class given only its parent.
   * 
   * @param parent a composite control which will be the parent of the new
   *          instance
   * @exception IllegalArgumentException <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *              </ul>
   * @exception SWTException <ul>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the parent</li>
   *              <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed
   *              subclass</li>
   *              </ul>
   * @see SWT
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public ColorDialog( final Shell parent ) {
    this( parent, SWT.APPLICATION_MODAL );
    setText( "Color" );
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
   * @param parent a composite control which will be the parent of the new
   *          instance (cannot be null)
   * @param style the style of control to construct
   * @exception IllegalArgumentException <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *              </ul>
   * @exception SWTException <ul>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the parent</li>
   *              <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed
   *              subclass</li>
   *              </ul>
   * @see SWT
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public ColorDialog( final Shell parent, final int style ) {
    super( parent, style );
    setText( "Color" );
  }

  /**
   * Makes the receiver visible and brings it to the front of the display.
   * 
   * @return the selected color, or null if the dialog was cancelled, no color
   *         was selected, or an error occurred
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   */
  public RGB open() {
    shell = new Shell( parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL );
    shell.setText( getText() );
    createControls( shell );
    if( rgb == null ) {
      rgb = new RGB( 255, 255, 255 );
    }
    updateColorDisplay();
    updateSpinners();
    Rectangle parentSize = parent.getBounds();
    Point prefSize = shell.computeSize( SWT.DEFAULT, SWT.DEFAULT );
    shell.setSize( prefSize );
    int locationX = ( parentSize.width - prefSize.x ) / 2 + parentSize.x;
    int locationY = ( parentSize.height - prefSize.y ) / 2 + parentSize.y;
    shell.setLocation( new Point( locationX, locationY ) );
    shell.pack();
    shell.open();
    Display display = parent.getDisplay();
    while( !shell.isDisposed() ) {
      if( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    return rgb;
  }

  /**
   * Returns the currently selected color in the receiver.
   * 
   * @return the RGB value for the selected color, may be null
   * @see PaletteData#getRGBs
   */
  public RGB getRGB() {
    return rgb;
  }

  /**
   * Sets the receiver's selected color to be the argument.
   * 
   * @param rgb the new RGB value for the selected color, may be null to let the
   *          platform select a default when open() is called
   * @see PaletteData#getRGBs
   */
  public void setRGB( final RGB rgb ) {
    this.rgb = rgb;
  }

  private void updateColorDisplay() {
    colorDisplay.setBackground( Graphics.getColor( rgb ) );
  }

  private void updateSpinners() {
    redSpin.setSelection( rgb.red );
    greenSpin.setSelection( rgb.green );
    blueSpin.setSelection( rgb.blue );
  }

  private void createControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 1, false ) );
    createColorArea( parent );
    createPalette( parent );
    createButtons( parent );
  }

  private void createColorArea( final Composite parent ) {
    // Current color selection display
    Composite areaComp = new Composite( parent, 0 );
    GridData compData = new GridData( SWT.CENTER, SWT.CENTER, true, false );
    areaComp.setLayoutData( compData );
    areaComp.setLayout( new GridLayout( 2, true ) );
    colorDisplay = new Label( areaComp, SWT.BORDER | SWT.FLAT );
    GridData data = new GridData();
    data.widthHint = COLOR_DISPLAY_BOX_SIZE;
    data.heightHint = COLOR_DISPLAY_BOX_SIZE;
    colorDisplay.setLayoutData( data );
    // Color components spinners
    Composite spinComp = new Composite( areaComp, 0 );
    spinComp.setLayout( new GridLayout( 2, true ) );
    Label rLabel = new Label( spinComp, 0 );
    rLabel.setText( "Red:" );
    redSpin = new Spinner( spinComp, 0 );
    redSpin.setMaximum( MAX_RGB_COMPONENT_VALUE );
    redSpin.addModifyListener( new SpinnerListener( redSpin, RED ) );
    //
    Label gLabel = new Label( spinComp, 0 );
    gLabel.setText( "Green:" );
    greenSpin = new Spinner( spinComp, 0 );
    greenSpin.setMaximum( MAX_RGB_COMPONENT_VALUE );
    greenSpin.addModifyListener( new SpinnerListener( greenSpin, GREEN ) );
    //
    Label bLabel = new Label( spinComp, 0 );
    bLabel.setText( "Blue:" );
    blueSpin = new Spinner( spinComp, 0 );
    blueSpin.setMaximum( MAX_RGB_COMPONENT_VALUE );
    blueSpin.addModifyListener( new SpinnerListener( blueSpin, BLUE ) );
  }

  private void createPalette( final Composite parent ) {
    Composite paletteComp = new Composite( parent, SWT.NONE );
    GridData palData = new GridData( SWT.CENTER, SWT.CENTER, true, false );
    paletteComp.setLayoutData( palData );
    paletteComp.setLayout( new GridLayout( PALETTE_BOXES_IN_ROW, true ) );
    Label title = new Label( paletteComp, 0 );
    title.setText( "Basic Colors:" );
    GridData titleData = new GridData( SWT.LEFT, SWT.CENTER, true, false );
    titleData.horizontalSpan = PALETTE_BOXES_IN_ROW;
    title.setLayoutData( titleData );
    for( int i = 0; i < PALETTE_COLORS.length; i++ ) {
      createPaletteColorBox( paletteComp, PALETTE_COLORS[ i ] );
    }
  }

  private Label createPaletteColorBox( final Composite parent, final RGB color )
  {
    Label result = new Label( parent, SWT.BORDER | SWT.FLAT );
    result.setBackground( Graphics.getColor( color ) );
    GridData data = new GridData();
    data.widthHint = PALETTE_BOX_SIZE;
    data.heightHint = PALETTE_BOX_SIZE;
    result.setLayoutData( data );
    result.addMouseListener( new PaletteListener( color ) );
    return result;
  }

  private void createButtons( final Composite parent ) {
    Composite buttonComp = new Composite( parent, SWT.NONE );
    buttonComp.setLayout( new GridLayout( 0, true ) );
    GridData buttonData = new GridData( SWT.RIGHT, SWT.CENTER, true, false );
    buttonComp.setLayoutData( buttonData );
    Button okButton
      = createButton( buttonComp, SWT.getMessage( "SWT_OK" ), SWT.OK );
    createButton( buttonComp, SWT.getMessage( "SWT_Cancel" ), SWT.CANCEL );
    okButton.forceFocus();
  }

  private Button createButton( final Composite parent,
                               final String text,
                               final int returnCode )
  {
    // Increment the number of columns in the button bar
    ( ( GridLayout )parent.getLayout() ).numColumns++;
    Button result = new Button( parent, SWT.PUSH );
    // Set button layout data
    GridData data = new GridData( GridData.HORIZONTAL_ALIGN_FILL );
    int widthHint = convertHorizontalDLUsToPixels( BUTTON_WIDTH );
    Point minSize = result.computeSize( SWT.DEFAULT, SWT.DEFAULT, true );
    data.widthHint = Math.max( widthHint, minSize.x );
    result.setLayoutData( data );
    // Set text
    result.setText( text );
    result.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        if( returnCode == SWT.CANCEL ) {
          ColorDialog.this.rgb = null;
        }
        shell.close();
      }
    } );
    return result;
  }

  private int convertHorizontalDLUsToPixels( final int dlus ) {
    Font dialogFont = shell.getFont();
    float charWidth = Graphics.getAvgCharWidth( dialogFont );
    float width = charWidth * dlus + HORIZONTAL_DIALOG_UNIT_PER_CHAR / 2;
    return ( int )( width / HORIZONTAL_DIALOG_UNIT_PER_CHAR );
  }

  private void setColorFomSpinner( final int colorIndex, final int value ) {
    switch( colorIndex ) {
      case RED:
        rgb.red = value;
      break;
      case GREEN:
        rgb.green = value;
      break;
      case BLUE:
        rgb.blue = value;
      break;
      default:
      break;
    }
    updateColorDisplay();
  }

  private void setColorFromPalette( final RGB selectedColor ) {
    rgb.blue = selectedColor.blue;
    rgb.green = selectedColor.green;
    rgb.red = selectedColor.red;
    updateColorDisplay();
    updateSpinners();
  }
}
