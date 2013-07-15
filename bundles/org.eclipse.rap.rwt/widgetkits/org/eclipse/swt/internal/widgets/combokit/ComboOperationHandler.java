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
package org.eclipse.swt.internal.widgets.combokit;

import static org.eclipse.rap.rwt.internal.util.OperationHandlerUtil.createKeyEvent;
import static org.eclipse.rap.rwt.internal.util.OperationHandlerUtil.createMenuDetectEvent;
import static org.eclipse.rap.rwt.internal.util.OperationHandlerUtil.createSelectionEvent;
import static org.eclipse.rap.rwt.internal.util.OperationHandlerUtil.processMouseEvent;
import static org.eclipse.rap.rwt.internal.util.OperationHandlerUtil.processTraverseEvent;
import static org.eclipse.swt.internal.events.EventLCAUtil.isListening;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.util.OperationHandlerUtil;
import org.eclipse.rap.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.remote.AbstractOperationHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;


public class ComboOperationHandler extends AbstractOperationHandler {

  private static final String PROP_SELECTION_INDEX = "selectionIndex";
  private static final String PROP_LIST_VISIBLE = "listVisible";
  private static final String PROP_TEXT = "text";
  private static final String PROP_SELECTION_START = "selectionStart";
  private static final String PROP_SELECTION_LENGTH = "selectionLength";

  private final Combo combo;

  public ComboOperationHandler( Combo combo ) {
    this.combo = combo;
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
    combo.notifyListeners( SWT.Selection, event );
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
    combo.notifyListeners( SWT.DefaultSelection, event );
  }

  /*
   * PROTOCOL NOTIFY Modify
   * ignored, Modify event is fired when set text
   */
  public void handleNotifyModify( JsonObject properties ) {
  }

  /*
   * PROTOCOL NOTIFY FocusIn
   */
  public void handleNotifyFocusIn( JsonObject properties ) {
    combo.notifyListeners( SWT.FocusIn, new Event() );
  }

  /*
   * PROTOCOL NOTIFY FocusOut
   */
  public void handleNotifyFocusOut( JsonObject properties ) {
    combo.notifyListeners( SWT.FocusOut, new Event() );
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
    processMouseEvent( SWT.MouseDown, combo, properties );
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
    processMouseEvent( SWT.MouseDoubleClick, combo, properties );
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
    processMouseEvent( SWT.MouseUp, combo, properties );
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
    processTraverseEvent( combo, properties );
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
    combo.notifyListeners( SWT.KeyDown, createKeyEvent( properties ) );
    combo.notifyListeners( SWT.KeyUp, createKeyEvent( properties ) );
  }

  /*
   * PROTOCOL NOTIFY MenuDetect
   *
   * @param x (int) the x coordinate of the pointer
   * @param y (int) the y coordinate of the pointer
   */
  public void handleNotifyMenuDetect( JsonObject properties ) {
    combo.notifyListeners( SWT.MenuDetect, createMenuDetectEvent( properties ) );
  }

  /*
   * PROTOCOL NOTIFY Help
   */
  public void handleNotifyHelp( JsonObject properties ) {
    combo.notifyListeners( SWT.Help, new Event() );
  }

  @Override
  public void handleSet( JsonObject properties ) {
    handleSetSelectionIndex( properties );
    handleSetListVisible( properties );
    handleSetText( properties );
    handleSetSelection( properties );
  }

  /*
   * PROTOCOL SET selectionIndex
   *
   * @param selectionIndex (int) the index of the item to select
   */
  private void handleSetSelectionIndex( JsonObject properties ) {
    JsonValue selectionIndex = properties.get( PROP_SELECTION_INDEX );
    if( selectionIndex != null ) {
      combo.select( selectionIndex.asInt() );
    }
  }

  /*
   * PROTOCOL SET listVisible
   *
   * @param listVisible (boolean) the visibility state of the list
   */
  private void handleSetListVisible( JsonObject properties ) {
    JsonValue listVisible = properties.get( PROP_LIST_VISIBLE );
    if( listVisible != null ) {
      combo.setListVisible( listVisible.asBoolean() );
    }
  }

  /*
   * PROTOCOL SET text
   *
   * @param text (string) the text
   */
  private void handleSetText( JsonObject properties ) {
    final JsonValue value = properties.get( PROP_TEXT );
    if( value != null ) {
      final String text = value.asString();
      if( isListening( combo, SWT.Verify ) ) {
        // setText needs to be executed in a ProcessAcction runnable as it may
        // fire a VerifyEvent whose fields (text and doit) need to be evaluated
        // before actually setting the new value
        ProcessActionRunner.add( new Runnable() {
          public void run() {
            combo.setText( text );
            // since text is set in process action, preserved values have to be replaced
            WidgetUtil.getAdapter( combo ).preserve( PROP_TEXT, text );
         }
        } );
      } else {
        combo.setText( text );
      }
    }
  }

  /*
   * PROTOCOL SET textSelection
   *
   * @param selectionStart (int) the text selection start
   * @param selectionLength (int) the text selection length
   */
  private void handleSetSelection( JsonObject properties ) {
    JsonValue selectionStart = properties.get( PROP_SELECTION_START );
    JsonValue selectionLength = properties.get( PROP_SELECTION_LENGTH );
    if( selectionStart != null || selectionLength != null ) {
      Point selection = new Point( 0, 0 );
      if( selectionStart != null ) {
        selection.x = selectionStart.asInt();
      }
      if( selectionLength != null ) {
        selection.y = selection.x + selectionLength.asInt();
      }
      combo.setSelection( selection );
    }
  }

}
