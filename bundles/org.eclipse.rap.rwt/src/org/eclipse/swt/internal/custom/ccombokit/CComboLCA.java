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
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Widget;

public final class CComboLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Combo";
  private static final String[] DEFAUT_ITEMS = new String[ 0 ];
  private static final Integer DEFAULT_SELECTION_INDEX = new Integer( -1 );
  private static final Integer DEFAULT_TEXT_LIMIT = new Integer( CCombo.LIMIT );
  private static final Point DEFAULT_SELECTION = new Point( 0, 0 );
  private static final Integer DEFAULT_VISIBLE_ITEM_COUNT = new Integer( 5 );

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
  static final String PROP_SELECTION_LISTENER = "selectionListener";
  static final String PROP_MODIFY_LISTENER = "modifyListener";
  static final String PROP_VERIFY_LISTENER = "verifyListener";

  public void preserveValues( Widget widget ) {
    CCombo ccombo = ( CCombo )widget;
    ControlLCAUtil.preserveValues( ccombo );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    String[] items = ccombo.getItems();
    adapter.preserve( PROP_ITEMS, items );
    adapter.preserve( PROP_SELECTION_INDEX, new Integer( ccombo.getSelectionIndex() ) );
    adapter.preserve( PROP_SELECTION, ccombo.getSelection() );
    adapter.preserve( PROP_TEXT_LIMIT, new Integer( ccombo.getTextLimit() ) );
    adapter.preserve( PROP_VISIBLE_ITEM_COUNT, new Integer( ccombo.getVisibleItemCount() ) );
    adapter.preserve( PROP_ITEM_HEIGHT, new Integer( ccombo.getItemHeight() ) );
    adapter.preserve( PROP_TEXT, ccombo.getText() );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( SelectionEvent.hasListener( ccombo ) ) );
    adapter.preserve( PROP_LIST_VISIBLE, new Boolean( ccombo.getListVisible() ) );
    adapter.preserve( PROP_EDITABLE, Boolean.valueOf( ccombo.getEditable() ) );
    adapter.preserve( PROP_SELECTION_LISTENER,
                      Boolean.valueOf( SelectionEvent.hasListener( ccombo ) ) );
    adapter.preserve( PROP_MODIFY_LISTENER,
                      Boolean.valueOf( ModifyEvent.hasListener( ccombo ) ) );
    adapter.preserve( PROP_VERIFY_LISTENER,
                      Boolean.valueOf( VerifyEvent.hasListener( ccombo ) ) );
    WidgetLCAUtil.preserveCustomVariant( ccombo );
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
    if( WidgetLCAUtil.hasChanged( ccombo, PROP_VISIBLE_ITEM_COUNT, newValue, defValue ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( ccombo );
      clientObject.setProperty( PROP_VISIBLE_ITEM_COUNT, newValue );
    }
  }

  private static void renderItems( CCombo ccombo ) {
    String[] items = ccombo.getItems();
    if( WidgetLCAUtil.hasChanged( ccombo, PROP_ITEMS, items, DEFAUT_ITEMS ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( ccombo );
      clientObject.setProperty( PROP_ITEMS, items );
    }
  }

  private static void renderListVisible( CCombo ccombo ) {
    Boolean newValue = Boolean.valueOf( ccombo.getListVisible() );
    if( WidgetLCAUtil.hasChanged( ccombo, PROP_LIST_VISIBLE, newValue, Boolean.FALSE ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( ccombo );
      clientObject.setProperty( PROP_LIST_VISIBLE, newValue );
    }
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
    Boolean newValue = Boolean.valueOf( ccombo.getEditable() );
    if( WidgetLCAUtil.hasChanged( ccombo, PROP_EDITABLE, newValue, Boolean.TRUE ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( ccombo );
      clientObject.setProperty( PROP_EDITABLE, newValue );
    }
  }

  private static void renderText( CCombo ccombo ) {
    String newValue = ccombo.getText();
    if( WidgetLCAUtil.hasChanged( ccombo, PROP_TEXT, newValue, "" ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( ccombo );
      clientObject.setProperty( PROP_TEXT, newValue );
    }
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
    Boolean newValue = Boolean.valueOf( SelectionEvent.hasListener( ccombo ) );
    if( WidgetLCAUtil.hasChanged( ccombo, PROP_SELECTION_LISTENER, newValue, Boolean.FALSE ) ) {
      renderListen( ccombo, "selection", newValue.booleanValue() );
    }
  }

  private static void renderListenModify( CCombo ccombo ) {
    Boolean newValue = Boolean.valueOf( ModifyEvent.hasListener( ccombo ) );
    if( WidgetLCAUtil.hasChanged( ccombo, PROP_MODIFY_LISTENER, newValue, Boolean.FALSE ) ) {
      renderListen( ccombo, "modify", newValue.booleanValue() );
    }
  }

  private static void renderListenVerify( CCombo ccombo ) {
    Boolean newValue = Boolean.valueOf( VerifyEvent.hasListener( ccombo ) );
    if( WidgetLCAUtil.hasChanged( ccombo, PROP_VERIFY_LISTENER, newValue, Boolean.FALSE ) ) {
      renderListen( ccombo, "verify", newValue.booleanValue() );
    }
  }

  //////////////////
  // Helping methods

  private static void renderListen( CCombo ccombo, String eventType, boolean hasListener ) {
    IClientObject clientObject = ClientObjectFactory.getForWidget( ccombo );
    if( hasListener ) {
      clientObject.addListener( eventType );
    } else {
      clientObject.removeListener( eventType );
    }
  }
}
