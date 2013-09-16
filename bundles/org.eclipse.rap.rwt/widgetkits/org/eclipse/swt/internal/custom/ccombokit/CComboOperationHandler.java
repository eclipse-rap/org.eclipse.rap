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
package org.eclipse.swt.internal.custom.ccombokit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_DEFAULT_SELECTION;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_MODIFY;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getAdapter;
import static org.eclipse.swt.internal.events.EventLCAUtil.isListening;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.ControlOperationHandler;
import org.eclipse.rap.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;


public class CComboOperationHandler extends ControlOperationHandler<CCombo> {

  private static final String PROP_SELECTION_INDEX = "selectionIndex";
  private static final String PROP_LIST_VISIBLE = "listVisible";
  private static final String PROP_TEXT = "text";
  private static final String PROP_SELECTION = "selection";
  private static final String PROP_SELECTION_START = "selectionStart";
  private static final String PROP_SELECTION_LENGTH = "selectionLength";

  public CComboOperationHandler( CCombo ccombo ) {
    super( ccombo );
  }

  @Override
  public void handleSet( CCombo ccombo, JsonObject properties ) {
    super.handleSet( ccombo, properties );
    handleSetSelectionIndex( ccombo, properties );
    handleSetListVisible( ccombo, properties );
    handleSetText( ccombo, properties );
    handleSetSelection( ccombo, properties );
  }

  @Override
  public void handleNotify( CCombo ccombo, String eventName, JsonObject properties ) {
    if( EVENT_SELECTION.equals( eventName ) ) {
      handleNotifySelection( ccombo, properties );
    } else if( EVENT_DEFAULT_SELECTION.equals( eventName ) ) {
      handleNotifyDefaultSelection( ccombo, properties );
    } else if( EVENT_MODIFY.equals( eventName ) ) {
      handleNotifyModify( ccombo, properties );
    } else {
      super.handleNotify( ccombo, eventName, properties );
    }
  }

  /*
   * PROTOCOL SET selectionIndex
   *
   * @param selectionIndex (int) the index of the item to select
   */
  public void handleSetSelectionIndex( CCombo ccombo, JsonObject properties ) {
    JsonValue selectionIndex = properties.get( PROP_SELECTION_INDEX );
    if( selectionIndex != null ) {
      ccombo.select( selectionIndex.asInt() );
    }
  }

  /*
   * PROTOCOL SET listVisible
   *
   * @param listVisible (boolean) the visibility state of the list
   */
  public void handleSetListVisible( CCombo ccombo, JsonObject properties ) {
    JsonValue listVisible = properties.get( PROP_LIST_VISIBLE );
    if( listVisible != null ) {
      ccombo.setListVisible( listVisible.asBoolean() );
    }
  }

  /*
   * PROTOCOL SET text
   *
   * @param text (string) the text
   */
  public void handleSetText( final CCombo ccombo, JsonObject properties ) {
    final JsonValue value = properties.get( PROP_TEXT );
    if( value != null ) {
      final String text = value.asString();
      if( isListening( ccombo, SWT.Verify ) ) {
        // setText needs to be executed in a ProcessAcction runnable as it may
        // fire a VerifyEvent whose fields (text and doit) need to be evaluated
        // before actually setting the new value
        ProcessActionRunner.add( new Runnable() {
          public void run() {
            ccombo.setText( text );
            // since text is set in process action, preserved values have to be replaced
            getAdapter( ccombo ).preserve( PROP_TEXT, text );
         }
        } );
      } else {
        ccombo.setText( text );
      }
    }
  }

  /*
   * PROTOCOL SET textSelection
   *
   * @param selectionStart (int) the text selection start
   * @param selectionLength (int) the text selection length
   */
  public void handleSetSelection( final CCombo ccombo, JsonObject properties ) {
    final Point selection = readSelection( properties );
    if( selection != null ) {
      if( isListening( ccombo, SWT.Verify ) ) {
        // if text is delayed, delay the selection too
        ProcessActionRunner.add( new Runnable() {
          public void run() {
            ccombo.setSelection( selection );
            // since selection is set in process action, preserved values have to be replaced
            getAdapter( ccombo ).preserve( PROP_SELECTION, selection );
          }
        } );
      } else {
        ccombo.setSelection( selection );
      }
    }
  }

  /*
   * PROTOCOL NOTIFY Selection
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   */
  public void handleNotifySelection( CCombo ccombo, JsonObject properties ) {
    Event event = createSelectionEvent( SWT.Selection, properties );
    ccombo.notifyListeners( SWT.Selection, event );
  }

  /*
   * PROTOCOL NOTIFY DefaultSelection
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   */
  public void handleNotifyDefaultSelection( CCombo ccombo, JsonObject properties ) {
    Event event = createSelectionEvent( SWT.DefaultSelection, properties );
    ccombo.notifyListeners( SWT.DefaultSelection, event );
  }

  /*
   * PROTOCOL NOTIFY Modify
   * ignored, Modify event is fired when set text
   */
  public void handleNotifyModify( CCombo ccombo, JsonObject properties ) {
  }

  private static Point readSelection( JsonObject properties ) {
    Point selection = null;
    JsonValue selectionStart = properties.get( PROP_SELECTION_START );
    JsonValue selectionLength = properties.get( PROP_SELECTION_LENGTH );
    if( selectionStart != null || selectionLength != null ) {
      selection = new Point( 0, 0 );
      if( selectionStart != null ) {
        selection.x = selectionStart.asInt();
      }
      if( selectionLength != null ) {
        selection.y = selection.x + selectionLength.asInt();
      }
    }
    return selection;
  }

}
