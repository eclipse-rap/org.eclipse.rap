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

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.ControlOperationHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;


public class ButtonOperationHandler extends ControlOperationHandler<Button> {

  private static final String PROP_SELECTION = "selection";

  public ButtonOperationHandler( Button button ) {
    super( button );
  }

  @Override
  public void handleSet( Button button, JsonObject properties ) {
    handleSetSelection( button, properties );
  }

  /*
   * PROTOCOL SET selection
   *
   * @param selection (boolean) true if the button was selected, otherwise false
   */
  public void handleSetSelection( Button button, JsonObject properties ) {
    JsonValue selection = properties.get( PROP_SELECTION );
    if( selection != null ) {
      button.setSelection( selection.asBoolean() );
    }
  }

  @Override
  public void handleNotify( Button button, String eventName, JsonObject properties ) {
    if( "Selection".equals( eventName ) ) {
      handleNotifySelection( button, properties );
    } else if( "DefaultSelection".equals( eventName ) ) {
      handleNotifyDefaultSelection( button, properties );
    } else {
      super.handleNotify( button, eventName, properties );
    }
  }

  /*
   * PROTOCOL NOTIFY Selection
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   */
  public void handleNotifySelection( Button button, JsonObject properties ) {
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
  public void handleNotifyDefaultSelection( Button button, JsonObject properties ) {
    Event event = createSelectionEvent( SWT.DefaultSelection, properties );
    button.notifyListeners( SWT.DefaultSelection, event );
  }

}
