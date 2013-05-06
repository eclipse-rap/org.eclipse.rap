/*******************************************************************************
 * Copyright (c) 2007, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.toolitemkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory.getClientObject;
import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.getStyles;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.hasChanged;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.wasEventSent;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.eclipse.swt.internal.events.EventLCAUtil.isListening;

import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.internal.util.MnemonicUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.internal.widgets.IToolItemAdapter;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;


final class ToolItemLCAUtil {

  private static final String TYPE = "rwt.widgets.ToolItem";
  private static final String[] ALLOWED_STYLES = new String[] {
    "PUSH", "CHECK", "RADIO", "SEPARATOR", "DROP_DOWN"
  };

  private static final String PROP_VISIBLE = "visible";
  private static final String PROP_TEXT = "text";
  private static final String PROP_MNEMONIC_INDEX = "mnemonicIndex";
  private static final String PROP_IMAGE = "image";
  private static final String PROP_HOT_IMAGE = "hotImage";
  private static final String PROP_CONTROL = "control";
  static final String PROP_SELECTION = "selection";
  private static final String PROP_SELECTION_LISTENER = "Selection";

  private ToolItemLCAUtil() {
    // prevent instantiation
  }

  static void preserveValues( ToolItem toolItem ) {
    WidgetLCAUtil.preserveBounds( toolItem, toolItem.getBounds() );
    WidgetLCAUtil.preserveEnabled( toolItem, toolItem.getEnabled() );
    WidgetLCAUtil.preserveToolTipText( toolItem, toolItem.getToolTipText() );
    WidgetLCAUtil.preserveCustomVariant( toolItem );
    WidgetLCAUtil.preserveData( toolItem );
    preserveProperty( toolItem, PROP_VISIBLE, isVisible( toolItem ) );
    preserveProperty( toolItem, PROP_TEXT, toolItem.getText() );
    preserveProperty( toolItem, PROP_IMAGE, getImage( toolItem ) );
    preserveProperty( toolItem, PROP_HOT_IMAGE, toolItem.getHotImage() );
    preserveProperty( toolItem, PROP_CONTROL, toolItem.getControl() );
    preserveProperty( toolItem, PROP_SELECTION, toolItem.getSelection() );
    preserveListener( toolItem, PROP_SELECTION_LISTENER, isListening( toolItem, SWT.Selection ) );
  }

  static void renderInitialization( ToolItem toolItem ) {
    ToolBar toolBar = toolItem.getParent();
    // TODO [tb] For the index, it is currently ignored that controls
    //           attached to a ToolItem use an index-slot of their own on
    //           the client, while they don't on the server. In theory,
    //           this could lead to an incorrect order of the items on the
    //           client, which is problematic with the keyboard-control
    //           and radio-groups.
    IClientObject clientObject = ClientObjectFactory.getClientObject( toolItem );
    clientObject.create( TYPE );
    clientObject.set( "parent", getId( toolItem.getParent() ) );
    clientObject.set( "style", createJsonArray( getStyles( toolItem, ALLOWED_STYLES ) ) );
    clientObject.set( "index", toolBar.indexOf( toolItem ) );
  }

  static void renderChanges( ToolItem toolItem ) {
    WidgetLCAUtil.renderBounds( toolItem, toolItem.getBounds() );
    WidgetLCAUtil.renderEnabled( toolItem, toolItem.getEnabled() );
    WidgetLCAUtil.renderToolTip( toolItem, toolItem.getToolTipText() );
    WidgetLCAUtil.renderCustomVariant( toolItem );
    WidgetLCAUtil.renderData( toolItem );
    renderText( toolItem );
    renderMnemonicIndex( toolItem );
    renderProperty( toolItem, PROP_VISIBLE, isVisible( toolItem ), true );
    renderProperty( toolItem, PROP_IMAGE, getImage( toolItem ), null );
    renderProperty( toolItem, PROP_HOT_IMAGE, toolItem.getHotImage(), null );
    renderProperty( toolItem, PROP_CONTROL, toolItem.getControl(), null );
    renderProperty( toolItem, PROP_SELECTION, toolItem.getSelection(), false );
    renderListener( toolItem,
                    PROP_SELECTION_LISTENER,
                    isListening( toolItem, SWT.Selection ),
                    false );
  }

  ////////////
  // Selection

  static void processSelection( ToolItem toolItem ) {
    String eventName = ClientMessageConst.EVENT_SELECTION;
    if( wasEventSent( toolItem, eventName ) ) {
      Event event = new Event();
      event.stateMask = EventLCAUtil.readStateMask( toolItem, eventName );
      toolItem.notifyListeners( SWT.Selection, event );
    }
  }

  //////////////////
  // Helping methods

  private static boolean isVisible( ToolItem toolItem ) {
    return toolItem.getAdapter( IToolItemAdapter.class ).getVisible();
  }

  private static void renderText( ToolItem toolItem ) {
    String newValue = toolItem.getText();
    if( hasChanged( toolItem, PROP_TEXT, newValue, "" ) ) {
      String text = MnemonicUtil.removeAmpersandControlCharacters( newValue );
      getClientObject( toolItem ).set( PROP_TEXT, text );
    }
  }

  private static void renderMnemonicIndex( ToolItem toolItem ) {
    String text = toolItem.getText();
    if( hasChanged( toolItem, PROP_TEXT, text, "" ) ) {
      int mnemonicIndex = MnemonicUtil.findMnemonicCharacterIndex( text );
      if( mnemonicIndex != -1 ) {
        getClientObject( toolItem ).set( PROP_MNEMONIC_INDEX, mnemonicIndex );
      }
    }
  }

  static Image getImage( ToolItem toolItem ) {
    Image result;
    if( toolItem.getEnabled() && toolItem.getParent().getEnabled() ) {
      result = toolItem.getImage();
    } else {
      result = toolItem.getDisabledImage();
      if( result == null ) {
        result = toolItem.getImage();
      }
    }
    return result;
  }
}
