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
import org.eclipse.rwt.internal.util.EncodingUtil;
import org.eclipse.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.ITextAdapter;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;


final class TextLCAUtil {

  private static final String TYPE = "rwt.widgets.Text";
  static final String PROP_TEXT = "text";
  static final String PROP_TEXT_LIMIT = "textLimit";
  static final String PROP_SELECTION = "selection";
  static final String PROP_EDITABLE = "editable";
  static final String PROP_VERIFY_MODIFY_LISTENER = "verifyModifyListener";
  static final String PROP_SELECTION_LISTENER = "selectionListener";
  static final String PROP_ECHO_CHAR = "echoChar";

  private static final Integer DEFAULT_TEXT_LIMIT = new Integer( Text.LIMIT );
  private static final Point DEFAULT_SELECTION = new Point( 0, 0 );

  private static final String JS_PROP_VALUE = "value";

  private TextLCAUtil() {
    // prevent instantiation
  }

  static void preserveValues( Text text ) {
    ControlLCAUtil.preserveValues( text );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    adapter.preserve( PROP_TEXT, text.getText() );
    adapter.preserve( PROP_SELECTION, text.getSelection() );
    adapter.preserve( PROP_TEXT_LIMIT, new Integer( text.getTextLimit() ) );
    adapter.preserve( PROP_EDITABLE, Boolean.valueOf( text.getEditable() ) );
    preserveVerifyAndModifyListener( text );
    WidgetLCAUtil.preserveCustomVariant( text );
  }

  static void renderInitialization( Text text ) {
    IClientObject clientObject = ClientObjectFactory.getForWidget( text );
    clientObject.create( TYPE );
    clientObject.setProperty( "parent", WidgetUtil.getId( text.getParent() ) );
    clientObject.setProperty( "style", WidgetLCAUtil.getStyles( text ) );
  }

  static void renderChanges( Text text ) throws IOException {
    renderEditable( text );
    renderSelection( text );
    renderTextLimit( text );
    WidgetLCAUtil.renderCustomVariant( text );
    ControlLCAUtil.renderChanges( text );
    writeVerifyAndModifyListener( text );
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

  static void writeText( Text text, boolean replaceNewLines ) throws IOException {
    String newValue = text.getText();
    JSWriter writer = JSWriter.getWriterFor( text );
    if( WidgetLCAUtil.hasChanged( text, PROP_TEXT, newValue, "" ) ) {
      if( replaceNewLines ) {
        newValue = WidgetLCAUtil.replaceNewLines( newValue, " " );
      }
      newValue = EncodingUtil.removeNonDisplayableChars( newValue );
      newValue = EncodingUtil.truncateAtZero( newValue );
      writer.set( JS_PROP_VALUE, newValue );
    }
  }

  private static void renderEditable( Text text ) {
    Boolean newValue = Boolean.valueOf( text.getEditable() );
    if( WidgetLCAUtil.hasChanged( text, PROP_EDITABLE, newValue, Boolean.TRUE ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( text );
      clientObject.setProperty( PROP_EDITABLE, newValue );
    }
  }

  private static void renderTextLimit( Text text ) {
    Integer newValue = new Integer( text.getTextLimit() );
    if( WidgetLCAUtil.hasChanged( text, PROP_TEXT_LIMIT, newValue, DEFAULT_TEXT_LIMIT ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( text );
      clientObject.setProperty( PROP_TEXT_LIMIT, newValue );
    }
  }

  private static void renderSelection( Text text ) {
    Point newValue = text.getSelection();
    // TODO [rh] could be optimized: when text was changed and selection is 0,0
    //      there is no need to write JavaScript since the client resets the
    //      selection as well when the new text is set.
    if( WidgetLCAUtil.hasChanged( text, PROP_SELECTION, newValue, DEFAULT_SELECTION ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( text );
      Integer start = new Integer( newValue.x );
      Integer lenght = new Integer( text.getSelectionCount() );
      clientObject.setProperty( "selection", new Object[] { start, lenght } );
    }
  }

  static void preserveSelectionListener( Text text ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    adapter.preserve( PROP_SELECTION_LISTENER, Boolean.valueOf( hasSelectionListener( text ) ) );
  }

  static void writeSelectionListener( Text text ) throws IOException {
    Boolean newValue = Boolean.valueOf( hasSelectionListener( text ) );
    if( WidgetLCAUtil.hasChanged( text, PROP_SELECTION_LISTENER, newValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( text );
      writer.callStatic( "org.eclipse.swt.TextUtil.setHasSelectionListener",
                         new Object[] { text, newValue } );
    }
  }

  private static void preserveVerifyAndModifyListener( Text text ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    adapter.preserve( PROP_VERIFY_MODIFY_LISTENER,
                      Boolean.valueOf( hasVerifyOrModifyListener( text ) ) );
  }

  private static void writeVerifyAndModifyListener( Text text ) throws IOException {
    Boolean newValue = Boolean.valueOf( hasVerifyOrModifyListener( text ) );
    String prop = PROP_VERIFY_MODIFY_LISTENER;
    if( WidgetLCAUtil.hasChanged( text, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( text );
      String function = "org.eclipse.swt.TextUtil.setHasVerifyOrModifyListener";
      writer.callStatic( function, new Object[] { text, newValue } );
    }
  }

  static void preserveEchoChar( Text text ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    adapter.preserve( PROP_ECHO_CHAR, getEchoChar( text ) );
  }

  static void renderEchoChar( Text text ) {
    String newValue = getEchoChar( text );
    if( WidgetLCAUtil.hasChanged( text, PROP_ECHO_CHAR, newValue, null ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( text );
      clientObject.setProperty( "echoChar", newValue );
    }
  }

  private static String getEchoChar( Text text ) {
    return text.getEchoChar() == 0 ? null : String.valueOf( text.getEchoChar() );
  }

  private static boolean hasSelectionListener( Text text ) {
    // Emulate SWT (on Windows) where a default button takes precedence over
    // a SelectionListener on a text field when both are on the same shell.
    Button defButton = text.getShell().getDefaultButton();
    // TODO [rst] On GTK, the SelectionListener is also off when the default
    //      button is invisible or disabled. Check with Windows and repair.
    boolean hasDefaultButton = defButton != null && defButton.isVisible();
    return !hasDefaultButton && SelectionEvent.hasListener( text );
  }

  private static boolean hasVerifyOrModifyListener( Text text ) {
    boolean hasVerifyListener = VerifyEvent.hasListener( text );
    boolean hasModifyListener = ModifyEvent.hasListener( text );
    return hasModifyListener || hasVerifyListener;
  }

  private static ITextAdapter getTextAdapter( Text text ) {
    return ( ITextAdapter )text.getAdapter( ITextAdapter.class );
  }
}
