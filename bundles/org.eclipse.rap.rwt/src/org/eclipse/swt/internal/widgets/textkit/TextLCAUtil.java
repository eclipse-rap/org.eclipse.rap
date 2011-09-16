/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
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

import java.io.IOException;

import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.ITextAdapter;
import org.eclipse.swt.widgets.*;


final class TextLCAUtil {

  private static final String TYPE = "rwt.widgets.Text";
  static final String PROP_TEXT = "text";
  static final String PROP_TEXT_LIMIT = "textLimit";
  static final String PROP_SELECTION = "selection";
  static final String PROP_EDITABLE = "editable";
  static final String PROP_ECHO_CHAR = "echoChar";
  static final String PROP_MESSAGE = "message";
  static final String PROP_MODIFY_LISTENER = "modifyListener";
  static final String PROP_VERIFY_LISTENER = "verifyListener";
  static final String PROP_SELECTION_LISTENER = "selectionListener";

  private static final Point DEFAULT_SELECTION = new Point( 0, 0 );

  private TextLCAUtil() {
    // prevent instantiation
  }

  static void preserveValues( Text text ) {
    ControlLCAUtil.preserveValues( text );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    adapter.preserve( PROP_TEXT, text.getText() );
    adapter.preserve( PROP_SELECTION, text.getSelection() );
    adapter.preserve( PROP_TEXT_LIMIT, getTextLimit( text ) );
    adapter.preserve( PROP_EDITABLE, Boolean.valueOf( text.getEditable() ) );
    adapter.preserve( PROP_ECHO_CHAR, getEchoChar( text ) );
    adapter.preserve( PROP_MESSAGE, text.getMessage() );
    adapter.preserve( PROP_MODIFY_LISTENER,
                      Boolean.valueOf( ModifyEvent.hasListener( text ) ) );
    adapter.preserve( PROP_VERIFY_LISTENER,
                      Boolean.valueOf( VerifyEvent.hasListener( text ) ) );
    adapter.preserve( PROP_SELECTION_LISTENER,
                      Boolean.valueOf( SelectionEvent.hasListener( text ) ) );
    WidgetLCAUtil.preserveCustomVariant( text );
  }

  static void renderInitialization( Text text ) {
    IClientObject clientObject = ClientObjectFactory.getForWidget( text );
    clientObject.create( TYPE );
    clientObject.setProperty( "parent", WidgetUtil.getId( text.getParent() ) );
    clientObject.setProperty( "style", WidgetLCAUtil.getStyles( text ) );
  }

  static void renderChanges( Text text ) throws IOException {
    ControlLCAUtil.renderChanges( text );
    WidgetLCAUtil.renderCustomVariant( text );
    renderText( text );
    renderEditable( text );
    renderSelection( text );
    renderTextLimit( text );
    renderEchoChar( text );
    renderMessage( text );
    renderListenModify( text );
    renderListenVerify( text );
    renderListenSelection( text );
  }

  static void readTextAndSelection( final Text text ) {
    final Point selection = readSelection( text );
    final String txt = WidgetLCAUtil.readPropertyValue( text, "text" );
    if( txt != null ) {
      if( VerifyEvent.hasListener( text ) ) {
        // setText needs to be executed in a ProcessAction runnable as it may
        // fire a VerifyEvent whose fields (text and doit) need to be evaluated
        // before actually setting the new value
        ProcessActionRunner.add( new Runnable() {

          public void run() {
            ITextAdapter textAdapter = getTextAdapter( text );
            textAdapter.setText( txt, selection );
            // since text is set in process action, preserved values have to be
            // replaced
            IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
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
    String selStart = WidgetLCAUtil.readPropertyValue( text, "selectionStart" );
    String selLength = WidgetLCAUtil.readPropertyValue( text, "selectionLength" );
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

  private static void renderText( Text text ) {
    renderProperty( text, PROP_TEXT, text.getText(), "" );
  }

  private static void renderEditable( Text text ) {
    renderProperty( text, PROP_EDITABLE, Boolean.valueOf( text.getEditable() ), Boolean.TRUE );
  }

  private static void renderTextLimit( Text text ) {
    renderProperty( text, PROP_TEXT_LIMIT, getTextLimit( text ), null );
  }

  private static void renderSelection( Text text ) {
    Point newValue = text.getSelection();
    if( WidgetLCAUtil.hasChanged( text, PROP_SELECTION, newValue, DEFAULT_SELECTION ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( text );
      Integer start = new Integer( newValue.x );
      Integer end = new Integer( newValue.y );
      clientObject.setProperty( PROP_SELECTION, new Object[] { start, end } );
    }
  }

  private static void renderEchoChar( Text text ) {
    renderProperty( text, PROP_ECHO_CHAR, getEchoChar( text ), null );
  }

  private static void renderMessage( Text text ) {
    renderProperty( text, PROP_MESSAGE, text.getMessage(), "" );
  }

  static void renderListenSelection( Text text ) {
    Boolean newValue = Boolean.valueOf( SelectionEvent.hasListener( text ) );
    if( WidgetLCAUtil.hasChanged( text, PROP_SELECTION_LISTENER, newValue, Boolean.FALSE ) ) {
      renderListen( text, "selection", newValue.booleanValue() );
    }
  }

  private static void renderListenModify( Text text ) {
    Boolean newValue = Boolean.valueOf( ModifyEvent.hasListener( text ) );
    if( WidgetLCAUtil.hasChanged( text, PROP_MODIFY_LISTENER, newValue, Boolean.FALSE ) ) {
      renderListen( text, "modify", newValue.booleanValue() );
    }
  }

  private static void renderListenVerify( Text text ) {
    Boolean newValue = Boolean.valueOf( VerifyEvent.hasListener( text ) );
    if( WidgetLCAUtil.hasChanged( text, PROP_VERIFY_LISTENER, newValue, Boolean.FALSE ) ) {
      renderListen( text, "verify", newValue.booleanValue() );
    }
  }

  //////////////////
  // Helping methods

  private static void renderProperty( Text text, String property, Object newValue, Object defValue )
  {
    if( WidgetLCAUtil.hasChanged( text, property, newValue, defValue ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( text );
      clientObject.setProperty( property, newValue );
    }
  }

  private static void renderListen( Text text, String eventType, boolean hasListener ) {
    IClientObject clientObject = ClientObjectFactory.getForWidget( text );
    if( hasListener ) {
      clientObject.addListener( eventType );
    } else {
      clientObject.removeListener( eventType );
    }
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

  private static ITextAdapter getTextAdapter( Text text ) {
    return ( ITextAdapter )text.getAdapter( ITextAdapter.class );
  }
}
