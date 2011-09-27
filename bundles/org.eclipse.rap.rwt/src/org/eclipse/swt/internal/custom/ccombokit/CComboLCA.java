/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.custom.ccombokit;

import java.io.IOException;

import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Widget;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderListener;

public final class CComboLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Combo";

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
  private static final Integer DEFAULT_TEXT_LIMIT = new Integer( CCombo.LIMIT );
  private static final Point DEFAULT_SELECTION = new Point( 0, 0 );
  private static final Integer DEFAULT_VISIBLE_ITEM_COUNT = new Integer( 5 );

  public void preserveValues( Widget widget ) {
    CCombo ccombo = ( CCombo )widget;
    ControlLCAUtil.preserveValues( ccombo );
    WidgetLCAUtil.preserveCustomVariant( ccombo );
    preserveProperty( ccombo, PROP_ITEMS, ccombo.getItems() );
    preserveProperty( ccombo, PROP_SELECTION_INDEX, new Integer( ccombo.getSelectionIndex() ) );
    preserveProperty( ccombo, PROP_SELECTION, ccombo.getSelection() );
    preserveProperty( ccombo, PROP_TEXT_LIMIT, new Integer( ccombo.getTextLimit() ) );
    preserveProperty( ccombo, PROP_VISIBLE_ITEM_COUNT, new Integer( ccombo.getVisibleItemCount() ) );
    preserveProperty( ccombo, PROP_ITEM_HEIGHT, new Integer( ccombo.getItemHeight() ) );
    preserveProperty( ccombo, PROP_TEXT, ccombo.getText() );
    preserveProperty( ccombo, PROP_LIST_VISIBLE, new Boolean( ccombo.getListVisible() ) );
    preserveProperty( ccombo, PROP_EDITABLE, Boolean.valueOf( ccombo.getEditable() ) );
    preserveListener( ccombo, PROP_SELECTION_LISTENER, SelectionEvent.hasListener( ccombo ) );
    preserveListener( ccombo, PROP_MODIFY_LISTENER, ModifyEvent.hasListener( ccombo ) );
    preserveListener( ccombo, PROP_VERIFY_LISTENER, VerifyEvent.hasListener( ccombo ) );
  }

  public void readData( Widget widget ) {
    CCombo ccombo = ( CCombo )widget;
    String value = WidgetLCAUtil.readPropertyValue( ccombo, "selectedItem" );
    if( value != null ) {
      ccombo.select( NumberFormatUtil.parseInt( value ) );
    }
    String listVisible = WidgetLCAUtil.readPropertyValue( ccombo, "listVisible" );
    if( listVisible != null ) {
      ccombo.setListVisible( Boolean.valueOf( listVisible ).booleanValue() );
    }
    readTextAndSelection( ccombo );
    ControlLCAUtil.processSelection( ccombo, null, true );
    ControlLCAUtil.processMouseEvents( ccombo );
    ControlLCAUtil.processKeyEvents( ccombo );
    ControlLCAUtil.processMenuDetect( ccombo );
    WidgetLCAUtil.processHelp( ccombo );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    CCombo ccombo = ( CCombo )widget;
    IClientObject clientObject = ClientObjectFactory.getForWidget( ccombo );
    clientObject.create( TYPE );
    clientObject.setProperty( "parent", WidgetUtil.getId( ccombo.getParent() ) );
    clientObject.setProperty( "style", WidgetLCAUtil.getStyles( ccombo ) );
    clientObject.setProperty( "ccombo", true );
  }

  public void renderChanges( Widget widget ) throws IOException {
    CCombo ccombo = ( CCombo )widget;
    ControlLCAUtil.renderChanges( ccombo );
    WidgetLCAUtil.renderCustomVariant( ccombo );
    renderItemHeight( ccombo );
    renderVisibleItemCount( ccombo );
    renderItems( ccombo );
    renderListVisible( ccombo );
    renderSelectionIndex( ccombo );
    renderEditable( ccombo );
    renderText( ccombo );
    renderSelection( ccombo );
    renderTextLimit( ccombo );
    renderListenSelection( ccombo );
    renderListenModify( ccombo );
    renderListenVerify( ccombo );
  }

  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getForWidget( widget ).destroy();
  }

  ///////////////////////////////////////
  // Helping methods to read client state

  private static void readTextAndSelection( final CCombo ccombo ) {
    final Point selection = readSelection( ccombo );
    final String txt = WidgetLCAUtil.readPropertyValue( ccombo, "text" );
    if( txt != null ) {
      if( VerifyEvent.hasListener( ccombo ) ) {
        // setText needs to be executed in a ProcessAcction runnable as it may
        // fire a VerifyEvent whose fields (text and doit) need to be evaluated
        // before actually setting the new value
        ProcessActionRunner.add( new Runnable() {
          public void run() {
            ccombo.setText( txt );
            // since text is set in process action, preserved values have to be
            // replaced
            IWidgetAdapter adapter = WidgetUtil.getAdapter( ccombo );
            adapter.preserve( PROP_TEXT, txt );
            if( selection != null ) {
              ccombo.setSelection( selection );
              adapter.preserve( PROP_SELECTION, selection );
            }
         }
        } );
      } else {
        ccombo.setText( txt );
        if( selection != null ) {
          ccombo.setSelection( selection );
        }
      }
    } else if( selection != null ) {
      ccombo.setSelection( selection );
    }
  }

  private static Point readSelection( CCombo ccombo ) {
    Point result = null;
    String selStart = WidgetLCAUtil.readPropertyValue( ccombo, "selectionStart" );
    String selLength = WidgetLCAUtil.readPropertyValue( ccombo, "selectionLength" );
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

  //////////////////////////////////////////////
  // Helping methods to write changed properties

  private static void renderItemHeight( CCombo ccombo ) {
    Integer newValue = new Integer( ccombo.getItemHeight() );
    if( WidgetLCAUtil.hasChanged( ccombo, PROP_ITEM_HEIGHT, newValue ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( ccombo );
      clientObject.setProperty( PROP_ITEM_HEIGHT, newValue );
    }
  }

  private static void renderVisibleItemCount( CCombo ccombo ) {
    Integer newValue = new Integer( ccombo.getVisibleItemCount() );
    Integer defValue = DEFAULT_VISIBLE_ITEM_COUNT;
    renderProperty( ccombo, PROP_VISIBLE_ITEM_COUNT, newValue, defValue );
  }

  private static void renderItems( CCombo ccombo ) {
    renderProperty( ccombo, PROP_ITEMS, ccombo.getItems(), DEFAUT_ITEMS );
  }

  private static void renderListVisible( CCombo ccombo ) {
    String prop = PROP_LIST_VISIBLE;
    Boolean defValue = Boolean.FALSE;
    renderProperty( ccombo, prop, Boolean.valueOf( ccombo.getListVisible() ), defValue );
  }

  private static void renderSelectionIndex( CCombo ccombo ) {
    Integer newValue = new Integer( ccombo.getSelectionIndex() );
    boolean selectionChanged
      = WidgetLCAUtil.hasChanged( ccombo, PROP_SELECTION_INDEX, newValue, DEFAULT_SELECTION_INDEX );
    // The 'textChanged' statement covers the following use case:
    // ccombo.add( "a" );  ccombo.select( 0 );
    // -- in a subsequent request --
    // ccombo.removeAll();  ccombo.add( "b" );  ccombo.select( 0 );
    // When only examining selectionIndex, a change cannot be determined
    boolean textChanged = !ccombo.getEditable()
                          && WidgetLCAUtil.hasChanged( ccombo, PROP_TEXT, ccombo.getText(), "" );
    if( selectionChanged || textChanged ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( ccombo );
      clientObject.setProperty( PROP_SELECTION_INDEX, newValue );
    }
  }

  private static void renderEditable( CCombo ccombo ) {
    renderProperty( ccombo, PROP_EDITABLE, Boolean.valueOf( ccombo.getEditable() ), Boolean.TRUE );
  }

  private static void renderText( CCombo ccombo ) {
    renderProperty( ccombo, PROP_TEXT, ccombo.getText(), "" );
  }

  private static void renderSelection( CCombo ccombo ) {
    Point newValue = ccombo.getSelection();
    if( WidgetLCAUtil.hasChanged( ccombo, PROP_SELECTION, newValue, DEFAULT_SELECTION ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( ccombo );
      Integer start = new Integer( newValue.x );
      Integer end = new Integer( newValue.y );
      clientObject.setProperty( PROP_SELECTION, new Object[] { start, end } );
    }
  }

  private static void renderTextLimit( CCombo ccombo ) {
    Integer newValue = new Integer( ccombo.getTextLimit() );
    if( WidgetLCAUtil.hasChanged( ccombo, PROP_TEXT_LIMIT, newValue, DEFAULT_TEXT_LIMIT ) ) {
      if( newValue.intValue() == CCombo.LIMIT ) {
        newValue = null;
      }
      IClientObject clientObject = ClientObjectFactory.getForWidget( ccombo );
      clientObject.setProperty( PROP_TEXT_LIMIT, newValue );
    }
  }


  private static void renderListenSelection( CCombo ccombo ) {
    renderListener( ccombo, PROP_SELECTION_LISTENER, SelectionEvent.hasListener( ccombo ), false );
  }

  private static void renderListenModify( CCombo ccombo ) {
    renderListener( ccombo, PROP_MODIFY_LISTENER, ModifyEvent.hasListener( ccombo ), false );
  }

  private static void renderListenVerify( CCombo ccombo ) {
    renderListener( ccombo, PROP_VERIFY_LISTENER, VerifyEvent.hasListener( ccombo ), false );
  }
}
