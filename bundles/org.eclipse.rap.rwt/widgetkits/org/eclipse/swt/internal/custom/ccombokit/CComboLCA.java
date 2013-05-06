/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.custom.ccombokit;

import static org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory.getClientObject;
import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.getStyles;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.hasChanged;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.readPropertyValue;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getAdapter;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.eclipse.swt.internal.events.EventLCAUtil.isListening;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Widget;


public final class CComboLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Combo";
  private static final String[] ALLOWED_STYLES = new String[] { "FLAT", "BORDER" };

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
  static final String PROP_SELECTION_LISTENER = "Selection";
  static final String PROP_DEFAULT_SELECTION_LISTENER = "DefaultSelection";
  static final String PROP_MODIFY_LISTENER = "Modify";

  // Default values
  private static final String[] DEFAUT_ITEMS = new String[ 0 ];
  private static final Integer DEFAULT_SELECTION_INDEX = new Integer( -1 );
  private static final Point DEFAULT_SELECTION = new Point( 0, 0 );
  private static final int DEFAULT_VISIBLE_ITEM_COUNT = 5;

  @Override
  public void preserveValues( Widget widget ) {
    CCombo ccombo = ( CCombo )widget;
    ControlLCAUtil.preserveValues( ccombo );
    WidgetLCAUtil.preserveCustomVariant( ccombo );
    preserveProperty( ccombo, PROP_ITEMS, ccombo.getItems() );
    preserveProperty( ccombo, PROP_SELECTION_INDEX, ccombo.getSelectionIndex() );
    preserveProperty( ccombo, PROP_SELECTION, ccombo.getSelection() );
    preserveProperty( ccombo, PROP_TEXT_LIMIT, getTextLimit( ccombo ) );
    preserveProperty( ccombo, PROP_VISIBLE_ITEM_COUNT, ccombo.getVisibleItemCount() );
    preserveProperty( ccombo, PROP_ITEM_HEIGHT, ccombo.getItemHeight() );
    preserveProperty( ccombo, PROP_TEXT, ccombo.getText() );
    preserveProperty( ccombo, PROP_LIST_VISIBLE, ccombo.getListVisible() );
    preserveProperty( ccombo, PROP_EDITABLE, Boolean.valueOf( ccombo.getEditable() ) );
    preserveListener( ccombo, PROP_SELECTION_LISTENER, isListening( ccombo, SWT.Selection ) );
    preserveListener( ccombo,
                      PROP_DEFAULT_SELECTION_LISTENER,
                      isListening( ccombo, SWT.DefaultSelection ) );
    preserveListener( ccombo, PROP_MODIFY_LISTENER, hasModifyListener( ccombo ) );
  }

  public void readData( Widget widget ) {
    CCombo ccombo = ( CCombo )widget;
    String value = readPropertyValue( ccombo, "selectionIndex" );
    if( value != null ) {
      ccombo.select( NumberFormatUtil.parseInt( value ) );
    }
    String listVisible = readPropertyValue( ccombo, "listVisible" );
    if( listVisible != null ) {
      ccombo.setListVisible( Boolean.valueOf( listVisible ).booleanValue() );
    }
    readTextAndSelection( ccombo );
    ControlLCAUtil.processSelection( ccombo, null, true );
    ControlLCAUtil.processDefaultSelection( ccombo, null );
    ControlLCAUtil.processEvents( ccombo );
    ControlLCAUtil.processKeyEvents( ccombo );
    ControlLCAUtil.processMenuDetect( ccombo );
    WidgetLCAUtil.processHelp( ccombo );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    CCombo ccombo = ( CCombo )widget;
    IClientObject clientObject = getClientObject( ccombo );
    clientObject.create( TYPE );
    clientObject.set( "parent", getId( ccombo.getParent() ) );
    clientObject.set( "style", createJsonArray( getStyles( ccombo, ALLOWED_STYLES ) ) );
    clientObject.set( "ccombo", true );
  }

  @Override
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
    renderListenDefaultSelection( ccombo );
    renderListenModify( ccombo );
  }

  ///////////////////////////////////////
  // Helping methods to read client state

  private static void readTextAndSelection( final CCombo ccombo ) {
    final Point selection = readSelection( ccombo );
    final String txt = readPropertyValue( ccombo, "text" );
    if( txt != null ) {
      if( isListening( ccombo, SWT.Verify ) ) {
        // setText needs to be executed in a ProcessAcction runnable as it may
        // fire a VerifyEvent whose fields (text and doit) need to be evaluated
        // before actually setting the new value
        ProcessActionRunner.add( new Runnable() {
          public void run() {
            ccombo.setText( txt );
            // since text is set in process action, preserved values have to be
            // replaced
            WidgetAdapter adapter = getAdapter( ccombo );
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
    String selStart = readPropertyValue( ccombo, "selectionStart" );
    String selLength = readPropertyValue( ccombo, "selectionLength" );
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
    Integer newValue = Integer.valueOf( ccombo.getItemHeight() );
    if( hasChanged( ccombo, PROP_ITEM_HEIGHT, newValue ) ) {
      getClientObject( ccombo ).set( PROP_ITEM_HEIGHT, newValue.intValue() );
    }
  }

  private static void renderVisibleItemCount( CCombo ccombo ) {
    int defValue = DEFAULT_VISIBLE_ITEM_COUNT;
    renderProperty( ccombo, PROP_VISIBLE_ITEM_COUNT, ccombo.getVisibleItemCount(), defValue );
  }

  private static void renderItems( CCombo ccombo ) {
    renderProperty( ccombo, PROP_ITEMS, ccombo.getItems(), DEFAUT_ITEMS );
  }

  private static void renderListVisible( CCombo ccombo ) {
    renderProperty( ccombo, PROP_LIST_VISIBLE, ccombo.getListVisible(), false );
  }

  private static void renderSelectionIndex( CCombo ccombo ) {
    Integer newValue = Integer.valueOf( ccombo.getSelectionIndex() );
    boolean selectionChanged
      = WidgetLCAUtil.hasChanged( ccombo, PROP_SELECTION_INDEX, newValue, DEFAULT_SELECTION_INDEX );
    // The 'textChanged' statement covers the following use case:
    // ccombo.add( "a" );  ccombo.select( 0 );
    // -- in a subsequent request --
    // ccombo.removeAll();  ccombo.add( "b" );  ccombo.select( 0 );
    // When only examining selectionIndex, a change cannot be determined
    boolean textChanged = !ccombo.getEditable()
                          && hasChanged( ccombo, PROP_TEXT, ccombo.getText(), "" );
    if( selectionChanged || textChanged ) {
      getClientObject( ccombo ).set( PROP_SELECTION_INDEX, newValue.intValue() );
    }
  }

  private static void renderEditable( CCombo ccombo ) {
    renderProperty( ccombo, PROP_EDITABLE, ccombo.getEditable(), true );
  }

  private static void renderText( CCombo ccombo ) {
    renderProperty( ccombo, PROP_TEXT, ccombo.getText(), "" );
  }

  private static void renderSelection( CCombo ccombo ) {
    renderProperty( ccombo, PROP_SELECTION, ccombo.getSelection(), DEFAULT_SELECTION );
  }

  private static void renderTextLimit( CCombo ccombo ) {
    renderProperty( ccombo, PROP_TEXT_LIMIT, getTextLimit( ccombo ), null );
  }

  private static void renderListenSelection( CCombo ccombo ) {
    renderListener( ccombo, PROP_SELECTION_LISTENER, isListening( ccombo, SWT.Selection ), false );
  }

  private static void renderListenDefaultSelection( CCombo ccombo ) {
    renderListener( ccombo,
                    PROP_DEFAULT_SELECTION_LISTENER,
                    isListening( ccombo, SWT.DefaultSelection ),
                    false );
  }

  private static void renderListenModify( CCombo ccombo ) {
    renderListener( ccombo, PROP_MODIFY_LISTENER, hasModifyListener( ccombo ), false );
  }

  private static boolean hasModifyListener( CCombo ccombo ) {
    return isListening( ccombo, SWT.Modify ) || isListening( ccombo, SWT.Verify );
  }

  //////////////////
  // Helping methods

  private static Integer getTextLimit( CCombo ccombo ) {
    Integer result = Integer.valueOf( ccombo.getTextLimit() );
    if( result.intValue() == CCombo.LIMIT  ) {
      result = null;
    }
    return result;
  }
}
