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

package org.eclipse.swt.widgets;

import org.eclipse.rwt.Adaptable;
import org.eclipse.rwt.internal.AdapterManager;
import org.eclipse.rwt.internal.AdapterManagerImpl;
import org.eclipse.rwt.internal.events.EventAdapter;
import org.eclipse.rwt.internal.events.IEventAdapter;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.internal.widgets.UntypedEventAdapter;
import org.eclipse.swt.internal.widgets.WidgetAdapter;


/**
 * This class is the abstract superclass of all user interface objects.  
 * Widgets are created, disposed and issue notification to listeners
 * when events occur which affect them.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Dispose</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is intended to be subclassed <em>only</em>
 * within the SWT implementation. However, it has not been marked
 * final to allow those outside of the SWT development team to implement
 * patched versions of the class in order to get around specific
 * limitations in advance of when those limitations can be addressed
 * by the team.  Any class built using subclassing to access the internals
 * of this class will likely fail to compile or run between releases and
 * may be strongly platform specific. Subclassing should not be attempted
 * without an intimate and detailed understanding of the workings of the
 * hierarchy. No support is provided for user-written classes which are
 * implemented as subclasses of this class.
 * </p>
 * <p>Even though this class implements <code>Adaptable</code> this interface
 * is <em>not</em> part of the RWT public API. It is only meant to be shared 
 * within the packages provided by RWT and should never be accessed from 
 * application code.
 * </p>
 *
 * @see #checkSubclass
 */
public abstract class Widget implements Adaptable {

  /* Default size for widgets */
  static final int DEFAULT_WIDTH = 64;
  static final int DEFAULT_HEIGHT = 64;
  
  /* Global state flags */
  static final int DISPOSED = 1 << 0;
//  static final int CANVAS = 1 << 1;
  static final int KEYED_DATA = 1 << 2;
  static final int DISABLED = 1 << 3;
  static final int HIDDEN = 1 << 4;
  
  /* A layout was requested on this widget */
  static final int LAYOUT_NEEDED  = 1<<5;
  
  /* The preferred size of a child has changed */
  static final int LAYOUT_CHANGED = 1<<6;
  
  /* A layout was requested in this widget hierarchy */
  static final int LAYOUT_CHILD = 1<<7;

  int style;
  int state;
  Display display;
  private Object data;
  private AdapterManager adapterManager;
  private WidgetAdapter widgetAdapter;
  private IEventAdapter eventAdapter;
  private UntypedEventAdapter untypedAdapter; 
  
  
  Widget() {
    // prevent instantiation from outside this package
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
   * </p>
   *
   * @param parent a widget which will be the parent of the new instance (cannot be null)
   * @param style the style of widget to construct
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the parent is disposed</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   *
   * @see SWT
   * @see #checkSubclass
   * @see #getStyle
   */
  public Widget( final Widget parent, final int style ) {
    checkSubclass();
    if( parent == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    this.style = style;
    this.display = parent.display;
  }

  /**
   * Implementation of the <code>Adaptable</code> interface. 
   * <p><strong>IMPORTANT:</strong> This method is <em>not</em> part of the RWT
   * public API. It is marked public only so that it can be shared
   * within the packages provided by RWT. It should never be accessed 
   * from application code.
   * </p>
   */
  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IEventAdapter.class ) {
      // //////////////////////////////////////////////////////
      // Note: This is not implemented via the AdapterManager,
      // since the manager's mapping mechanism prevents
      // the component being released unless the session
      // is invalidated.
      if( eventAdapter == null ) {
        eventAdapter = new EventAdapter();
      }
      result = eventAdapter;
    } else if( adapter == IWidgetAdapter.class ) {
      // TODO: [fappel] this is done for performance improvement and replaces
      //                the lookup in WidgetAdapterFactory. Since this is still
      //                a matter of investigation, WidgetAdapterFactory is not 
      //                changed yet.
      if( widgetAdapter == null ) {
        widgetAdapter = new WidgetAdapter();
      }
      result = widgetAdapter;
    } else {
      // TODO: [fappel] buffer the adapterManager for performance improvement.
      //                Note: this is still a matter of investigation since
      //                we improve cpu time on cost of memory consumption.
      if( adapterManager == null ) {
        adapterManager = AdapterManagerImpl.getInstance();
      }
      result = adapterManager.getAdapter( this, adapter );
    }
    return result;
  }
  
