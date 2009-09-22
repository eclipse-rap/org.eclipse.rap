/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.widgets.MenuHolder;
import org.eclipse.swt.internal.widgets.MenuHolder.IMenuHolderAdapter;


/**
 * <p>This class was introduced to be API compatible with SWT and does only 
 * provide those methods that are absolutely necessary to serve this purpose.
 * </p>
 */
public class Decorations extends Canvas {

  private Menu menuBar;
  private MenuHolder menuHolder;
  private DisposeListener menuBarDisposeListener;
  private Image image;
  private Image[] images;

  Decorations( final Composite parent ) {
    // prevent instantiation from outside this package
    super( parent );
    images = new Image[0];
  }

  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IMenuHolderAdapter.class ) {
      if( menuHolder == null ) {
        menuHolder = new MenuHolder();
      }
      result = menuHolder;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }

  /**
   * Sets the receiver's images to the argument, which may
   * be an empty array. Images are typically displayed by the
   * window manager when the instance is marked as iconified,
   * and may also be displayed somewhere in the trim when the
   * instance is in normal or maximized states. Depending where
   * the icon is displayed, the platform chooses the icon with
   * the "best" attributes. It is expected that the array will
   * contain the same icon rendered at different sizes, with
   * different depth and transparency attributes.
   * 
   * @param images the new image array
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the array of images is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if one of the images is null or has been
   *                                 disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
   *                                      created the receiver</li>
   * </ul>
   * 
   * @since 1.3
   */
  public void setImages( final Image[] images ) {
    checkWidget();
    if( images == null ) {
      error( SWT.ERROR_INVALID_ARGUMENT );
    }
    for( int i = 0; i < images.length; i++ ) {
      if( images[i] == null ) {
        error( SWT.ERROR_INVALID_ARGUMENT );
      }
    }
    this.images = images;
  }
  
  /**
   * Returns the receiver's images if they had previously been 
   * set using <code>setImages()</code>. Images are typically
   * displayed by the window manager when the instance is
   * marked as iconified, and may also be displayed somewhere
   * in the trim when the instance is in normal or maximized
   * states. Depending where the icon is displayed, the platform
   * chooses the icon with the "best" attributes.  It is expected
   * that the array will contain the same icon rendered at different
   * sizes, with different depth and transparency attributes.
   * 
   * <p>
   * Note: This method will return an empty array if called before
   * <code>setImages()</code> is called. It does not provide
   * access to a window manager provided, "default" image
   * even if one exists.
   * </p>
   * 
   * @return the images
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
   *                                      created the receiver</li>
   * </ul>
   * 
   * @since 1.3
   */
  public Image[] getImages() {
    checkWidget();
    return images;
  }
  
  /**
   * Sets the receiver's image to the argument, which may
   * be null. The image is typically displayed by the window
   * manager when the instance is marked as iconified, and
   * may also be displayed somewhere in the trim when the
   * instance is in normal or maximized states.
   *
   * @param image the new image (or null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the image has been disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setImage( final Image image ) {
    checkWidget();
    this.image = image;
  }

  /**
   * Returns the receiver's image if it had previously been
   * set using <code>setImage()</code>. The image is typically
   * displayed by the window manager when the instance is
   * marked as iconified, and may also be displayed somewhere
   * in the trim when the instance is in normal or maximized
   * states.
   * <p>
   * Note: This method will return null if called before
   * <code>setImage()</code> is called. It does not provide
   * access to a window manager provided, "default" image
   * even if one exists.
   * </p>
   *
   * @return the image
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

  //////////
  // MenuBar
  
  /**
   * Sets the receiver's menu bar to the argument, which
   * may be null.
   *
   * @param menuBar the new menu bar
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the menu has been disposed</li> 
   *    <li>ERROR_INVALID_PARENT - if the menu is not in the same widget tree</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setMenuBar( final Menu menuBar ) {
    checkWidget();
    if( this.menuBar != menuBar ) {
      if( menuBar != null ) {
        if( menuBar.isDisposed() ) {
          SWT.error( SWT.ERROR_INVALID_ARGUMENT );
        }
        if( menuBar.getParent() != this ) {
          SWT.error( SWT.ERROR_INVALID_PARENT );
        }
        if( ( menuBar.getStyle() & SWT.BAR ) == 0 ) {
          SWT.error( SWT.ERROR_MENU_NOT_BAR );
        }
      }
      removeMenuBarDisposeListener();
      this.menuBar = menuBar;
      addMenuBarDisposeListener();
    }
  }

  /**
   * Returns the receiver's menu bar if one had previously
   * been set, otherwise returns null.
   *
   * @return the menu bar or null
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Menu getMenuBar() {
    checkWidget();
    return menuBar;
  }

  ///////////
  // Disposal
  
  final void releaseWidget() {
    removeMenuBarDisposeListener();
    super.releaseWidget();
  }

  //////////////////////////////////////////////////////////
  // Helping methods to observe the disposal of the menuBar
  
  private void addMenuBarDisposeListener() {
    if( menuBar != null ) {
      if( menuBarDisposeListener == null ) {
        menuBarDisposeListener = new DisposeListener() {
          public void widgetDisposed( final DisposeEvent event ) {
            Decorations.this.menuBar = null;
          }
        };
      }
      menuBar.addDisposeListener( menuBarDisposeListener );
    }
  }

  private void removeMenuBarDisposeListener() {
    if( menuBar != null ) {
      menuBar.removeDisposeListener( menuBarDisposeListener );
    }
  }
}
