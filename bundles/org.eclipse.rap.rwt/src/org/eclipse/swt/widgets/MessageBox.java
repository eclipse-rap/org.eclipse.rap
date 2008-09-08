/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import java.util.StringTokenizer;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

/**
 * Instances of this class are used to inform or warn the user.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>ICON_ERROR, ICON_INFORMATION, ICON_QUESTION, ICON_WARNING, ICON_WORKING</dd>
 * <dd>OK, OK | CANCEL</dd>
 * <dd>YES | NO, YES | NO | CANCEL</dd>
 * <dd>RETRY | CANCEL</dd>
 * <dd>ABORT | RETRY | IGNORE</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles ICON_ERROR, ICON_INFORMATION, ICON_QUESTION,
 * ICON_WARNING and ICON_WORKING may be specified.
 * </p><p>
 * IMPORTANT: This class is intended to be subclassed <em>only</em>
 * within the SWT implementation.
 * </p>
 * @since 1.2
 */
public class MessageBox extends Dialog {

  private static final int BUTTON_WIDTH = 61;
  private static final int HORIZONTAL_DIALOG_UNIT_PER_CHAR = 4;
  private static final int MAX_WIDTH = 640;

  private Shell shell;
	private String message = "";
	private int result;

  /**
   * Constructs a new instance of this class given only its parent.
   *
   * @param parent a shell which will be the parent of the new instance
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   */
  public MessageBox( final Shell parent ) {
  	this( parent, SWT.OK | SWT.ICON_INFORMATION | SWT.APPLICATION_MODAL );
  }

  /**
   * Constructs a new instance of this class given its parent
   * and a style value describing its behavior and appearance.
   * <p>
   * The style value is either one of the style constants defined in
   * class <code>SWT</code> which is applicable to instances of this
   * class, or must be built by <em>bitwise OR</em>'ing together
   * (that is, using the <code>int</code> "|" operator) two or more
   * of those <code>SWT</code> style constants. The class description
   * lists the style constants that are applicable to the class.
   * Style bits are also inherited from superclasses.
   *
   * @param parent a shell which will be the parent of the new instance
   * @param style the style of dialog to construct
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   */
  public MessageBox( final Shell parent, final int style ) {
  	super( parent, checkStyle ( style ) );
  	checkSubclass();
  }

  static int checkStyle( final int style ) {
    int chkStyle = 0;
  	int mask = ( SWT.YES | SWT.NO
  	           | SWT.OK | SWT.CANCEL
  	           | SWT.ABORT | SWT.RETRY | SWT.IGNORE );
  	int bits = style & mask;
  	if(    bits == SWT.OK
  	    || bits == ( SWT.OK | SWT.CANCEL ) )
  	{
  	  chkStyle = style;
  	} else if(    bits == ( SWT.YES | SWT.NO )
  	           || bits == ( SWT.YES | SWT.NO | SWT.CANCEL ) )
  	{
  	  chkStyle = style;
  	} else if(    bits == ( SWT.RETRY | SWT.CANCEL )
  	           || bits == ( SWT.ABORT | SWT.RETRY | SWT.IGNORE ) )
  	{
  	  chkStyle = style;
  	} else {
  	  chkStyle = ( style & ~mask ) | SWT.OK;
  	}
  	return chkStyle;
  }

  /**
   * Returns the dialog's message, or an empty string if it does not have one.
   * The message is a description of the purpose for which the dialog was opened.
   * This message will be visible in the dialog while it is open.
   *
   * @return the message
   */
  public String getMessage() {
  	return message;
  }