  ///////////////////////////////////////////
  // Methods to get/set single and keyed data
  
  /**
   * Returns the application defined widget data associated
   * with the receiver, or null if it has not been set. The
   * <em>widget data</em> is a single, unnamed field that is
   * stored with every widget. 
   * <p>
   * Applications may put arbitrary objects in this field. If
   * the object stored in the widget data needs to be notified
   * when the widget is disposed of, it is the application's
   * responsibility to hook the Dispose event on the widget and
   * do so.
   * </p>
   *
   * @return the widget data
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - when the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - when called from the wrong thread</li>
   * </ul>
   *
   * @see #setData(Object)
   */
  public Object getData() {
    checkWidget();
    return ( state & KEYED_DATA ) != 0 ? ( ( Object[] )data )[ 0 ] : data;
  }
  
  /**
   * Sets the application defined widget data associated
   * with the receiver to be the argument. The <em>widget
   * data</em> is a single, unnamed field that is stored
   * with every widget. 
   * <p>
   * Applications may put arbitrary objects in this field. If
   * the object stored in the widget data needs to be notified
   * when the widget is disposed of, it is the application's
   * responsibility to hook the Dispose event on the widget and
   * do so.
   * </p>
   *
   * @param data the widget data
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - when the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - when called from the wrong thread</li>
   * </ul>
   * 
   * @see #getData()
   */
  public void setData( final Object data ) {
    checkWidget();
    if( ( state & KEYED_DATA ) != 0 ) {
      ( ( Object[] )this.data )[ 0 ] = data;
    } else {
      this.data = data;
    }
  }

