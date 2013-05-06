/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.getStyles;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.eclipse.swt.internal.events.EventLCAUtil.isListening;

import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.internal.util.MnemonicUtil;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;


final class ButtonLCAUtil {

  private static final String TYPE = "rwt.widgets.Button";
  private static final String[] ALLOWED_STYLES = new String[] {
    "ARROW", "CHECK", "PUSH", "RADIO", "TOGGLE", "FLAT", "WRAP", "BORDER"
  };

  private static final String PROP_TEXT = "text";
  private static final String PROP_MNEMONIC_INDEX = "mnemonicIndex";
  private static final String PROP_IMAGE = "image";
  private static final String PROP_SELECTION = "selection";
  private static final String PROP_GRAYED = "grayed";
  private static final String PROP_ALIGNMENT = "alignment";
  private static final String PROP_SELECTION_LISTENERS = "Selection";

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
    preserveListener( button, PROP_SELECTION_LISTENERS, isListening( button, SWT.Selection ) );
  }

  static void renderInitialization( Button button ) {
    IClientObject clientObject = ClientObjectFactory.getClientObject( button );
    clientObject.create( TYPE );
    clientObject.set( "parent", getId( button.getParent() ) );
    clientObject.set( "style", createJsonArray( getStyles( button, ALLOWED_STYLES ) ) );
  }

  static void renderChanges( Button button ) {
    ControlLCAUtil.renderChanges( button );
    WidgetLCAUtil.renderCustomVariant( button );
    renderText( button );
    renderMnemonicIndex( button );
    renderProperty( button, PROP_IMAGE, button.getImage(), null );
    renderProperty( button, PROP_ALIGNMENT, getAlignment( button ), DEFAULT_ALIGNMENT );
    renderProperty( button, PROP_SELECTION, button.getSelection(), false );
    renderProperty( button, PROP_GRAYED, button.getGrayed(), false );
    renderListener( button, PROP_SELECTION_LISTENERS, isListening( button, SWT.Selection ), false );
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

  private static void renderText( Button button ) {
    String newValue = button.getText();
    if( WidgetLCAUtil.hasChanged( button, PROP_TEXT, newValue, "" ) ) {
      String text = MnemonicUtil.removeAmpersandControlCharacters( newValue );
      IClientObject clientObject = ClientObjectFactory.getClientObject( button );
      clientObject.set( PROP_TEXT, text );
    }
  }

  private static void renderMnemonicIndex( Button button ) {
    String text = button.getText();
    if( WidgetLCAUtil.hasChanged( button, PROP_TEXT, text, "" ) ) {
      int mnemonicIndex = MnemonicUtil.findMnemonicCharacterIndex( text );
      if( mnemonicIndex != -1 ) {
        IClientObject clientObject = ClientObjectFactory.getClientObject( button );
        clientObject.set( PROP_MNEMONIC_INDEX, mnemonicIndex );
      }
    }
  }

}
