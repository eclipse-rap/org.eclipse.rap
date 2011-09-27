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
package org.eclipse.swt.internal.widgets.combokit;

import java.io.IOException;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderListener;


public class ComboLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Combo";

  // Must be in sync with appearance "list-item"
  private static final int LIST_ITEM_PADDING = 3;

  // Property names for preserve-value facility
  static final String PROP_ITEMS = "items";
  static final String PROP_TEXT = "text";
  static final String PROP_SELECTION_INDEX = "selectionIndex";
  static final String PROP_SELECTION = "selection";
  static final String PROP_TEXT_LIMIT = "textLimit";
  static final String PROP_LIST_VISIBLE = "listVisible";
  static final String PROP_EDITABLE = "editable";
  static final String PROP_VISIBLE_ITEM_COUNT = "visibleItemCount";
  static final String PROP_ITEM_HEIGHT = "itemHeight";
  static final String PROP_SELECTION_LISTENER = "selection";
  static final String PROP_MODIFY_LISTENER = "modify";
  static final String PROP_VERIFY_LISTENER = "verify";

  // Default values
  private static final String[] DEFAUT_ITEMS = new String[ 0 ];
  private static final Integer DEFAULT_SELECTION_INDEX = new Integer( -1 );
  private static final Point DEFAULT_SELECTION = new Point( 0, 0 );
  private static final Integer DEFAULT_TEXT_LIMIT = new Integer( Combo.LIMIT );
  private static final Integer DEFAULT_VISIBLE_ITEM_COUNT = new Integer( 5 );

  public void preserveValues( Widget widget ) {
    Combo combo = ( Combo )widget;
    ControlLCAUtil.preserveValues( combo );
    WidgetLCAUtil.preserveCustomVariant( combo );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_ITEMS, combo.getItems() );
    adapter.preserve( PROP_SELECTION_INDEX, new Integer( combo.getSelectionIndex() ) );
    adapter.preserve( PROP_SELECTION, combo.getSelection() );
    adapter.preserve( PROP_TEXT_LIMIT, new Integer( combo.getTextLimit() ) );
    adapter.preserve( PROP_VISIBLE_ITEM_COUNT, new Integer( combo.getVisibleItemCount() ) );
    adapter.preserve( PROP_ITEM_HEIGHT, new Integer( getItemHeight( combo ) ) );
    adapter.preserve( PROP_TEXT, combo.getText() );
    adapter.preserve( PROP_LIST_VISIBLE, new Boolean( combo.getListVisible() ) );
    adapter.preserve( PROP_EDITABLE, Boolean.valueOf( isEditable( combo ) ) );
    preserveListener( combo, PROP_SELECTION_LISTENER, SelectionEvent.hasListener( combo ) );
    preserveListener( combo, PROP_MODIFY_LISTENER, ModifyEvent.hasListener( combo ) );
    preserveListener( combo, PROP_VERIFY_LISTENER, VerifyEvent.hasListener( combo ) );
  }

  public void readData( Widget widget ) {
    Combo combo = ( Combo )widget;
    String value = WidgetLCAUtil.readPropertyValue( widget, "selectedItem" );
    if( value != null ) {
      combo.select( NumberFormatUtil.parseInt( value ) );
    }
    String listVisible = WidgetLCAUtil.readPropertyValue( combo, "listVisible" );
    if( listVisible != null ) {
      combo.setListVisible( Boolean.valueOf( listVisible ).booleanValue() );
    }
    readTextAndSelection( combo );
    ControlLCAUtil.processSelection( combo, null, true );
    ControlLCAUtil.processMouseEvents( combo );
    ControlLCAUtil.processKeyEvents( combo );
    ControlLCAUtil.processMenuDetect( combo );
    WidgetLCAUtil.processHelp( combo );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    Combo combo = ( Combo )widget;
    IClientObject clientObject = ClientObjectFactory.getForWidget( combo );
    clientObject.create( TYPE );
    clientObject.setProperty( "parent", WidgetUtil.getId( combo.getParent() ) );
    clientObject.setProperty( "style", WidgetLCAUtil.getStyles( combo ) );
  }

  public void renderChanges( Widget widget ) throws IOException {
    Combo combo = ( Combo )widget;
    ControlLCAUtil.renderChanges( combo );
    WidgetLCAUtil.renderCustomVariant( combo );
    renderItemHeight( combo );
    renderVisibleItemCount( combo );
    renderItems( combo );
    renderListVisible( combo );
    renderSelectionIndex( combo );
    renderEditable( combo );
    renderText( combo );
    renderSelection( combo );
    renderTextLimit( combo );
    renderListenSelection( combo );
    renderListenModify( combo );
    renderListenVerify( combo );
  }

  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getForWidget( widget ).destroy();
  }

  ///////////////////////////////////////
  // Helping methods to read client state

  private static void readTextAndSelection( final Combo combo ) {
    final Point selection = readSelection( combo );
    final String value = WidgetLCAUtil.readPropertyValue( combo, "text" );
    if( value != null ) {
      if( VerifyEvent.hasListener( combo ) ) {
        // setText needs to be executed in a ProcessAcction runnable as it may
        // fire a VerifyEvent whose fields (text and doit) need to be evaluated
        // before actually setting the new value
        ProcessActionRunner.add( new Runnable() {
          public void run() {
            combo.setText( value );
            // since text is set in process action, preserved values have to be
            // replaced
            IWidgetAdapter adapter = WidgetUtil.getAdapter( combo );
            adapter.preserve( PROP_TEXT, value );
            if( selection != null ) {
              combo.setSelection( selection );
              adapter.preserve( PROP_SELECTION, selection );
            }
         }
        } );
      } else {
        combo.setText( value );
        if( selection != null ) {
          combo.setSelection( selection );
        }
      }
    } else if( selection != null ) {
      combo.setSelection( selection );
    }
  }

  private static Point readSelection( Combo combo ) {
    Point result = null;
    String selStart = WidgetLCAUtil.readPropertyValue( combo, "selectionStart" );
    String selLength = WidgetLCAUtil.readPropertyValue( combo, "selectionLength" );
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

  ///////////////////////////////////////////////////
  // Helping methods to render the changed properties

  private static void renderItemHeight( Combo combo ) {
    Integer newValue = new Integer( getItemHeight( combo ) );
    if( WidgetLCAUtil.hasChanged( combo, PROP_ITEM_HEIGHT, newValue ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( combo );
      clientObject.setProperty( PROP_ITEM_HEIGHT, newValue );
    }
  }

  private static void renderVisibleItemCount( Combo combo ) {
    Integer newValue = new Integer( combo.getVisibleItemCount() );
    Integer defValue = DEFAULT_VISIBLE_ITEM_COUNT;
    renderProperty( combo, PROP_VISIBLE_ITEM_COUNT, newValue, defValue );
  }

  private static void renderItems( Combo combo ) {
    renderProperty( combo, PROP_ITEMS, combo.getItems(), DEFAUT_ITEMS );
  }

  private static void renderListVisible( Combo combo ) {
    Boolean defValue = Boolean.FALSE;
    renderProperty( combo, PROP_LIST_VISIBLE, Boolean.valueOf( combo.getListVisible() ), defValue );
  }

  private static void renderSelectionIndex( Combo combo ) {
    Integer newValue = new Integer( combo.getSelectionIndex() );
    boolean selectionChanged
      = WidgetLCAUtil.hasChanged( combo, PROP_SELECTION_INDEX, newValue, DEFAULT_SELECTION_INDEX );
    // The 'textChanged' statement covers the following use case:
    // combo.add( "a" );  combo.select( 0 );
    // -- in a subsequent request --
    // combo.removeAll();  combo.add( "b" );  combo.select( 0 );
    // When only examining selectionIndex, a change cannot be determined
    boolean textChanged
      = !isEditable( combo ) && WidgetLCAUtil.hasChanged( combo, PROP_TEXT, combo.getText(), "" );
    if( selectionChanged || textChanged ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( combo );
      clientObject.setProperty( PROP_SELECTION_INDEX, newValue );
    }
  }

  private static void renderEditable( Combo combo ) {
    renderProperty( combo, PROP_EDITABLE, Boolean.valueOf( isEditable( combo ) ), Boolean.TRUE );
  }

  private static void renderText( Combo combo ) {
    if( isEditable( combo ) ) {
      renderProperty( combo, PROP_TEXT, combo.getText(), "" );
    }
  }

  private static void renderSelection( Combo combo ) {
    Point newValue = combo.getSelection();
    if( WidgetLCAUtil.hasChanged( combo, PROP_SELECTION, newValue, DEFAULT_SELECTION ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( combo );
      Integer start = new Integer( newValue.x );
      Integer end = new Integer( newValue.y );
      clientObject.setProperty( PROP_SELECTION, new Object[] { start, end } );
    }
  }

  private static void renderTextLimit( Combo combo ) {
    Integer newValue = new Integer( combo.getTextLimit() );
    if( WidgetLCAUtil.hasChanged( combo, PROP_TEXT_LIMIT, newValue, DEFAULT_TEXT_LIMIT ) ) {
      if( newValue.intValue() == Combo.LIMIT ) {
        newValue = null;
      }
      IClientObject clientObject = ClientObjectFactory.getForWidget( combo );
      clientObject.setProperty( PROP_TEXT_LIMIT, newValue );
    }
  }

  private static void renderListenSelection( Combo combo ) {
    renderListener( combo, PROP_SELECTION_LISTENER, SelectionEvent.hasListener( combo ), false );
  }

  private static void renderListenModify( Combo combo ) {
    renderListener( combo, PROP_MODIFY_LISTENER, ModifyEvent.hasListener( combo ), false );
  }

  private static void renderListenVerify( Combo combo ) {
    renderListener( combo, PROP_VERIFY_LISTENER, VerifyEvent.hasListener( combo ), false );
  }

  //////////////////
  // Helping methods

  private static boolean isEditable( Combo combo ) {
    return ( ( combo.getStyle() & SWT.READ_ONLY ) == 0 );
  }

  static int getItemHeight( Combo combo ) {
    int charHeight = Graphics.getCharHeight( combo.getFont() );
    int padding = 2 * LIST_ITEM_PADDING;
    return charHeight + padding;
  }
}
