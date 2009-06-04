/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.graphics;

import org.eclipse.rwt.internal.theme.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.internal.graphics.ResourceFactory;

/**
 * This class is the abstract superclass of all device objects,
 * such as Display.
 *
 * <p>This class is <em>not</em> intended to be directly used by clients.</p>
 */
public abstract class Device {

  private boolean disposed;
  
  /**
   * Returns the matching standard color for the given
   * constant, which should be one of the color constants
   * specified in class <code>SWT</code>. Any value other
   * than one of the SWT color constants which is passed
   * in will result in the color black. This color should
   * not be free'd because it was allocated by the system,
   * not the application.
   *
   * @param id the color constant
   * @return the matching color
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   *
   * @see SWT
   */
  public Color getSystemColor( final int id ) {
    checkDevice();
    Color result;
    switch( id ) {
      case SWT.COLOR_WHITE:
        result = ResourceFactory.getColor( 255, 255, 255 );
      break;
      case SWT.COLOR_BLACK:
        result = ResourceFactory.getColor( 0, 0, 0 );
      break;
      case SWT.COLOR_RED:
        result = ResourceFactory.getColor( 255, 0, 0 );
      break;
      case SWT.COLOR_DARK_RED:
        result = ResourceFactory.getColor( 128, 0, 0 );
      break;
      case SWT.COLOR_GREEN:
        result = ResourceFactory.getColor( 0, 255, 0 );
      break;
      case SWT.COLOR_DARK_GREEN:
        result = ResourceFactory.getColor( 0, 128, 0 );
      break;
      case SWT.COLOR_YELLOW:
        result = ResourceFactory.getColor( 255, 255, 0 );
      break;
      case SWT.COLOR_DARK_YELLOW:
        result = ResourceFactory.getColor( 128, 128, 0 );
      break;
      case SWT.COLOR_BLUE:
        result = ResourceFactory.getColor( 0, 0, 255 );
      break;
      case SWT.COLOR_DARK_BLUE:
        result = ResourceFactory.getColor( 0, 0, 128 );
      break;
      case SWT.COLOR_MAGENTA:
        result = ResourceFactory.getColor( 255, 0, 255 );
      break;
      case SWT.COLOR_DARK_MAGENTA:
        result = ResourceFactory.getColor( 128, 0, 128 );
      break;
      case SWT.COLOR_CYAN:
        result = ResourceFactory.getColor( 0, 255, 255 );
      break;
      case SWT.COLOR_DARK_CYAN:
        result = ResourceFactory.getColor( 0, 128, 128 );
      break;
      case SWT.COLOR_GRAY:
        result = ResourceFactory.getColor( 192, 192, 192 );
      break;
      case SWT.COLOR_DARK_GRAY:
        result = ResourceFactory.getColor( 128, 128, 128 );
      break;
      default:
        result = ResourceFactory.getColor( 0, 0, 0 );
      break;
    }
    return result;
  }

  /**
   * Returns a reasonable font for applications to use.
   * On some platforms, this will match the "default font"
   * or "system font" if such can be found.  This font
   * should not be free'd because it was allocated by the
   * system, not the application.
   * <p>
   * Typically, applications which want the default look
   * should simply not set the font on the widgets they
   * create. Widgets are always created with the correct
   * default font for the class of user-interface component
   * they represent.
   * </p>
   *
   * @return a font
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public Font getSystemFont() {
    checkDevice();
    QxType font = ThemeUtil.getCssValue( "*", "font", SimpleSelector.DEFAULT );
    return QxFont.createFont( ( QxFont )font );
  }

  /**
   * Returns a rectangle which describes the area of the receiver which is
   * capable of displaying data.
   * 
   * @return the client area
   * @exception SWTException <ul>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   * @see #getBounds
   * @since 1.2
   */
  public Rectangle getClientArea() {
    checkDevice();
    return getBounds();
  }
  
  /**
   * Returns a rectangle describing the receiver's size and location.
   * 
   * @return the bounding rectangle
   * @exception SWTException <ul>
   *   <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   * @since 1.2
   */
  public Rectangle getBounds() {
    checkDevice();
    return new Rectangle( 0, 0, 0, 0 );
  }

  /**
   * Disposes of the operating system resources associated with
   * the receiver. After this method has been invoked, the receiver
   * will answer <code>true</code> when sent the message
   * <code>isDisposed()</code>.
   *
   * @see #release
   * @see #destroy
   * @see #checkDevice
   */
  public void dispose() {
    if( !isDisposed() ) {
      release();
      destroy();
      disposed = true;
    }
  }

  /**
   * Returns <code>true</code> if the device has been disposed,
   * and <code>false</code> otherwise.
   * <p>
   * This method gets the dispose state for the device.
   * When a device has been disposed, it is an error to
   * invoke any other method using the device.
   *
   * @return <code>true</code> when the device is disposed and <code>false</code> otherwise
   */
  public boolean isDisposed() {
    return disposed;
  }

  /**
   * Releases any internal resources <!-- back to the operating
   * system and clears all fields except the device handle -->.
   * <p>
   * When a device is destroyed, resources that were acquired
   * on behalf of the programmer need to be returned to the
   * operating system.  For example, if the device allocated a
   * font to be used as the system font, this font would be
   * freed in <code>release</code>.  Also,to assist the garbage
   * collector and minimize the amount of memory that is not
   * reclaimed when the programmer keeps a reference to a
   * disposed device, all fields except the handle are zero'd.
   * The handle is needed by <code>destroy</code>.
   * </p>
   * This method is called before <code>destroy</code>.
   * </p><p>
   * If subclasses reimplement this method, they must
   * call the <code>super</code> implementation.
   * </p>
   *
   * @see #dispose
   * @see #destroy
   */
  protected void release() {
  }

  /**
   * Destroys the device <!-- in the operating system and releases
   * the device's handle -->.  If the device does not have a handle,
   * this method may do nothing depending on the device.
   * <p>
   * This method is called after <code>release</code>.
   * </p><p>
   * Subclasses are supposed to reimplement this method and not
   * call the <code>super</code> implementation.
   * </p>
   *
   * @see #dispose
   * @see #release
   */
  protected void destroy() {
  }

  /**
   * Throws an <code>SWTException</code> if the receiver can not
   * be accessed by the caller. This may include both checks on
   * the state of the receiver and more generally on the entire
   * execution context. This method <em>should</em> be called by
   * device implementors to enforce the standard SWT invariants.
   * <p>
   * Currently, it is an error to invoke any method (other than
   * <code>isDisposed()</code> and <code>dispose()</code>) on a
   * device that has had its <code>dispose()</code> method called.
   * </p><p>
   * In future releases of SWT, there may be more or fewer error
   * checks and exceptions may be thrown for different reasons.
   * <p>
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  protected void checkDevice() {
    if( disposed ) {
      SWT.error( SWT.ERROR_DEVICE_DISPOSED );
    }
  }

}
