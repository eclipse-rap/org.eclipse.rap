/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.events;

import org.eclipse.rwt.Adaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;


/**
 * Instances of this class are sent as a result of
 * text being modified.
 * 
 * <p><strong>IMPORTANT:</strong> All <code>public static</code> members of 
 * this class are <em>not</em> part of the RWT public API. They are marked 
 * public only so that they can be shared within the packages provided by RWT. 
 * They should never be accessed from application code.
 * </p>
 *
 * @see VerifyListener
 */
public final class VerifyEvent extends TypedEvent {

  private static final long serialVersionUID = 1L;

  public static final int VERIFY_TEXT = SWT.Verify;
  private static final Class LISTENER = VerifyListener.class;

  /**
   * A flag indicating whether the operation should be allowed.
   * Setting this field to <code>false</code> will cancel the operation.
   */
  public boolean doit;

  /**
   * the new text that will be inserted.
   * Setting this field will change the text that is about to
   * be inserted or deleted.
   */
  public String text;

  /**
   * the range of text being modified.
   * Setting these fields has no effect.
   */
  public int start, end;


  /**
   * Constructs a new instance of this class based on the
   * information in the given untyped event.
   *
   * @param event the untyped event containing the information
   */
  public VerifyEvent( final Event event ) {
    this( ( Control )event.widget );
  }
  
  /**
   * Constructs a new instance of this class. 
   * <p><strong>IMPORTANT:</strong> This method is <em>not</em> part of the RWT
   * public API. It is marked public only so that it can be shared
   * within the packages provided by RWT. It should never be accessed 
   * from application code.
   * </p>
   */
  public VerifyEvent( final Control source ) {
    super( source, VERIFY_TEXT );
    doit = true;
  }
  
  protected void dispatchToObserver( final Object listener ) {
    switch( getID() ) {
      case VERIFY_TEXT:
        ( ( VerifyListener )listener ).verifyText( this );
      break;
      default:
        throw new IllegalStateException( "Invalid event handler type." );
    }
  }

  protected Class getListenerType() {
    return LISTENER;
  }

  protected boolean allowProcessing() {
    return true;
  }

  public static void addListener( final Adaptable adaptable, 
                                  final VerifyListener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable, 
                                     final VerifyListener listener )
  {
    removeListener( adaptable, LISTENER, listener );
  }
  
  public static boolean hasListener( final Adaptable adaptable ) {
    return hasListener( adaptable, LISTENER );
  }
  
  public static Object[] getListeners( final Adaptable adaptable ) {
    return getListener( adaptable, LISTENER );
  }

  /**
   * Returns a string containing a concise, human-readable
   * description of the receiver.
   *
   * @return a string representation of the event
   */
  public String toString() {
    String string = super.toString();
    return string.substring( 0, string.length() - 1 ) // remove trailing '}'
      // differs from SWT: no doit in superclass, thus add explicitly
      + " doit=" + doit 
      + " start=" + start
      + " end=" + end
      + " text=" + text
      + "}";
  }
}
