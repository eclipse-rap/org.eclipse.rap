/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.browser;

import org.eclipse.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.swt.*;
import org.eclipse.swt.internal.widgets.IBrowserAdapter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;


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
    public String getExecuteScript() {
      return Browser.this.executeScript;
    }
    public void setExecuteResult( final boolean result ) {
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          Browser.this.executeResult = Boolean.valueOf( result );
        }
      } );
    }
  }
  
  private static final String ABOUT_BLANK = "about:blank";
  
  private String url;
  private String html;
  public String executeScript;
  private Boolean executeResult;
  private final IBrowserAdapter browserAdapter;

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
   * @param parent a widget which will be the parent of the new instance (cannot be null)
   * @param style the style of widget to construct
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   * </ul>
   * @exception SWTError <ul>
   *    <li>ERROR_NO_HANDLES if a handle could not be obtained for browser creation</li>
   * </ul>
   * 
   * @see org.eclipse.swt.Widget#getStyle
   */
  public Browser( final Composite parent, final int style ) {
    super( parent, style );
    browserAdapter = new BrowserAdapter();
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
   * Execute the specified script.
   *
   * <p>Execute a script containing javascript commands in the context of the 
   * current document.</p> 
   * 
   * <!-- Begin RAP specific -->
   * <p><strong>Note:</strong> Care should be taken when using this method. 
   * The given <code>script</code> is executed in an <code>IFRAME</code> 
   * inside the document that represents the client-side application.
   * Since the execution context of an <code>IFRAME</code> is not fully 
   * isolated from the surrounding documument it may break the client-side
   * application.</p>
   * <!-- End RAP specific -->
   * 
   * @param script the script with javascript commands
   *  
   * @return <code>true</code> if the operation was successful and 
   * <code>false</code> otherwise
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the script is null</li>
   * </ul>
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   *    <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * </ul>
   *
   * @since 1.1
   */
  public boolean execute( final String script ) {
    checkWidget();
    if( script == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    executeScript = script;
    executeResult = null;
    while( executeResult == null ) {
      Display display = getDisplay();
      if( !display.readAndDispatch() )  {
        display.sleep();
      }
    }
    executeScript = null;
    return executeResult.booleanValue();
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
