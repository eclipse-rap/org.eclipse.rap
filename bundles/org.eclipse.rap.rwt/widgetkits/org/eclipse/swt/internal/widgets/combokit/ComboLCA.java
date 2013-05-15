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
package org.eclipse.swt.internal.widgets.combokit;

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

import java.io.IOException;

import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.internal.textsize.TextSizeUtil;
import org.eclipse.rap.rwt.internal.theme.IThemeAdapter;
import org.eclipse.rap.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Widget;


public class ComboLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Combo";
  private static final String[] ALLOWED_STYLES = new String[] { "DROP_DOWN", "SIMPLE", "BORDER" };

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
  private static final Integer DEFAULT_SELECTION_INDEX = Integer.valueOf( -1 );
  private static final Point DEFAULT_SELECTION = new Point( 0, 0 );
  private static final int DEFAULT_VISIBLE_ITEM_COUNT = 5;

  @Override
  public void preserveValues( Widget widget ) {
    Combo combo = ( Combo )widget;
    ControlLCAUtil.preserveValues( combo );
    WidgetLCAUtil.preserveCustomVariant( combo );
    preserveProperty( combo, PROP_ITEMS, combo.getItems() );
    preserveProperty( combo, PROP_SELECTION_INDEX, Integer.valueOf( combo.getSelectionIndex() ) );
    preserveProperty( combo, PROP_SELECTION, combo.getSelection() );
    preserveProperty( combo, PROP_TEXT_LIMIT, getTextLimit( combo ) );
    preserveProperty( combo, PROP_VISIBLE_ITEM_COUNT, combo.getVisibleItemCount() );
    preserveProperty( combo, PROP_ITEM_HEIGHT, getItemHeight( combo ) );
    preserveProperty( combo, PROP_TEXT, combo.getText() );
    preserveProperty( combo, PROP_LIST_VISIBLE, combo.getListVisible() );
    preserveProperty( combo, PROP_EDITABLE, Boolean.valueOf( isEditable( combo ) ) );
    preserveListener( combo, PROP_SELECTION_LISTENER, isListening( combo, SWT.Selection ) );
    preserveListener( combo,
                      PROP_DEFAULT_SELECTION_LISTENER,
                      isListening( combo, SWT.DefaultSelection ) );
    preserveListener( combo, PROP_MODIFY_LISTENER, hasModifyListener( combo ) );
  }

  public void readData( Widget widget ) {
    Combo combo = ( Combo )widget;
    String value = readPropertyValue( widget, "selectionIndex" );
    if( value != null ) {
      combo.select( NumberFormatUtil.parseInt( value ) );
    }
    String listVisible = readPropertyValue( combo, "listVisible" );
    if( listVisible != null ) {
      combo.setListVisible( Boolean.valueOf( listVisible ).booleanValue() );
    }
    readTextAndSelection( combo );
    ControlLCAUtil.processSelection( combo, null, true );
    ControlLCAUtil.processDefaultSelection( combo, null );
    ControlLCAUtil.processEvents( combo );
    ControlLCAUtil.processKeyEvents( combo );
    ControlLCAUtil.processMenuDetect( combo );
    WidgetLCAUtil.processHelp( combo );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    Combo combo = ( Combo )widget;
    IClientObject clientObject = getClientObject( combo );
    clientObject.create( TYPE );
    clientObject.set( "parent", getId( combo.getParent() ) );
    clientObject.set( "style", createJsonArray( getStyles( combo, ALLOWED_STYLES ) ) );
  }

  @Override
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
    renderListenDefaultSelection( combo );
    renderListenModify( combo );
  }

  ///////////////////////////////////////
  // Helping methods to read client state

  private static void readTextAndSelection( final Combo combo ) {
    final Point selection = readSelection( combo );
    final String value = readPropertyValue( combo, "text" );
    if( value != null ) {
      if( isListening( combo, SWT.Verify ) ) {
        // setText needs to be executed in a ProcessAcction runnable as it may
        // fire a VerifyEvent whose fields (text and doit) need to be evaluated
        // before actually setting the new value
        ProcessActionRunner.add( new Runnable() {
          public void run() {
            combo.setText( value );
            // since text is set in process action, preserved values have to be
            // replaced
            WidgetAdapter adapter = WidgetUtil.getAdapter( combo );
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
    String selStart = readPropertyValue( combo, "selectionStart" );
    String selLength = readPropertyValue( combo, "selectionLength" );
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
    Integer newValue = Integer.valueOf( getItemHeight( combo ) );
    if( hasChanged( combo, PROP_ITEM_HEIGHT, newValue ) ) {
      getClientObject( combo ).set( PROP_ITEM_HEIGHT, newValue.intValue() );
    }
  }

  private static void renderVisibleItemCount( Combo combo ) {
    int defValue = DEFAULT_VISIBLE_ITEM_COUNT;
    renderProperty( combo, PROP_VISIBLE_ITEM_COUNT, combo.getVisibleItemCount(), defValue );
  }

  private static void renderItems( Combo combo ) {
    renderProperty( combo, PROP_ITEMS, combo.getItems(), DEFAUT_ITEMS );
  }

  private static void renderListVisible( Combo combo ) {
    renderProperty( combo, PROP_LIST_VISIBLE, combo.getListVisible(), false );
  }

  private static void renderSelectionIndex( Combo combo ) {
    Integer newValue = Integer.valueOf( combo.getSelectionIndex() );
    boolean selectionChanged
      = hasChanged( combo, PROP_SELECTION_INDEX, newValue, DEFAULT_SELECTION_INDEX );
    // The 'textChanged' statement covers the following use case:
    // combo.add( "a" );  combo.select( 0 );
    // -- in a subsequent request --
    // combo.removeAll();  combo.add( "b" );  combo.select( 0 );
    // When only examining selectionIndex, a change cannot be determined
    boolean textChanged
      = !isEditable( combo ) && hasChanged( combo, PROP_TEXT, combo.getText(), "" );
    if( selectionChanged || textChanged ) {
      getClientObject( combo ).set( PROP_SELECTION_INDEX, newValue.intValue() );
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
    renderProperty( combo, PROP_SELECTION, combo.getSelection(), DEFAULT_SELECTION );
  }

  private static void renderTextLimit( Combo combo ) {
    renderProperty( combo, PROP_TEXT_LIMIT, getTextLimit( combo ), null );
  }

  private static void renderListenSelection( Combo combo ) {
    renderListener( combo, PROP_SELECTION_LISTENER, isListening( combo, SWT.Selection ), false );
  }

  private static void renderListenDefaultSelection( Combo combo ) {
    renderListener( combo,
                    PROP_DEFAULT_SELECTION_LISTENER,
                    isListening( combo, SWT.DefaultSelection ),
                    false );
  }

  private static void renderListenModify( Combo combo ) {
    renderListener( combo, PROP_MODIFY_LISTENER, hasModifyListener( combo ), false );
  }

  private static boolean hasModifyListener( Combo combo ) {
    return isListening( combo, SWT.Modify ) || isListening( combo, SWT.Verify );
  }

  //////////////////
  // Helping methods

  private static boolean isEditable( Combo combo ) {
    return ( ( combo.getStyle() & SWT.READ_ONLY ) == 0 );
  }

  private static int getItemHeight( Combo combo ) {
    return TextSizeUtil.getCharHeight( combo.getFont() ) + getListItemPadding( combo ).height;
  }

  private static Rectangle getListItemPadding( Combo combo ) {
    ComboThemeAdapter themeAdapter = ( ComboThemeAdapter )combo.getAdapter( IThemeAdapter.class );
    return themeAdapter.getListItemPadding( combo );
  }

  private static Integer getTextLimit( Combo combo ) {
    Integer result = Integer.valueOf( combo.getTextLimit() );
    if( result.intValue() == Combo.LIMIT  ) {
      result = null;
    }
    return result;
  }
}
