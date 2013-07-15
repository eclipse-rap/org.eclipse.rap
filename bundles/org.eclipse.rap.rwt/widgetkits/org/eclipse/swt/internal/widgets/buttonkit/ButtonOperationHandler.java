/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.buttonkit;

import static org.eclipse.rap.rwt.internal.util.OperationHandlerUtil.createKeyEvent;
import static org.eclipse.rap.rwt.internal.util.OperationHandlerUtil.createMenuDetectEvent;
import static org.eclipse.rap.rwt.internal.util.OperationHandlerUtil.createSelectionEvent;
import static org.eclipse.rap.rwt.internal.util.OperationHandlerUtil.processMouseEvent;
import static org.eclipse.rap.rwt.internal.util.OperationHandlerUtil.processTraverseEvent;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.util.OperationHandlerUtil;
import org.eclipse.rap.rwt.remote.AbstractOperationHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;


public class ButtonOperationHandler extends AbstractOperationHandler {

  private static final String PROP_SELECTION = "selection";

  private final Button button;

  public ButtonOperationHandler( Button button ) {
    this.button = button;
  }

  @Override
  public void handleNotify( String eventName, JsonObject properties ) {
    OperationHandlerUtil.handleNotify( this, eventName, properties );
  }

  /*
   * PROTOCOL NOTIFY Selection
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   */
  public void handleNotifySelection( JsonObject properties ) {
    Event event = createSelectionEvent( SWT.Selection, properties );
    if( ( button.getStyle() & SWT.RADIO ) != 0 && !button.getSelection() ) {
      event.time = -1;
    }
    button.notifyListeners( SWT.Selection, event );
  }

  /*
   * PROTOCOL NOTIFY DefaultSelection
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   */
  public void handleNotifyDefaultSelection( JsonObject properties ) {
    Event event = createSelectionEvent( SWT.DefaultSelection, properties );
    button.notifyListeners( SWT.DefaultSelection, event );
  }

  /*
   * PROTOCOL NOTIFY FocusIn
   */
  public void handleNotifyFocusIn( JsonObject properties ) {
    button.notifyListeners( SWT.FocusIn, new Event() );
  }

  /*
   * PROTOCOL NOTIFY FocusOut
   */
  public void handleNotifyFocusOut( JsonObject properties ) {
    button.notifyListeners( SWT.FocusOut, new Event() );
  }

  /*
   * PROTOCOL NOTIFY MouseDown
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   * @param button (int) the number of the mouse button as in Event.button
   * @param x (int) the x coordinate of the pointer
   * @param y (int) the y coordinate of the pointer
   * @param time (int) the time when the event occurred
   */
  public void handleNotifyMouseDown( JsonObject properties ) {
    processMouseEvent( SWT.MouseDown, button, properties );
  }

  /*
   * PROTOCOL NOTIFY MouseDoubleClick
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   * @param button (int) the number of the mouse button as in Event.button
   * @param x (int) the x coordinate of the pointer
   * @param y (int) the y coordinate of the pointer
   * @param time (int) the time when the event occurred
   */
  public void handleNotifyMouseDoubleClick( JsonObject properties ) {
    processMouseEvent( SWT.MouseDoubleClick, button, properties );
  }

  /*
   * PROTOCOL NOTIFY MouseUp
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   * @param button (int) the number of the mouse button as in Event.button
   * @param x (int) the x coordinate of the pointer
   * @param y (int) the y coordinate of the pointer
   * @param time (int) the time when the event occurred
   */
  public void handleNotifyMouseUp( JsonObject properties ) {
    processMouseEvent( SWT.MouseUp, button, properties );
  }

  /*
   * PROTOCOL NOTIFY Traverse
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   * @param keyCode (int) the key code of the key that was typed
   * @param charCode (int) the char code of the key that was typed
   */
  public void handleNotifyTraverse( JsonObject properties ) {
    processTraverseEvent( button, properties );
  }

  /*
   * PROTOCOL NOTIFY KeyDown
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   * @param keyCode (int) the key code of the key that was typed
   * @param charCode (int) the char code of the key that was typed
   */
  public void handleNotifyKeyDown( JsonObject properties ) {
    button.notifyListeners( SWT.KeyDown, createKeyEvent( properties ) );
    button.notifyListeners( SWT.KeyUp, createKeyEvent( properties ) );
  }

  /*
   * PROTOCOL NOTIFY MenuDetect
   *
   * @param x (int) the x coordinate of the pointer
   * @param y (int) the y coordinate of the pointer
   */
  public void handleNotifyMenuDetect( JsonObject properties ) {
    button.notifyListeners( SWT.MenuDetect, createMenuDetectEvent( properties ) );
  }

  /*
   * PROTOCOL NOTIFY Help
   */
  public void handleNotifyHelp( JsonObject properties ) {
    button.notifyListeners( SWT.Help, new Event() );
  }

  @Override
  public void handleSet( JsonObject properties ) {
    handleSetSelection( properties );
  }

  /*
   * PROTOCOL SET selection
   *
   * @param selection (boolean) true if the button was selected, otherwise false
   */
  private void handleSetSelection( JsonObject properties ) {
    JsonValue selection = properties.get( PROP_SELECTION );
    if( selection != null ) {
      button.setSelection( selection.asBoolean() );
    }
  }

}
