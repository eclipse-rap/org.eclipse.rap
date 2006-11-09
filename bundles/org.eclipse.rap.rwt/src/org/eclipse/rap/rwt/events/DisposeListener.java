package org.eclipse.rap.rwt.events;

import java.util.EventListener;

/**
 * <p>Classes which implement this interface provide a method
 * that deals with the event that is generated when a widget
 * is disposed.
 * </p>
 * <p>After creating an instance of a class that implements
 * this interface it can be added to a widget using the
 * <code>addDisposeListener</code> method and removed using
 * the <code>removeDisposeListener</code> method. When a
 * widget is disposed, the widgetDisposed method will
 * be invoked.
 * </p>
 * @see DisposeEvent
 */
public interface DisposeListener extends EventListener {

  /**
   * <p>Sent when the widget is disposed.</p>
   * @param e an event containing information about the dispose
   */
  public void widgetDisposed( DisposeEvent event );
}
