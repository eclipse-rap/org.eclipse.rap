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
package org.eclipse.swt.internal.widgets.textkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory.getClientObject;
import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.getStyles;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.hasChanged;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.readPropertyValue;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.eclipse.swt.internal.events.EventLCAUtil.isListening;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.ITextAdapter;
import org.eclipse.swt.widgets.Text;


final class TextLCAUtil {

  private static final String TYPE = "rwt.widgets.Text";
  private static final String[] ALLOWED_STYLES = new String[] {
    "CENTER",
    "LEFT",
    "RIGHT",
    "MULTI",
    "SINGLE",
    "PASSWORD",
    "SEARCH",
    "WRAP",
    "H_SCROLL",
    "V_SCROLL",
    "BORDER"
  };
  private static final String[] ALLOWED_STYLES_WITH_SEARCH = new String[] {
    "CENTER",
    "LEFT",
    "RIGHT",
    "SINGLE",
    "SEARCH",
    "ICON_CANCEL",
    "ICON_SEARCH",
    "BORDER"
  };

  static final String PROP_TEXT = "text";
  static final String PROP_TEXT_LIMIT = "textLimit";
  static final String PROP_SELECTION = "selection";
  static final String PROP_EDITABLE = "editable";
  static final String PROP_ECHO_CHAR = "echoChar";
  static final String PROP_MESSAGE = "message";
  static final String PROP_MODIFY_LISTENER = "Modify";
  static final String PROP_DEFAULT_SELECTION_LISTENER = "DefaultSelection";

  private static final Point ZERO_SELECTION = new Point( 0, 0 );

  private TextLCAUtil() {
    // prevent instantiation
  }

  static void preserveValues( Text text ) {
    ControlLCAUtil.preserveValues( text );
    WidgetLCAUtil.preserveCustomVariant( text );
    preserveProperty( text, PROP_TEXT, text.getText() );
    preserveProperty( text, PROP_SELECTION, text.getSelection() );
    preserveProperty( text, PROP_TEXT_LIMIT, getTextLimit( text ) );
    preserveProperty( text, PROP_EDITABLE, text.getEditable() );
    preserveProperty( text, PROP_ECHO_CHAR, getEchoChar( text ) );
    preserveProperty( text, PROP_MESSAGE, text.getMessage() );
    preserveListener( text, PROP_MODIFY_LISTENER, hasModifyListener( text ) );
    preserveListener( text,
                      PROP_DEFAULT_SELECTION_LISTENER,
                      isListening( text, SWT.DefaultSelection ) );
  }

  static void renderInitialization( Text text ) {
    IClientObject clientObject = getClientObject( text );
    clientObject.create( TYPE );
    clientObject.set( "parent", getId( text.getParent() ) );
    clientObject.set( "style", createJsonArray( getStyles( text, getAllowedStyles( text ) ) ) );
  }

  static void renderChanges( Text text ) {
    ControlLCAUtil.renderChanges( text );
    WidgetLCAUtil.renderCustomVariant( text );
    renderProperty( text, PROP_TEXT, text.getText(), "" );
    renderProperty( text, PROP_EDITABLE, text.getEditable(), true );
    renderSelection( text );
    renderProperty( text, PROP_TEXT_LIMIT, getTextLimit( text ), null );
    renderProperty( text, PROP_ECHO_CHAR, getEchoChar( text ), null );
    renderProperty( text, PROP_MESSAGE, text.getMessage(), "" );
    renderListener( text, PROP_MODIFY_LISTENER, hasModifyListener( text ), false );
    renderListener( text,
                    PROP_DEFAULT_SELECTION_LISTENER,
                    isListening( text, SWT.DefaultSelection ),
                    false );
  }

  static void readTextAndSelection( final Text text ) {
    final Point selection = readSelection( text );
    final String txt = readPropertyValue( text, "text" );
    if( txt != null ) {
      if( isListening( text, SWT.Verify ) ) {
        // setText needs to be executed in a ProcessAction runnable as it may
        // fire a VerifyEvent whose fields (text and doit) need to be evaluated
        // before actually setting the new value
        ProcessActionRunner.add( new Runnable() {

          public void run() {
            text.getAdapter( ITextAdapter.class ).setText( txt, selection );
            // since text is set in process action, preserved values have to be
            // replaced
            WidgetAdapter adapter = WidgetUtil.getAdapter( text );
            adapter.preserve( PROP_TEXT, txt );
            if( selection != null ) {
              adapter.preserve( PROP_SELECTION, selection );
            }
          }
        } );
      } else {
        text.setText( txt );
        if( selection != null ) {
          text.setSelection( selection );
        }
      }
    } else if( selection != null ) {
      // [rst] Apply selection even if text has not changed
      // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=195171
      text.setSelection( selection );
    }
  }

  private static Point readSelection( Text text ) {
    Point result = null;
    String selStart = readPropertyValue( text, "selectionStart" );
    String selLength = readPropertyValue( text, "selectionLength" );
    if( selStart != null || selLength != null ) {
      result = new Point( 0, 0 );
      if( selStart != null ) {
        result.x = NumberFormatUtil.parseInt( selStart );
      }
      if( selLength != null ) {
        result.y = result.x + NumberFormatUtil.parseInt( selLength );
      }
    }
    return result;
  }

  private static void renderSelection( Text text ) {
    Point newValue = text.getSelection();
    boolean changed = hasChanged( text, PROP_SELECTION, newValue, ZERO_SELECTION );
    if( !changed ) {
      changed = hasChanged( text, PROP_TEXT, text.getText() ) && !newValue.equals( ZERO_SELECTION );
    }
    if( changed ) {
      IClientObject clientObject = getClientObject( text );
      clientObject.set( PROP_SELECTION, new JsonArray().add( newValue.x ).add( newValue.y ) );
    }
  }

  //////////////////
  // Helping methods

  private static String[] getAllowedStyles( Text text ) {
    return ( text.getStyle() & SWT.SEARCH ) != 0 ? ALLOWED_STYLES_WITH_SEARCH : ALLOWED_STYLES;
  }

  private static Integer getTextLimit( Text text ) {
    Integer result = null;
    int textLimit = text.getTextLimit();
    if( textLimit > 0 && textLimit != Text.LIMIT ) {
      result = new Integer( textLimit );
    }
    return result;
  }

  private static String getEchoChar( Text text ) {
    return text.getEchoChar() == 0 ? null : String.valueOf( text.getEchoChar() );
  }

  private static boolean hasModifyListener( Text text ) {
    // NOTE : Client does not support Verify, it is created server-side from Modify
    return isListening( text, SWT.Modify ) || isListening( text, SWT.Verify );
  }

}
