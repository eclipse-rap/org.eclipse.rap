/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.widgets;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.widgets.IFileUploadAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;


// Known limitations:
// 1. The border can not be clicked.
// 2. Pressing the button by keyboard (Space/Enter) works, but will have
//    no visual feedback (pressed state).
// 3. The mouse-cursor can not be changed in Firefox, it will always be
//    the default, not the "pointer" hand.
// 4. In Chrome the keyboard control will only work if the button has been
//    focused with TAB, therefore the focus-frame will not be displayed if
//    focused with mouse in Chrome. Its still focused.

/**
 * A button-like widget allowing to select a file from the client's file system and to upload this
 * file to a given URL using HTTP POST.
 * <p>
 * Note that although this class is a subclass of <code>Composite</code>,
 * it does not make sense to set a layout on it.
 * </p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 *
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * @since 1.4
 */
public class FileUpload extends Canvas {
  private static final long serialVersionUID = 1L;

  private final IFileUploadAdapter fileUploadAdapter;
  private String text;
  private Image image;
  private String fileName;
  private String url;

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
   * </p>
   *
   * @param parent a composite control which will be the parent of the new instance (cannot be null)
   * @param style the style of control to construct
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   */
  public FileUpload( Composite parent, int style ) {
    super( parent, checkStyle( style ) );
    fileUploadAdapter = new FileUploadAdapter();
    text = "";
  }

  /**
   * Sets the receiver's text.
   *
   * @param text the new text
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the text is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setText( String text ) {
    if( text == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    checkWidget();
    this.text = text;
  }

  /**
   * Returns the receiver's text, which will be an empty
   * string if it has never been set.
   *
   * @return the receiver's text
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public String getText() {
    checkWidget();
    return text;
  }

  /**
   * Sets the receiver's image to the argument, which may be
   * <code>null</code> indicating that no image should be displayed.
   *
   * @param image the image to display on the receiver (may be <code>null</code>)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the image has been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setImage( Image image ) {
    checkWidget();
    if( image != null && image.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    this.image = image;
  }

  /**
   * Returns the receiver's image if it has one, or null
   * if it does not.
   *
   * @return the receiver's image
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Image getImage() {
    checkWidget();
    return image;
  }

  /**
   * Returns the selected file name, without the path. If no file name has been selected,
   * <code>null</code> is returned.
   * 
   * @return the selected file name
   * 
   * @exception SWTException <ul>
   *   <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *   <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public String getFileName() {
    checkWidget();
    return fileName;
  }

  /**
   * Starts to upload the selected file to the given URL using HTTP POST. If no file has been
   * selected, nothing happens.
   * <p>
   * <strong>Note:</strong> Calling this method while a file is still uploading might interrupt the
   * ongoing upload. Due to restrictions of the client, there is no feedback for success or failure
   * of the upload. This information can only be obtained from the server that accepts the upload.
   * </p>
   * 
   * @param url the URL to upload to, must not be <code>null</code>
   * 
   * @exception IllegalArgumentException <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the url is null</li>
   *              </ul>
   * @exception SWTException <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
   *              receiver</li>
   *              </ul>
   */
  public void submit( String url ) {
    if( url == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    checkWidget();
    if( fileName != null ) {
      this.url  = url;      
    }
  }

  /**
   * Adds the listener to the collection of listeners who will
   * be notified when the user changes the receiver's selection, by sending
   * it one of the messages defined in the <code>SelectionListener</code>
   * interface.
   * <p>
   * <code>widgetSelected</code> is called when the selected file changes.
   * <code>widgetDefaultSelected</code> is not called.
   * </p>
   *
   * @param listener the listener which should be notified when the user changes the receiver's selection
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see SelectionListener
   * @see SelectionEvent
   * @see #removeSelectionListener
   */
  public void addSelectionListener( SelectionListener listener ) {
    checkWidget();
    SelectionEvent.addListener( this, listener );
  }

  /**
   * Removes the listener from the collection of listeners who will
   * be notified when the user changes the receiver's selection.
   *
   * @param listener the listener which should no longer be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see SelectionListener
   * @see SelectionEvent
   * @see #addSelectionListener
   */
  public void removeSelectionListener( SelectionListener listener ) {
    checkWidget();
    SelectionEvent.removeListener( this, listener );
  }

  /////////////////////
  // Overridden methods

  public Point computeSize( int wHint, int hHint, boolean changed ) {
    checkWidget();
    int width = 0;
    int height = 0;
    boolean hasImage = image != null;
    boolean hasText = text.length() > 0;
    if( hasImage ) {
      Rectangle imageBounds = image.getBounds ();
      width = imageBounds.width;
      height = imageBounds.height;
    }
    if( hasText ) {
      Point extent = Graphics.stringExtent( getFont(), text );
      width += extent.x;
      height = Math.max( height, extent.y );
    }
    if( height == 0 ) {
      height = 10;
    }
    // TODO [tb] : Adapt when widget is themeable:
    //    ButtonThemeAdapter themeAdapter
    //      = ( ButtonThemeAdapter )getAdapter( IThemeAdapter.class );
    if( hasText && hasImage ) {
      //int spacing = themeAdapter.getSpacing( this );
      int spacing = 2;
      width += spacing;
    }
    // Rectangle padding = themeAdapter.getPadding( this );
    Rectangle padding = new Rectangle( 5, 2, 10, 4 );
    width += padding.width;
    height += padding.height;
    if( wHint != SWT.DEFAULT ) {
      width = wHint;
    }
    if( hHint != SWT.DEFAULT ) {
      height = hHint;
    }
    // int border = getBorderWidth();
    int border = 1;
    width += border * 2;
    height += border * 2;
    return new Point( width, height );
  }

  public void setLayout( Layout layout ) {
    checkWidget();
    // prevent setting a layout
  }

  public Control[] getChildren() {
    checkWidget();
    return new Control[ 0 ];
  }

  public Object getAdapter( Class adapter ) {
    Object result;
    if( adapter == IFileUploadAdapter.class ) {
      result = fileUploadAdapter;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }

  private static int checkStyle( int style ) {
    return style & SWT.BORDER;
  }

  ////////////////
  // Inner classes

  private final class FileUploadAdapter implements IFileUploadAdapter {
  
    public void setFileName( String value ) {
      fileName = value;
    }
    
    public String getAndResetUrl() {
      String result = url;
      url = null;
      return result;
    }
  }
}