  /**
   * Returns the application defined property of the receiver
   * with the specified name, or null if it has not been set.
   * <p>
   * Applications may have associated arbitrary objects with the
   * receiver in this fashion. If the objects stored in the
   * properties need to be notified when the widget is disposed
   * of, it is the application's responsibility to hook the
   * Dispose event on the widget and do so.
   * </p>
   *
   * @param	key the name of the property
   * @return the value of the property or null if it has not been set
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the key is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see #setData(String, Object)
   */
  public Object getData( final String key ) {
    checkWidget();
    if( key == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    Object result = null;
    if( ( state & KEYED_DATA ) != 0 ) {
      Object[] table = ( Object[] )data;
      for( int i = 1; result == null && i < table.length; i += 2 ) {
        if( key.equals( table[ i ] ) ) {
          result = table[ i + 1 ];
        }
      }
    }
    return result;
  }
  
  /**
   * Sets the application defined property of the receiver
   * with the specified name to the given value.
   * <p>
   * Applications may associate arbitrary objects with the
   * receiver in this fashion. If the objects stored in the
   * properties need to be notified when the widget is disposed
   * of, it is the application's responsibility to hook the
   * Dispose event on the widget and do so.
   * </p>
   *
   * @param key the name of the property
   * @param value the new value for the property
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the key is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see #getData(String)
   */
  public void setData( final String key, final Object value ) {
    checkWidget();
    if( key == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    int index = 1;
    Object[] table = null;
    if( ( state & KEYED_DATA ) != 0 ) {
      table = ( Object[] )data;
      while( index < table.length ) {
        if( key.equals( table[ index ] ) ) {
          break;
        }
        index += 2;
      }
    }
    if( value != null ) {
      if( ( state & KEYED_DATA ) != 0 ) {
        if( index == table.length ) {
          Object[] newTable = new Object[ table.length + 2 ];
          System.arraycopy( table, 0, newTable, 0, table.length );
          data = table = newTable;
        }
      } else {
        table = new Object[ 3 ];
        table[ 0 ] = data;
        data = table;
        state |= KEYED_DATA;
      }
      table[ index ] = key;
      table[ index + 1 ] = value;
    } else {
      if( ( state & KEYED_DATA ) != 0 ) {
        if( index != table.length ) {
          int length = table.length - 2;
          if( length == 1 ) {
            data = table[ 0 ];
            state &= ~KEYED_DATA;
          } else {
            Object[] newTable = new Object[ length ];
            System.arraycopy( table, 0, newTable, 0, index );
            System.arraycopy( table, index + 2, newTable, index, length - index );
            data = newTable;
          }
        }
      }
    }
  }
  
  /**
   * Returns the <code>Display</code> that is associated with
   * the receiver.
   * <p>
   * A widget's display is either provided when it is created
   * (for example, top level <code>Shell</code>s) or is the
   * same as its parent's display.
   * </p>
   *
   * @return the receiver's display
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public Display getDisplay() {
    // do not check control for UI Thread, see bug #193389
    Display display = this.display;
//    if( display == null )
//      error( SWT.ERROR_WIDGET_DISPOSED );
    return display;
  }

  /**
   * Returns the receiver's style information.
   * <p>
   * Note that the value which is returned by this method <em>may
   * not match</em> the value which was provided to the constructor
   * when the receiver was created. This can occur when the underlying
   * operating system does not support a particular combination of
   * requested styles. For example, if the platform widget used to
   * implement a particular SWT widget always has scroll bars, the
   * result of calling this method would always have the
   * <code>SWT.H_SCROLL</code> and <code>SWT.V_SCROLL</code> bits set.
   * </p>
   *
   * @return the style bits
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int getStyle() {
    checkWidget();
    return style;
  }

  
  ///////////////////////////////////////////////
  // Registration and deregistration of listeners
  
  /**
   * Adds the listener to the collection of listeners who will
   * be notified when the widget is disposed. When the widget is
   * disposed, the listener is notified by sending it the
   * <code>widgetDisposed()</code> message.
   *
   * @param listener the listener which should be notified when the receiver is disposed
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see DisposeListener
   * @see #removeDisposeListener
   */
  public void addDisposeListener( final DisposeListener listener ) {
    checkWidget();
    DisposeEvent.addListener( this, listener );
  }

  /**
   * Removes the listener from the collection of listeners who will
   * be notified when the widget is disposed.
   *
   * @param listener the listener which should no longer be notified when the receiver is disposed
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see DisposeListener
   * @see #addDisposeListener
   */
  public void removeDisposeListener( final DisposeListener listener ) {
    checkWidget();
    DisposeEvent.removeListener( this, listener );
  }
  
  ////////////////////////////////////////
  // Methods for untyped listener handling
  
  /**
   * Adds the listener to the collection of listeners who will be notified when
   * an event of the given type occurs. When the event does occur in the widget,
   * the listener is notified by sending it the <code>handleEvent()</code>
   * message. The event type is one of the event constants defined in class
   * <code>SWT</code>.
   * 
   * @param eventType the type of event to listen for
   * @param listener the listener which should be notified when the event occurs
   * @exception IllegalArgumentException
   *              <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   *              </ul>
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @see Listener
   * @see SWT
   * @see #removeListener
   * <!--@see #notifyListeners-->
   */
  public void addListener( final int eventType, final Listener listener ) {
    checkWidget();
    if( listener == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( untypedAdapter == null ) {
      untypedAdapter = new UntypedEventAdapter();
    }
    untypedAdapter.addListener( this, eventType, listener );
  }
  
  /**
   * Removes the listener from the collection of listeners who will be notified
   * when an event of the given type occurs. The event type is one of the event
   * constants defined in class <code>SWT</code>.
   * 
   * @param eventType the type of event to listen for
   * @param listener the listener which should no longer be notified when the
   *          event occurs
   * @exception IllegalArgumentException
   *              <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   *              </ul>
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   * @see Listener
   * @see SWT
   * @see #addListener
   * <!--@see #notifyListeners-->
   */
  public void removeListener( final int eventType, final Listener listener ) {
    checkWidget();
    if( listener == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    untypedAdapter.removeListener( this, eventType, listener );
    if( untypedAdapter.isEmpty() ) {
      untypedAdapter = null;
    }
  }

  ///////////////////////
  // toString and helpers
  
  /**
   * Returns a string containing a concise, human-readable
   * description of the receiver.
   *
   * @return a string representation of the receiver
   */
  public String toString() {
    String string = "*Disposed*";
    if( !isDisposed() ) {
      string = "*Wrong Thread*";
      if( isValidThread() ) {
        string = getNameText();
      }
    }
    return getName() + " {" + string + "}";
  }

  /**
   * Returns the name of the widget. This is the name of
   * the class without the package name.
   *
   * @return the name of the widget
   */
  String getName() {
    String string = getClass().getName();
    int index = string.lastIndexOf( '.' );
    if( index != -1 ) {
      string = string.substring( index + 1, string.length() );
    } 
    return string;
  }

  /*
   * Returns a short printable representation for the contents
   * of a widget. For example, a button may answer the label
   * text. This is used by <code>toString</code> to provide a
   * more meaningful description of the widget.
   *
   * @return the contents string for the widget
   *
   * @see #toString
   */
  String getNameText() {
    return "";
  }
  
  ///////////////////////////////////
  // Methods to dispose of the widget
  
  /**
   * Disposes of the operating system resources associated with
   * the receiver and all its descendents. After this method has
   * been invoked, the receiver and all descendents will answer
   * <code>true</code> when sent the message <code>isDisposed()</code>.
   * Any internal connections between the widgets in the tree will
   * have been removed to facilitate garbage collection.
   * <p>
   * NOTE: This method is not called recursively on the descendents
   * of the receiver. This means that, widget implementers can not
   * detect when a widget is being disposed of by re-implementing
   * this method, but should instead listen for the <code>Dispose</code>
   * event.
   * </p>
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see #addDisposeListener
   * @see #removeDisposeListener
   * @see #checkWidget
   */
  // TODO [rh] ensure that this implementation aligns with SWT rules for
  //      disposing (see The Standard Widget Toolkit, p 13)
  public void dispose() {
    if( !isDisposed() ) {
      if( !isValidThread() ) {
        error( SWT.ERROR_THREAD_INVALID_ACCESS );
      }
      DisposeEvent disposeEvent = new DisposeEvent( this );
      disposeEvent.processEvent();
      releaseChildren();
      releaseParent();
      releaseWidget();
      adapterManager = null;
			// FIXME [rh] quick fix to get UITestUtil_Test#testGetIdAfterDispose
      //       running. 
//      data = null;
      state |= DISPOSED;
    }
  }

  /**
   * Returns <code>true</code> if the widget has been disposed,
   * and <code>false</code> otherwise.
   * <p>
   * This method gets the dispose state for the widget.
   * When a widget has been disposed, it is an error to
   * invoke any other method using the widget.
   * </p>
   *
   * @return <code>true</code> when the widget is disposed and <code>false</code> otherwise
   */
  public boolean isDisposed() {
    return ( state & DISPOSED ) != 0;
  }
  
  void releaseChildren() {
    // do nothing - derived classes may override
  }

  void releaseParent() {
    // do nothing - derived classes may override
  }

  void releaseWidget() {
    // do nothing - derived classes may override
  }

//  /**
//   * Checks that this class can be subclassed.
//   * <p>
//   * The SWT class library is intended to be subclassed 
//   * only at specific, controlled points (most notably, 
//   * <code>Composite</code> and <code>Canvas</code> when
//   * implementing new widgets). This method enforces this
//   * rule unless it is overridden.
//   * </p><p>
//   * <em>IMPORTANT:</em> By providing an implementation of this
//   * method that allows a subclass of a class which does not 
//   * normally allow subclassing to be created, the implementer
//   * agrees to be fully responsible for the fact that any such
//   * subclass will likely fail between SWT releases and will be
//   * strongly platform specific. No support is provided for
//   * user-written classes which are implemented in this fashion.
//   * </p><p>
//   * The ability to subclass outside of the allowed SWT classes
//   * is intended purely to enable those not on the SWT development
//   * team to implement patches in order to get around specific
//   * limitations in advance of when those limitations can be
//   * addressed by the team. Subclassing should not be attempted
//   * without an intimate and detailed understanding of the hierarchy.
//   * </p>
//   *
//   * @exception SWTException <ul>
//   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
//   * </ul>
//   */
  protected void checkSubclass() {
    // TODO [rh] IMPLEMENTATION MISSING (see Display#isValidClass)
    if( !isValidSubclass() ) {
      error( SWT.ERROR_INVALID_SUBCLASS );
    }
  }

  /*
   * Returns <code>true</code> when subclassing is
   * allowed and <code>false</code> otherwise
   *
   * @return <code>true</code> when subclassing is allowed and <code>false</code> otherwise
   */
  boolean isValidSubclass() {
    return Display.isValidClass( getClass() );
  }

  /**
   * Throws an <code>SWTException</code> if the receiver can not
   * be accessed by the caller. This may include both checks on
   * the state of the receiver and more generally on the entire
   * execution context. This method <em>should</em> be called by
   * widget implementors to enforce the standard SWT invariants.
   * <p>
   * Currently, it is an error to invoke any method (other than
   * <code>isDisposed()</code>) on a widget that has had its 
   * <code>dispose()</code> method called. It is also an error
   * to call widget methods from any thread that is different
   * from the thread that created the widget.
   * </p><p>
   * In future releases of SWT, there may be more or fewer error
   * checks and exceptions may be thrown for different reasons.
   * </p>
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  protected void checkWidget() {
    if( !isValidThread() ) {
      error( SWT.ERROR_THREAD_INVALID_ACCESS );
    }
//    TODO [fappel]: implementation
//    if( isDisposed() ) {
//      error( SWT.ERROR_WIDGET_DISPOSED );
//    }
//    if ((state & DISPOSED) != 0) error (SWT.ERROR_WIDGET_DISPOSED);
  }
  
  /*
   * Returns <code>true</code> when the current thread is
   * the thread that created the widget and <code>false</code>
   * otherwise.
   *
   * @return <code>true</code> when the current thread is the thread that 
   * created the widget and <code>false</code> otherwise
   */
  boolean isValidThread() {
    return getDisplay().getThread() == Thread.currentThread();
//    return RWTLifeCycle.getThread() == Thread.currentThread();
  }

  static int checkBits( final int style,
                        final int int0,
                        final int int1,
                        final int int2,
                        final int int3,
                        final int int4,
                        final int int5 )
  {
    int mask = int0 | int1 | int2 | int3 | int4 | int5;
    int result = style;
    if( ( result & mask ) == 0 ) {
      result |= int0;
    }
    if( ( result & int0 ) != 0 ) {
      result = ( result & ~mask ) | int0;
    }
    if( ( result & int1 ) != 0 ) {
      result = ( result & ~mask ) | int1;
    }
    if( ( result & int2 ) != 0 ) {
      result = ( result & ~mask ) | int2;
    }
    if( ( result & int3 ) != 0 ) {
      result = ( result & ~mask ) | int3;
    }
    if( ( result & int4 ) != 0 ) {
      result = ( result & ~mask ) | int4;
    }
    if( ( result & int5 ) != 0 ) {
      result = ( result & ~mask ) | int5;
    }
    return result;
  }

  void error( final int code ) {
    SWT.error( code );
  }
}