  /**
   * Sets the dialog's message, which is a description of
   * the purpose for which it was opened. This message will be
   * visible on the dialog while it is open.
   *
   * @param string the message
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   * </ul>
   */
  public void setMessage( final String string ) {
    if( string == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    message = string;
  }

  /**
   * Makes the dialog visible and brings it to the front
   * of the display.
   *
   * @return the ID of the button that was selected to dismiss the
   *         message box (e.g. SWT.OK, SWT.CANCEL, etc.)
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the dialog has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the dialog</li>
   * </ul>
   */
  public int open() {
    shell = new Shell( parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL );
    shell.setText( getText() );

    createControls( shell );

    Rectangle parentSize = parent.getBounds();
    Point prefSize = shell.computeSize( SWT.DEFAULT, SWT.DEFAULT );
    if( prefSize.x > MAX_WIDTH ) {
      prefSize.x = MAX_WIDTH;
    }
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

    return result;
  }

  private void createControls( final Composite parent ) {
    Display display = parent.getDisplay();
    Image icon = null;
    parent.setLayout( new GridLayout( 2, false ) );

    // Icon
    int systemImageID = -1;
    if( ( style & SWT.ICON_ERROR ) != 0 ) {
      systemImageID = SWT.ICON_ERROR;
    } else if( ( style & SWT.ICON_INFORMATION ) != 0 ) {
      systemImageID = SWT.ICON_INFORMATION;
    } else if( ( style & SWT.ICON_QUESTION ) != 0 ) {
      systemImageID = SWT.ICON_QUESTION;
    } else if( ( style & SWT.ICON_WARNING ) != 0 ) {
      systemImageID = SWT.ICON_WARNING;
    } else if( ( style & SWT.ICON_WORKING ) != 0 ) {
      systemImageID = SWT.ICON_WORKING;
    }
    if( systemImageID != -1 ) {
      icon = display.getSystemImage( systemImageID );
      createIcon( parent, icon );
    }

    // Text
    createText( parent, icon );

    // Buttons
    Composite buttonComp = new Composite( parent, SWT.NONE );
    buttonComp.setLayout( new GridLayout( 0, true ) );
    GridData buttonData = new GridData( SWT.CENTER, SWT.CENTER, true, false );
    buttonData.horizontalSpan = 2;
    buttonComp.setLayoutData( buttonData );

    if( ( style & SWT.YES ) == SWT.YES ) {
      createButton( buttonComp, SWT.getMessage( "SWT_Yes" ), SWT.YES );
    }
    if( ( style & SWT.NO ) == SWT.NO ) {
      createButton( buttonComp, SWT.getMessage( "SWT_No" ), SWT.NO );
    }
    if( ( style & SWT.OK ) == SWT.OK ) {
      createButton( buttonComp, SWT.getMessage( "SWT_OK" ), SWT.OK );
    }
    if( ( style & SWT.ABORT ) == SWT.ABORT ) {
      createButton( buttonComp, SWT.getMessage( "SWT_Abort" ), SWT.ABORT );
    }
    if( ( style & SWT.RETRY ) == SWT.RETRY ) {
      createButton( buttonComp, SWT.getMessage( "SWT_Retry" ), SWT.RETRY );
    }
    if( ( style & SWT.CANCEL ) == SWT.CANCEL ) {
      createButton( buttonComp, SWT.getMessage( "SWT_Cancel" ), SWT.CANCEL );
    }
    if( ( style & SWT.IGNORE ) == SWT.IGNORE ) {
      createButton( buttonComp, SWT.getMessage( "SWT_Ignore" ), SWT.IGNORE );
    }
    buttonComp.getChildren()[0].forceFocus();
  }

  private Button createButton( final Composite parent,
                               final String btnText,
                               final int resCode )
  {
    // Increment the number of columns in the button bar
    ( ( GridLayout ) parent.getLayout() ).numColumns++;
    Button btn = new Button( parent, SWT.PUSH );
    // Set button layout data
    GridData data = new GridData( GridData.HORIZONTAL_ALIGN_FILL );
    int widthHint = convertHorizontalDLUsToPixels( BUTTON_WIDTH );
    Point minSize = btn.computeSize( SWT.DEFAULT, SWT.DEFAULT, true );
    data.widthHint = Math.max( widthHint, minSize.x );
    btn.setLayoutData( data );
    // Set text
    btn.setText( btnText );
    btn.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        result = resCode;
        shell.close();
      }
    } );
    return btn;
  }

  private void createIcon( final Composite parent, final Image icon ) {
    Label iconLabel = new Label( parent, SWT.CENTER );
    // Set label layout data
    GridData data = new GridData( SWT.CENTER, SWT.TOP, false, false );
    data.widthHint = icon.getBounds().width + 20;
    iconLabel.setLayoutData( data );
    // Set image
    iconLabel.setImage( icon );
  }

  private void createText( final Composite parent, final Image icon ) {
    Label textLabel = new Label( parent, SWT.WRAP );
    String text = getMessage();
    Font font = textLabel.getFont();
    // Set label layout data
    GridData data = new GridData( GridData.HORIZONTAL_ALIGN_FILL );
    int iconWidth = icon == null ? 0 : icon.getBounds().width;
    int maxTextWidth = MAX_WIDTH - iconWidth - 20;
    // Determine the max line length
    int maxLineWidth = 0;
    StringTokenizer st = new StringTokenizer( text, "\n" );
    while( st.hasMoreTokens() ) {
      String line = st.nextToken();
      int lineWidth = TextSizeDetermination.stringExtent( font, line ).x;
      maxLineWidth = Math.max( maxLineWidth, lineWidth );
    }
    // Set wrap width
    if( maxLineWidth > maxTextWidth ) {
      data.widthHint = maxTextWidth;
    }
    textLabel.setLayoutData( data );
    textLabel.setText( text );
  }

  private int convertHorizontalDLUsToPixels( final int dlus ) {
    Font dialogFont = shell.getFont();
    float charWidth = Graphics.getAvgCharWidth( dialogFont );
    float width = charWidth * dlus + HORIZONTAL_DIALOG_UNIT_PER_CHAR / 2;
    return ( int)( width / HORIZONTAL_DIALOG_UNIT_PER_CHAR );
  }
}
