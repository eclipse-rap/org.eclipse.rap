/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.combokit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Widget;

/**
 * Life cycle adapter for Combo widgets.
 */
public class ComboLCA extends AbstractWidgetLCA {

  private static final String[] DEFAUT_ITEMS = new String[ 0 ];
  private static final Integer DEFAULT_SELECTION = new Integer( -1 );
  private static final Point DEFAULT_TEXT_SELECTION = new Point( 0, 0 );
  private static final Integer DEFAULT_TEXT_LIMIT = new Integer( Combo.LIMIT );

  // Must be in sync with appearance "list-item"
  private static final int LIST_ITEM_PADDING = 3;

  // Constants for JS functions names
  private static final String JS_FUNC_SELECT = "select";
  private static final String JS_FUNC_SET_SELECTION_TEXT = "setTextSelection";

  // Property names for preserve-value facility
  static final String PROP_ITEMS = "items";
  static final String PROP_TEXT = "text";
  static final String PROP_SELECTION = "selection";
  static final String PROP_TEXT_SELECTION = "textSelection";
  static final String PROP_TEXT_LIMIT = "textLimit";
  static final String PROP_EDITABLE = "editable";
  static final String PROP_VERIFY_MODIFY_LISTENER = "verifyModifyListener";
  static final String PROP_MAX_LIST_HEIGHT = "maxListHeight";
  static final String PROP_LIST_ITEM_HEIGHT = "listItemHeight";

