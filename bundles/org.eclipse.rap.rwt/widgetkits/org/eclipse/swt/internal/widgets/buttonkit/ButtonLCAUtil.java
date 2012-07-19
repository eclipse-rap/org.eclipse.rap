/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.buttonkit;

import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderListener;


final class ButtonLCAUtil {

  private static final String TYPE = "rwt.widgets.Button";
  private static final String[] ALLOWED_STYLES = new String[] {
    "ARROW", "CHECK", "PUSH", "RADIO", "TOGGLE", "FLAT", "WRAP", "BORDER"
  };

  static final String PROP_TEXT = "text";
  static final String PROP_IMAGE = "image";
  static final String PROP_SELECTION = "selection";
  static final String PROP_GRAYED = "grayed";
  static final String PROP_ALIGNMENT = "alignment";
  static final String PROP_SELECTION_LISTENERS = "selection";

  private static final String PARAM_SELECTION = "selection";
  private static final String DEFAULT_ALIGNMENT = "center";

  private ButtonLCAUtil() {
    // prevent instantiation
  }

  static void preserveValues( Button button ) {
    ControlLCAUtil.preserveValues( button );
    WidgetLCAUtil.preserveCustomVariant( button );
    preserveProperty( button, PROP_TEXT, button.getText() );
    preserveProperty( button, PROP_IMAGE, button.getImage() );
    preserveProperty( button, PROP_SELECTION, Boolean.valueOf( button.getSelection() ) );
    preserveProperty( button, PROP_GRAYED, Boolean.valueOf( button.getGrayed() ) );
    preserveProperty( button, PROP_ALIGNMENT, getAlignment( button ) );
    preserveListener( button, PROP_SELECTION_LISTENERS, SelectionEvent.hasListener( button ) );
  }

  static void renderInitialization( Button button ) {
    IClientObject clientObject = ClientObjectFactory.getClientObject( button );
    clientObject.create( TYPE );
    clientObject.set( "parent", WidgetUtil.getId( button.getParent() ) );
    clientObject.set( "style", WidgetLCAUtil.getStyles( button, ALLOWED_STYLES ) );
  }

  static void renderChanges( Button button ) {
    ControlLCAUtil.renderChanges( button );
    WidgetLCAUtil.renderCustomVariant( button );
    renderProperty( button, PROP_TEXT, button.getText(), "" );
    renderProperty( button, PROP_IMAGE, button.getImage(), null );
    renderProperty( button, PROP_ALIGNMENT, getAlignment( button ), DEFAULT_ALIGNMENT );
    renderProperty( button, PROP_SELECTION, button.getSelection(), false );
    renderProperty( button, PROP_GRAYED, button.getGrayed(), false );
    renderListener( button, PROP_SELECTION_LISTENERS, SelectionEvent.hasListener( button ), false );
  }

  static boolean readSelection( Button button ) {
    String value = WidgetLCAUtil.readPropertyValue( button, PARAM_SELECTION );
    if( value != null ) {
      button.setSelection( Boolean.valueOf( value ).booleanValue() );
    }
    return value != null;
  }

  //////////////////
  // Helping methods

  private static String getAlignment( Button button ) {
    int alignment = button.getAlignment();
    String result;
    if( ( alignment & SWT.LEFT ) != 0 ) {
      result = "left";
    } else if( ( alignment & SWT.CENTER ) != 0 ) {
      result = "center";
    } else if( ( alignment & SWT.RIGHT ) != 0 ) {
      result = "right";
    } else if( ( alignment & SWT.UP ) != 0 ) {
      result = "up";
    } else if( ( alignment & SWT.DOWN ) != 0 ) {
      result = "down";
    } else {
      result = "left";
    }
    return result;
  }
}
