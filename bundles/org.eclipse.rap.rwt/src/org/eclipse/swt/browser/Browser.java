/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.internal.widgets.IBrowserAdapter;
import org.eclipse.swt.widgets.Composite;


/**
 * Instances of this class implement the browser user interface
 * metaphor.  It allows the user to visualize and navigate through
 * HTML documents.
 * <p>
 * Note that although this class is a subclass of <code>Composite</code>,
 * it does not make sense to set a layout on it.
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * 
 * @since 1.0
 *
 * <hr/>
 * <p>Currently implemented</p>
 * <ul><li>text and url property</li></ul>
 * <p>The enabled property in not (yet) evaluated.</p>
 * <p>Focus events are not yet implemented</p>
 * 
 */
// TODO [rh] implement refresh method
// TODO [rh] bring focus events to work
public class Browser extends Composite {

  private final class BrowserAdapter implements IBrowserAdapter {
    public String getText() {
      return Browser.this.html;
    }
  }
  
  private static final String ABOUT_BLANK = "about:blank";
  
  private String url;
  private String html;

  private final IBrowserAdapter browserAdapter = new BrowserAdapter();

  /**
   * <p>The <code>style</code> flag is not yet evaluated.</p>
   * 
   * TODO: JavaDoc
   */
  public Browser( final Composite composite, final int style ) {
    super( composite, style );
  }
  
  /**
   * Loads a URL.
   * 
   * @param url the URL to be loaded
   *
   * @return true if the operation was successful and false otherwise.
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the url is null</li>
   * </ul>
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   *    <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * </ul>
   *  
   * @see #getUrl
   * 
   * @since 1.0
   */
  public boolean setUrl( final String url ) {
    checkWidget();
    if( url == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    LocationEvent event;
    event = new LocationEvent( this, LocationEvent.CHANGING, url );
    event.processEvent();
    boolean result = event.doit;
    if( result ) {
      this.url = url;
      html = null;
      event = new LocationEvent( this, LocationEvent.CHANGED, url );
      event.processEvent();
    }
    return result;
  }
  
  /**
   * Returns the current URL.
   *
   * @return the current URL or an empty <code>String</code> if there is no current URL
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   *    <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * </ul>
   *
   * @see #setUrl
   * 
   * @since 1.0
   */
  public String getUrl() {
    checkWidget();
    return url;
  }

  /**
   * Renders HTML.
   * 
   * <p>
   * The html parameter is Unicode encoded since it is a java <code>String</code>.
   * As a result, the HTML meta tag charset should not be set. The charset is implied
   * by the <code>String</code> itself.
   * 
   * @param html the HTML content to be rendered
   *
   * @return true if the operation was successful and false otherwise.
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the html is null</li>
   * </ul>
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   *    <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * </ul>
   *  
   * @see #setUrl
   * 
   * @since 1.0
   */
  public boolean setText( final String html ) {
    checkWidget();
    if( html == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    LocationEvent event;
    event = new LocationEvent( this, LocationEvent.CHANGING, ABOUT_BLANK );
    event.processEvent();
    boolean result = event.doit;
    if( result ) {
      this.html = html;
      url = null;
      event = new LocationEvent( this, LocationEvent.CHANGED, ABOUT_BLANK );
      event.processEvent();
    }
    return result;
  }

  /**	 
   * Adds the listener to the collection of listeners who will be
   * notified when the current location has changed or is about to change.
   * <p>
   * This notification typically occurs when the application navigates
   * to a new location with {@link #setUrl(String)} or when the user
   * activates a hyperlink.
   * </p>
   *
   * @param listener the listener which should be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   *    <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * </ul>
   *
   * @since 1.0
   */
  public void addLocationListener( final LocationListener listener ) {
    LocationEvent.addListener( this, listener );
  }
  
  /**	 
   * Removes the listener from the collection of listeners who will
   * be notified when the current location is changed or about to be changed.
   *
   * @param listener the listener which should no longer be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   *    <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void removeLocationListener( final LocationListener listener ) {
    LocationEvent.removeListener( this, listener );
  }
  
  public Object getAdapter( final Class adapter ) {
    Object result;
    if( IBrowserAdapter.class.equals( adapter ) ) {
      result = browserAdapter;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }
}
