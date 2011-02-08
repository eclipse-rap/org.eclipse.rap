/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.browser;

import java.util.*;

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.rwt.lifecycle.WidgetUtil;
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
    public void setExecuteResult( final boolean result, final Object value ) {
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          Browser.this.executeResult = Boolean.valueOf( result );
          Browser.this.evaluateResult = value;
        }
      } );
    }
    public void setExecutePending( final boolean executePending ) {
      Browser.this.executePending = executePending;
    }
    public boolean getExecutePending() {
      return Browser.this.executePending;
    }
    public BrowserFunction[] getBrowserFunctions() {
      return Browser.this.getBrowserFunctions();
    }
  }

  private static final String FUNCTIONS_TO_CREATE
    = Browser.class.getName() + "#functionsToCreate.";
  private static final String FUNCTIONS_TO_DESTROY
    = Browser.class.getName() + "#functionsToDestroy.";

  private static final String ABOUT_BLANK = "about:blank";

  private String url;
  private String html;
  public String executeScript;
  private Boolean executeResult;
  private boolean executePending;
  private Object evaluateResult;
  private final IBrowserAdapter browserAdapter;
  private List functions;

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
    if( ( style & ( SWT.MOZILLA | SWT.WEBKIT ) ) != 0 ) {
      throw new SWTError( SWT.ERROR_NO_HANDLES, "Unsupported Browser type" );
    }
    html = "";
    url = "";
    browserAdapter = new BrowserAdapter();
    functions = new ArrayList();
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
      html = "";
      event = new LocationEvent( this, LocationEvent.CHANGED, url );
      event.top = true;
      event.processEvent();

      ProgressEvent progressEvent
        = new ProgressEvent( this, ProgressEvent.CHANGED );
      progressEvent.processEvent();
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
      url = "";
      event = new LocationEvent( this, LocationEvent.CHANGED, ABOUT_BLANK );
      event.top = true;
      event.processEvent();

      ProgressEvent progressEvent
        = new ProgressEvent( this, ProgressEvent.CHANGED );
      progressEvent.processEvent();
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
   * isolated from the surrounding document it may break the client-side
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
    boolean result = false;
    if( executeScript == null ) {
      executeScript = script;
      executeResult = null;
      while( executeResult == null ) {
        Display display = getDisplay();
        if( !display.readAndDispatch() )  {
          display.sleep();
        }
      }
      executeScript = null;
      executePending = false;
      result = executeResult.booleanValue();
    }
    return result;
  }

  /**
   * Returns the result, if any, of executing the specified script.
   * <p>
   * Evaluates a script containing javascript commands in the context of
   * the current document.  If document-defined functions or properties
   * are accessed by the script then this method should not be invoked
   * until the document has finished loading (<code>ProgressListener.completed()</code>
   * gives notification of this).
   * </p><p>
   * If the script returns a value with a supported type then a java
   * representation of the value is returned.  The supported
   * javascript -> java mappings are:
   * <ul>
   * <li>javascript null or undefined -> <code>null</code></li>
   * <li>javascript number -> <code>java.lang.Double</code></li>
   * <li>javascript string -> <code>java.lang.String</code></li>
   * <li>javascript boolean -> <code>java.lang.Boolean</code></li>
   * <li>javascript array whose elements are all of supported types -> <code>java.lang.Object[]</code></li>
   * </ul>
   *
   * An <code>SWTException</code> is thrown if the return value has an
   * unsupported type, or if evaluating the script causes a javascript
   * error to be thrown.
   *
   * @param script the script with javascript commands
   *
   * @return the return value, if any, of executing the script
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the script is null</li>
   * </ul>
   *
   * @exception SWTException <ul>
   *    <li>ERROR_FAILED_EVALUATE when the script evaluation causes a javascript error to be thrown</li>
   *    <li>ERROR_INVALID_RETURN_VALUE when the script returns a value of unsupported type</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   *    <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * </ul>
   *
   * @see ProgressListener#completed(ProgressEvent)
   *
   * @since 1.4
   */
  public Object evaluate( final String script ) throws SWTException {
    if( script == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    StringBuffer buffer = new StringBuffer( "(function(){" );
    buffer.append( script );
    buffer.append( "})();" );
    boolean success = execute( buffer.toString() );
    if( !success ) {
      String errorString = "Failed to evaluate javascript expression";
      throw new SWTException( SWT.ERROR_FAILED_EVALUATE, errorString );
    }
    return evaluateResult;
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
    checkWidget();
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
    checkWidget();
    LocationEvent.removeListener( this, listener );
  }

  /**
   * Adds the listener to the collection of listeners who will be
   * notified when a progress is made during the loading of the current
   * URL or when the loading of the current URL has been completed.
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
   * @since 1.4
   */
  public void addProgressListener( final ProgressListener listener ) {
    checkWidget();
    ProgressEvent.addListener( this, listener );
  }

  /**
   * Removes the listener from the collection of listeners who will
   * be notified when a progress is made during the loading of the current
   * URL or when the loading of the current URL has been completed.
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
   * @since 1.4
   */
  public void removeProgressListener( final ProgressListener listener ) {
    checkWidget();
    ProgressEvent.removeListener( this, listener );
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

  /**
   * Returns the JavaXPCOM <code>nsIWebBrowser</code> for the receiver, or <code>null</code>
   * if it is not available.  In order for an <code>nsIWebBrowser</code> to be returned all
   * of the following must be true: <ul>
   *    <li>the receiver's style must be <code>SWT.MOZILLA</code></li>
   *    <li>the classes from JavaXPCOM &gt;= 1.8.1.2 must be resolvable at runtime</li>
   *    <li>the version of the underlying XULRunner must be &gt;= 1.8.1.2</li>
   * </ul>
   *
   * @return the receiver's JavaXPCOM <code>nsIWebBrowser</code> or <code>null</code>
   *
   * @since 1.4
   */
  public Object getWebBrowser() {
    checkWidget();
    return null;
  }

  //////////////////////////////////////////
  // BrowserFunction support helping methods

  private BrowserFunction[] getBrowserFunctions() {
    BrowserFunction[] result = new BrowserFunction[ functions.size() ];
    for( int i = 0; i < functions.size(); i++ ) {
      result[ i ] = ( BrowserFunction )functions.get( i );
    }
    return result;
  }

  void createFunction( final BrowserFunction function ) {
    boolean removed = false;
    for( int i = 0; i < functions.size() && !removed; i++ ) {
      BrowserFunction current = ( BrowserFunction )functions.get( i );
      if( current.name.equals( function.name ) ) {
        functions.remove( current );
        removed = true;
      }
    }
    functions.add( function );
    if( !removed ) {
      updateBrowserFunctions( function.getName(), true );
    }
  }

  void destroyFunction( final BrowserFunction function ) {
    functions.remove( function );
    updateBrowserFunctions( function.getName(), false );
  }

  private void updateBrowserFunctions( final String function,
                                       final boolean create )
  {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    String id = WidgetUtil.getId( this );
    String key = create ? FUNCTIONS_TO_CREATE + id : FUNCTIONS_TO_DESTROY + id;
    String[] funcList = ( String[] )stateInfo.getAttribute( key );
    String[] newList;
    if( funcList == null ) {
      newList = new String[ 1 ];
      newList[ 0 ] = function;
    } else {
      newList = new String[ funcList.length + 1 ];
      System.arraycopy( funcList, 0, newList, 0, funcList.length );
      newList[ funcList.length ] = function;
    }
    stateInfo.setAttribute( key, newList );
  }

  protected void checkWidget() {
    super.checkWidget ();
  }
}
