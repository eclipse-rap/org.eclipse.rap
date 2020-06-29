/*******************************************************************************
 * Copyright (c) 2020 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.dnd;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.internal.lifecycle.SimpleLifeCycle;
import org.eclipse.rap.rwt.remote.AbstractOperationHandler;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;

/**
 * The <code>Clipboard</code> provides a mechanism for transferring data from one
 * application to another or within an application.
 *
 * <p>IMPORTANT: This class is <em>not</em> intended to be subclassed.</p>
 *
 * @noextend This class is not intended to be subclassed by clients.
 * @since 3.14
 */
public class Clipboard {

  private static final String REMOTE_TYPE = "rwt.client.Clipboard";
  private static final String PROP_TEXT = "text";
  private static final String PROP_OPERATION = "operation";
  private static final String PROP_RESULT = "result";
  private static final String PROP_ERROR_MESSAGE = "errorMessage";
  private static final String METHOD_OPERATION_SUCCEEDED = "operationSucceeded";
  private static final String METHOD_OPERATION_FAILED = "operationFailed";
  private static final String METHOD_READ_TEXT = "readText";
  private static final String METHOD_WRITE_TEXT = "writeText";

  enum ClipboardOperation {
    WRITE_TEXT,
    READ_TEXT
  }

  private Display display;
  private final RemoteObject remoteObject;
  private ClipboardListener listener;
  private boolean operationPending;
  private Object operationResult;

  /**
   * Constructs a new instance of this class.
   *
   * @param display the display on which to allocate the clipboard
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   *
   * @see Clipboard#checkSubclass
   */
  public Clipboard( Display display ) {
    checkSubclass();
    this.display = display;
    if( this.display == null ) {
      this.display = Display.getCurrent();
      if( this.display == null ) {
        this.display = Display.getDefault();
      }
    }
    if( this.display.getThread() != Thread.currentThread() ) {
      DND.error( SWT.ERROR_THREAD_INVALID_ACCESS );
    }
    remoteObject = RWT.getUISession().getConnection().createRemoteObject( REMOTE_TYPE );
    remoteObject.setHandler( new ClipboardOperationHandler() );
  }

  /**
   * Sets the listener who will be notified when clipboard operation is performed, by sending
   * it one of the messages defined in the <code>ClipboardListener</code> interface.
   *
   * @param listener the listener which should be notified
   *
   * @exception IllegalArgumentException <ul>
   *     <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see ClipboardListener
   */
  void setClipboardListener( ClipboardListener listener ) {
    checkWidget();
    if( listener == null ) {
      DND.error( SWT.ERROR_NULL_ARGUMENT );
    }
    this.listener = listener;
  }

  /**
   * Place text data in the client clipboard.
   *
   * @param text the text to placed in the clipboard
   *
   * @exception IllegalArgumentException <ul>
   *     <li>ERROR_NULL_ARGUMENT - if the text is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  void writeText( String text ) {
    checkWidget();
    if( text == null ) {
      DND.error( SWT.ERROR_NULL_ARGUMENT );
    }
    remoteObject.call( METHOD_WRITE_TEXT, new JsonObject().add( PROP_TEXT, text ) );
  }

  /**
   * Ask the client to get the text data from the clipboard.
   *
   * To get the actual data you have to add ClipboardListener before calling this method.
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see ClipboardListener
   */
  void readText() {
    checkWidget();
    remoteObject.call( METHOD_READ_TEXT, null );
  }