  public void preserveValues( final Widget widget ) {
    Combo combo = ( Combo )widget;
    ControlLCAUtil.preserveValues( combo );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    String[] items = combo.getItems();
    adapter.preserve( PROP_ITEMS, items );
    Integer selection = new Integer( combo.getSelectionIndex() );
    adapter.preserve( PROP_SELECTION, selection );
    adapter.preserve( PROP_TEXT_SELECTION, combo.getSelection() );
    adapter.preserve( PROP_TEXT_LIMIT,
                      new Integer( combo.getTextLimit() ) );
    adapter.preserve( PROP_MAX_LIST_HEIGHT,
                      new Integer( getMaxListHeight( combo ) ) );
    adapter.preserve( PROP_LIST_ITEM_HEIGHT,
                      new Integer( getListItemHeight( combo ) ) );
    adapter.preserve( PROP_TEXT, combo.getText() );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( SelectionEvent.hasListener( combo ) ) );
    adapter.preserve( PROP_EDITABLE, Boolean.valueOf( isEditable( combo ) ) );
    boolean hasVerifyListener = VerifyEvent.hasListener( combo );
    boolean hasModifyListener = ModifyEvent.hasListener( combo );
    boolean hasListener = hasVerifyListener || hasModifyListener;
    adapter.preserve( PROP_VERIFY_MODIFY_LISTENER,
                      Boolean.valueOf( hasListener ) );
    WidgetLCAUtil.preserveCustomVariant( combo );
  }

  public void readData( final Widget widget ) {
    final Combo combo = ( Combo )widget;
    String value = WidgetLCAUtil.readPropertyValue( widget, "selectedItem" );
    if( value != null ) {
      combo.select( Integer.parseInt( value ) );
    }
    readTextAndSelection( combo );
    ControlLCAUtil.processSelection( combo, null, true );
    ControlLCAUtil.processMouseEvents( combo );
    ControlLCAUtil.processKeyEvents( combo );
    WidgetLCAUtil.processHelp( combo );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    Combo combo = ( Combo )widget;
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.newWidget( "org.eclipse.swt.widgets.Combo" );
    ControlLCAUtil.writeStyleFlags( combo );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Combo combo = ( Combo )widget;
    ControlLCAUtil.writeChanges( combo );
    writeListItemHeight( combo );
    writeItems( combo );
    writeSelectionListener( combo );
    writeSelection( combo );
    writeMaxListHeight( combo );
    writeEditable( combo );
    writeText( combo );
    writeTextSelection( combo );
    writeTextLimit( combo );
    writeVerifyAndModifyListener( combo );
    WidgetLCAUtil.writeCustomVariant( combo );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
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
              adapter.preserve( PROP_TEXT_SELECTION, selection );
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
  
  private static Point readSelection( final Combo combo ) {
    Point result = null;
    String selStart = WidgetLCAUtil.readPropertyValue( combo,
                                                       "selectionStart" );
    String selLength = WidgetLCAUtil.readPropertyValue( combo,
                                                        "selectionLength" );
    if( selStart != null || selLength != null ) {
      result = new Point( 0, 0 );
      if( selStart != null ) {
        result.x = Integer.parseInt( selStart );
      }
      if( selLength != null ) {
        result.y = result.x + Integer.parseInt( selLength );
      }
    }
    return result;
  }

  //////////////////////////////////////////////
  // Helping methods to write changed properties

  private static void writeItems( final Combo combo ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( combo );
    String[] items = combo.getItems();
    if( WidgetLCAUtil.hasChanged( combo, PROP_ITEMS, items, DEFAUT_ITEMS ) ) {
      // Convert newlines into whitespaces
      for( int i = 0; i < items.length; i++ ) {
        items[ i ] = WidgetLCAUtil.replaceNewLines( items[ i ], " " );
        items[ i ] = WidgetLCAUtil.escapeText( items[ i ], false );
      }
      writer.set( PROP_ITEMS, new Object[] { items } );
    }
  }

  private static void writeSelection( final Combo combo ) throws IOException {
    Integer newValue = new Integer( combo.getSelectionIndex() );
    Integer defValue = DEFAULT_SELECTION;
    boolean selectionChanged
      = WidgetLCAUtil.hasChanged( combo, PROP_SELECTION, newValue, defValue );
    // The 'textChanged' statement covers the following use case:
    // combo.add( "a" );  combo.select( 0 );
    // -- in a subsequent request --
    // combo.removeAll();  combo.add( "b" );  combo.select( 0 );
    // When only examining selectionIndex, a change cannot be determined
    boolean textChanged = !isEditable( combo )
                          && WidgetLCAUtil.hasChanged( combo, 
                                                       PROP_TEXT, 
                                                       combo.getText(), 
                                                       "" );
    if( selectionChanged || textChanged ) {
      JSWriter writer = JSWriter.getWriterFor( combo );
      writer.call( JS_FUNC_SELECT, new Object[] { newValue } );
    }
  }
  
  private static void writeTextSelection( final Combo combo )
    throws IOException
  {
    Point newValue = combo.getSelection();
    Point defValue = DEFAULT_TEXT_SELECTION;
    Integer start = new Integer( newValue.x );
    Integer end = new Integer( newValue.y );
    Integer count = new Integer( end.intValue() - start.intValue() );
    // TODO [rh] could be optimized: when text was changed and selection is 0,0
    //      there is no need to write JavaScript since the client resets the
    //      selection as well when the new text is set.
    if( WidgetLCAUtil.hasChanged( combo,
                                  PROP_TEXT_SELECTION,
                                  newValue,
                                  defValue ) ) {
      // [rh] Workaround for bug 252462: Changing selection on a hidden text
      // widget causes exception in FF
      if( combo.isVisible() ) {
        JSWriter writer = JSWriter.getWriterFor( combo );
        writer.call( JS_FUNC_SET_SELECTION_TEXT,
                     new Object[] { start, count } );
      }
    }
  }
  
  private static void writeTextLimit( final Combo combo )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( combo );
    Integer newValue = new Integer( combo.getTextLimit() );
    Integer defValue = DEFAULT_TEXT_LIMIT;
    if( WidgetLCAUtil.hasChanged( combo,
                                  PROP_TEXT_LIMIT,
                                  newValue,
                                  defValue ) )
    {
      // Negative values are treated as 'no limit' which is achieved by passing
      // null to the client-side textLimit property
      if( newValue.intValue() < 0 ) {
        newValue = null;
      }
      writer.set( "textLimit", newValue );
    }
  }

  private static void writeListItemHeight( final Combo combo )
    throws IOException
    {
      Integer newValue = new Integer( getListItemHeight( combo ) );
      if( WidgetLCAUtil.hasChanged( combo,
                                    PROP_LIST_ITEM_HEIGHT,
                                    newValue ) )
      {
        JSWriter writer = JSWriter.getWriterFor( combo );
        writer.set( PROP_LIST_ITEM_HEIGHT, "listItemHeight", newValue );
      }
  }
  
  private static void writeMaxListHeight( final Combo combo )
    throws IOException
  {
    Integer newValue = new Integer( getMaxListHeight( combo ) );
    if( WidgetLCAUtil.hasChanged( combo,
                                  PROP_MAX_LIST_HEIGHT,
                                  newValue ) )
    {
      JSWriter writer = JSWriter.getWriterFor( combo );
      writer.set( PROP_MAX_LIST_HEIGHT, "maxListHeight", newValue );
    }
  }

  private static void writeEditable( final Combo combo ) throws IOException {
    boolean editable = isEditable( combo );
    Boolean newValue = Boolean.valueOf( editable );
    if( WidgetLCAUtil.hasChanged( combo, PROP_EDITABLE, newValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( combo );
      writer.set( PROP_EDITABLE, "editable", newValue, null );
    }
  }

  private static void writeText( final Combo combo ) throws IOException {
    if( isEditable( combo ) ) {
      JSWriter writer = JSWriter.getWriterFor( combo );
      writer.set( PROP_TEXT, "value", combo.getText(), "" );
    }
  }

  private static void writeSelectionListener( final Combo combo )
    throws IOException
  {
    boolean hasListener = SelectionEvent.hasListener( combo );
    Boolean newValue = Boolean.valueOf( hasListener );
    String prop = Props.SELECTION_LISTENERS;
    if( WidgetLCAUtil.hasChanged( combo, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( combo );
      writer.set( "hasSelectionListener", combo );
    }
  }

  private static void writeVerifyAndModifyListener( final Combo combo )
    throws IOException
  {
    boolean hasVerifyListener = VerifyEvent.hasListener( combo );
    boolean hasModifyListener = ModifyEvent.hasListener( combo );
    boolean hasListener = hasVerifyListener || hasModifyListener;
    Boolean newValue = Boolean.valueOf( hasListener );
    String prop = PROP_VERIFY_MODIFY_LISTENER;
    if( WidgetLCAUtil.hasChanged( combo, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( combo );
      writer.set( "hasVerifyModifyListener", combo );
    }
  }
  
  //////////////////
  // Helping methods

  private static boolean isEditable( final Combo combo ) {
    return ( ( combo.getStyle() & SWT.READ_ONLY ) == 0 );
  }

  static int getMaxListHeight( final Combo combo ) {
    int visibleItemCount = combo.getVisibleItemCount();
    int itemHeight = getListItemHeight( combo );
    return visibleItemCount * itemHeight;
  }
  
  static int getListItemHeight( final Combo combo ) {
    int charHeight = TextSizeDetermination.getCharHeight( combo.getFont() );
    int padding = 2 * LIST_ITEM_PADDING;
    return charHeight + padding;
  }
}