  /**
   * Retrieve the data of the specified type currently available on the system
   * clipboard. Refer to the specific subclass of <code>Transfer</code> to
   * determine the type of object returned. Only <code>TextTransfer</code> is currently supported.
   *
   * <p>The following snippet shows text being retrieved from the
   * clipboard:</p>
   *
   *    <code><pre>
   *    Clipboard clipboard = new Clipboard(display);
   *    TextTransfer textTransfer = TextTransfer.getInstance();
   *    String textData = (String)clipboard.getContents(textTransfer);
   *    if (textData != null) System.out.println("Text is "+textData);
   *    </code></pre>
   *
   * @param transfer the transfer agent for the type of data being requested
   * @return the data obtained from the clipboard or null if no data of this type is available
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if transfer is null</li>
   * </ul>
   *
   * @exception UnsupportedOperationException when running the application in JEE_COMPATIBILITY mode
   * @exception IllegalStateException when clipboard content is already requested.
   *
   * @see org.eclipse.rap.rwt.application.Application.OperationMode
   * @see Transfer
   */
  public Object getContents( Transfer transfer ) {
    checkOperationMode();
    checkWidget();
    if( transfer == null ) {
      DND.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( !( transfer instanceof TextTransfer ) ) {
      DND.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    if( operationPending ) {
      throw new IllegalStateException( "Another clipboard operation is already pending" );
    }
    operationPending = true;
    operationResult = null;
    readText();
    while( operationResult == null ) {
      if( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    operationPending = false;
    return operationResult instanceof SWTException ? null : operationResult;
  }

  /**
   * Place data of the specified type on the system clipboard.  Only one type
   * of data can be placed on the system clipboard at the same time.  Setting the
   * data clears any previous data from the system clipboard, regardless of type.
   * Only <code>TextTransfer</code> is currently supported.
   *
   * <p>The following snippet shows text being set on the copy/paste clipboard:
   * </p>
   *
   * <code><pre>
   *  Clipboard clipboard = new Clipboard(display);
   *  String textData = "Hello World";
   *  TextTransfer textTransfer = TextTransfer.getInstance();
   *  Transfer[] transfers = new Transfer[]{textTransfer};
   *  Object[] data = new Object[]{textData};
   *  clipboard.setContents(data, transfers);
   * </code></pre>
   *
   * @param data the data to be set in the clipboard
   * @param dataTypes the transfer agents that will convert the data to its
   * platform specific format; each entry in the data array must have a
   * corresponding dataType
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if data is null or datatypes is null
   *          or the length of data is not the same as the length of dataTypes</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *  @exception SWTError <ul>
   *    <li>ERROR_CANNOT_SET_CLIPBOARD - if the clipboard is locked or otherwise unavailable</li>
   * </ul>
   *
   * @exception UnsupportedOperationException when running the application in JEE_COMPATIBILITY mode
   * @exception IllegalStateException when clipboard content is already requested.
   *
   * @see org.eclipse.rap.rwt.application.Application.OperationMode
   */
  public void setContents( Object[] data, Transfer[] dataTypes ) {
    checkOperationMode();
    checkWidget();
    if( data == null || dataTypes == null || data.length != dataTypes.length || data.length != 1 ) {
      DND.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    for( int i = 0; i < data.length; i++ ) {
      if(    data[ i ] == null
          || dataTypes[ i ] == null
          || !( dataTypes[ i ] instanceof TextTransfer )
          || !dataTypes[ i ].validate( data[ i ] ) )
      {
        DND.error( SWT.ERROR_INVALID_ARGUMENT );
      }
    }
    if( operationPending ) {
      throw new IllegalStateException( "Another clipboard operation is already pending" );
    }
    operationPending = true;
    operationResult = null;
    writeText( ( String )data[ 0 ] );
    while( operationResult == null ) {
      if( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    operationPending = false;
    if( operationResult instanceof SWTException ) {
      DND.error( DND.ERROR_CANNOT_SET_CLIPBOARD );
    }
  }

  /**
   * Disposes of the operating system resources associated with the clipboard.
   * The data will still be available on the system clipboard after the dispose
   * method is called.
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   * </ul>
   */
  public void dispose() {
    if( isDisposed() ) {
      return;
    }
    if( display.getThread() != Thread.currentThread() ) {
      DND.error( SWT.ERROR_THREAD_INVALID_ACCESS );
    }
    display = null;
    remoteObject.destroy();
  }

  /**
   * Returns <code>true</code> if the clipboard has been disposed,
   * and <code>false</code> otherwise.
   * <p>
   * This method gets the dispose state for the clipboard.
   * When a clipboard has been disposed, it is an error to
   * invoke any other method using the clipboard.
   * </p>
   *
   * @return <code>true</code> when the widget is disposed and <code>false</code> otherwise
   */
  public boolean isDisposed() {
    return display == null;
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
    if( display == null ) {
      DND.error( SWT.ERROR_WIDGET_DISPOSED );
    }
    if( display.getThread() != Thread.currentThread() ) {
      DND.error( SWT.ERROR_THREAD_INVALID_ACCESS );
    }
    if( display.isDisposed() ) {
      DND.error( SWT.ERROR_WIDGET_DISPOSED );
    }
  }

  /**
   * Checks that this class can be subclassed.
   * <p>
   * The SWT class library is intended to be subclassed
   * only at specific, controlled points. This method enforces this
   * rule unless it is overridden.
   * </p><p>
   * <em>IMPORTANT:</em> By providing an implementation of this
   * method that allows a subclass of a class which does not
   * normally allow subclassing to be created, the implementer
   * agrees to be fully responsible for the fact that any such
   * subclass will likely fail between SWT releases and will be
   * strongly platform specific. No support is provided for
   * user-written classes which are implemented in this fashion.
   * </p><p>
   * The ability to subclass outside of the allowed SWT classes
   * is intended purely to enable those not on the SWT development
   * team to implement patches in order to get around specific
   * limitations in advance of when those limitations can be
   * addressed by the team. Subclassing should not be attempted
   * without an intimate and detailed understanding of the hierarchy.
   * </p>
   *
   * @exception SWTException <ul>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   */
  protected void checkSubclass() {
    String name = getClass().getName();
    String validName = Clipboard.class.getName();
    if( !validName.equals( name ) ) {
      DND.error( SWT.ERROR_INVALID_SUBCLASS );
    }
  }

  private static void checkOperationMode() {
    if( getApplicationContext().getLifeCycleFactory().getLifeCycle() instanceof SimpleLifeCycle ) {
      throw new UnsupportedOperationException( "Method not supported in JEE_COMPATIBILITY mode." );
    }
  }

  /**
   * This listener interface is used to inform application code that the result of clipboard
   * operation execution is available.
   *
   * @see Clipboard#readText()
   */
  private interface ClipboardListener {

    void operationSucceeded( ClipboardOperation operation, String result );

    void operationFailed( ClipboardOperation operation, String errorMessage );

  }

  private class ClipboardOperationHandler extends AbstractOperationHandler {

    @Override
    public void handleCall( String methodName, JsonObject properties ) {
      if( METHOD_OPERATION_SUCCEEDED.equals( methodName ) ) {
        final ClipboardOperation operation = getOperation( properties );
        final String result = properties.get( PROP_RESULT ).asString();
        operationResult = result;
        if( listener != null ) {
          ProcessActionRunner.add( new Runnable() {
            @Override
            public void run() {
              listener.operationSucceeded( operation, result );
            }
          } );
        }
      } else if( METHOD_OPERATION_FAILED.equals( methodName ) ) {
        final ClipboardOperation operation = getOperation( properties );
        final String errorMessage = properties.get( PROP_ERROR_MESSAGE ).asString();
        if( ClipboardOperation.WRITE_TEXT.equals( operation ) ) {
          operationResult = new SWTException( SWT.ERROR_CANNOT_SET_TEXT, errorMessage );
        } else if( ClipboardOperation.READ_TEXT.equals( operation ) ) {
          operationResult = new SWTException( SWT.ERROR_CANNOT_GET_TEXT, errorMessage );
        } else {
          operationResult = new SWTException( errorMessage );
        }
        if( listener != null ) {
          ProcessActionRunner.add( new Runnable() {
            @Override
            public void run() {
              listener.operationFailed( operation, errorMessage );
            }
          } );
        }
      }
    }

    private ClipboardOperation getOperation( JsonObject properties ) {
      String operation = properties.get( PROP_OPERATION ).asString();
      if( METHOD_WRITE_TEXT.equals( operation ) ) {
        return ClipboardOperation.WRITE_TEXT;
      } else if( METHOD_READ_TEXT.equals( operation ) ) {
        return ClipboardOperation.READ_TEXT;
      } else {
        return null;
      }
    }

  }

}
