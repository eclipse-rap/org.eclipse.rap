/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.custom.ctabfolderkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_DETAIL;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_ITEM;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.readEventPropertyValue;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.wasEventSent;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.custom.ICTabFolderAdapter;
import org.eclipse.swt.internal.events.EventTypes;
import org.eclipse.swt.internal.widgets.IWidgetGraphicsAdapter;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;


public final class CTabFolderLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.CTabFolder";
  private static final String[] ALLOWED_STYLES = new String[] {
    "CLOSE", "FLAT", "SINGLE", "MULTI", "NO_RADIO_GROUP", "BORDER"
  };

  // Request parameter that denotes the id of the selected tab item
  public static final String PARAM_SELECTION = "selection";
  // Request parameters for min/max state
  public static final String PARAM_MAXIMIZED = "maximized";
  public static final String PARAM_MINIMIZED = "minimized";

  // Property names
  private static final String PROP_TOOLTIP_TEXTS = "toolTipTexts";
  private static final String PROP_TAB_POSITION = "tabPosition";
  private static final String PROP_TAB_HEIGHT = "tabHeight";
  private static final String PROP_MIN_MAX_STATE = "minMaxState";
  private static final String PROP_MINIMIZE_BOUNDS = "minimizeBounds";
  private static final String PROP_MINIMIZE_VISIBLE = "minimizeVisible";
  private static final String PROP_MAXIMIZE_BOUNDS = "maximizeBounds";
  private static final String PROP_MAXIMIZE_VISIBLE = "maximizeVisible";
  private static final String PROP_CHEVRON_BOUNDS = "chevronBounds";
  private static final String PROP_CHEVRON_VISIBLE = "chevronVisible";
  private static final String PROP_UNSELECTED_CLOSE_VISIBLE = "unselectedCloseVisible";
  private static final String PROP_SELECTION = "selection";
  private static final String PROP_SELECTION_BACKGROUND = "selectionBackground";
  private static final String PROP_SELECTION_FOREGROUND = "selectionForeground";
  private static final String PROP_SELECTION_BACKGROUND_IMAGE = "selectionBackgroundImage";
  private static final String PROP_SELECTION_BG_GRADIENT = "selectionBackgroundGradient";
  private static final String PROP_SELECTION_BG_GRADIENT_COLORS
    = "selectionBgGradientColors";
  private static final String PROP_SELECTION_BG_GRADIENT_PERCENTS
    = "selectionBgGradientPercents";
  private static final String PROP_SELECTION_BG_GRADIENT_VERTICAL
    = "selectionBgGradientVertical";
  private static final String PROP_BORDER_VISIBLE = "borderVisible";
  private static final String PROP_FOLDER_LISTENER = "Folder";
  private static final String PROP_SELECTION_LISTENER = "Selection";
  private static final String PROP_DEFAULT_SELECTION_LISTENER = "DefaultSelection";

  private static final String DEFAULT_TAB_POSITION = "top";
  private static final int DEFAULT_TAB_HEIGHT = 0;
  private static final String DEFAULT_MIN_MAX_STATE = "normal";
  private static final Rectangle ZERO_BOUNDS = new Rectangle( 0, 0, 0, 0 );

  @Override
  public void preserveValues( Widget widget ) {
    CTabFolder folder = ( CTabFolder )widget;
    ControlLCAUtil.preserveValues( folder );
    WidgetLCAUtil.preserveCustomVariant( folder );
    preserveProperty( folder, PROP_TAB_POSITION, getTabPosition( folder ) );
    preserveProperty( folder, PROP_TAB_HEIGHT, folder.getTabHeight() );
    preserveProperty( folder, PROP_MIN_MAX_STATE, getMinMaxState( folder ) );
    preserveProperty( folder, PROP_MINIMIZE_BOUNDS, getMinimizeBounds( folder ) );
    preserveProperty( folder, PROP_MINIMIZE_VISIBLE, folder.getMinimizeVisible() );
    preserveProperty( folder, PROP_MAXIMIZE_BOUNDS, getMaximizeBounds( folder ) );
    preserveProperty( folder, PROP_MAXIMIZE_VISIBLE, folder.getMaximizeVisible() );
    preserveProperty( folder, PROP_CHEVRON_BOUNDS, getChevronBounds( folder ) );
    preserveProperty( folder, PROP_CHEVRON_VISIBLE, getChevronVisible( folder ) );
    preserveProperty( folder, PROP_UNSELECTED_CLOSE_VISIBLE, folder.getUnselectedCloseVisible() );
    preserveProperty( folder, PROP_SELECTION, folder.getSelection() );
    preserveProperty( folder, PROP_SELECTION_BACKGROUND, getSelectionBackground( folder ) );
    preserveProperty( folder, PROP_SELECTION_FOREGROUND, getSelectionForeground( folder ) );
    preserveProperty( folder,
                      PROP_SELECTION_BACKGROUND_IMAGE,
                      getSelectionBackgroundImage( folder ) );
    preserveSelectionBgGradient( folder );
    preserveProperty( folder, PROP_BORDER_VISIBLE, folder.getBorderVisible() );
    preserveListener( folder, PROP_SELECTION_LISTENER, folder.isListening( SWT.Selection ) );
    preserveListener( folder,
                      PROP_DEFAULT_SELECTION_LISTENER,
                      folder.isListening( SWT.DefaultSelection ) );
    preserveListener( folder, PROP_FOLDER_LISTENER, hasFolderListener( folder ) );
  }

  public void readData( Widget widget ) {
    final CTabFolder folder = ( CTabFolder )widget;
    String value = WidgetLCAUtil.readPropertyValue( folder, PARAM_MINIMIZED );
    if( value != null ) {
      folder.setMinimized( Boolean.valueOf( value ).booleanValue() );
    }
    value = WidgetLCAUtil.readPropertyValue( folder, PARAM_MAXIMIZED );
    if( value != null ) {
      folder.setMaximized( Boolean.valueOf( value ).booleanValue() );
    }
    processFolderEvent( folder );
    // TODO [rh] it's a hack: necessary because folder.setSelection changes
    //      the visibility of tabItem.control; but preserveValues stores
    //      the already changed visibility and thus no visibility property is rendered
    String selectedItemId = WidgetLCAUtil.readPropertyValue( folder, PARAM_SELECTION );
    if( selectedItemId != null ) {
      final CTabItem item = ( CTabItem )WidgetUtil.find( folder, selectedItemId );
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          folder.setSelection( item );
          preserveProperty( folder, PROP_SELECTION, folder.getSelection() );
          ControlLCAUtil.processSelection( folder, item, false );
          ControlLCAUtil.processDefaultSelection( folder, item );
        }
      } );
    }
    ControlLCAUtil.processEvents( folder );
    ControlLCAUtil.processKeyEvents( folder );
    ControlLCAUtil.processMenuDetect( folder );
    WidgetLCAUtil.processHelp( folder );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    CTabFolder folder = ( CTabFolder )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( folder );
    clientObject.create( TYPE );
    clientObject.set( "parent", WidgetUtil.getId( folder.getParent() ) );
    clientObject.set( "style", WidgetLCAUtil.getStyles( folder, ALLOWED_STYLES ) );
    String[] toolTipTexts = new String[] {
      SWT.getMessage( "SWT_Minimize" ),
      SWT.getMessage( "SWT_Maximize" ),
      SWT.getMessage( "SWT_Restore" ),
      SWT.getMessage( "SWT_ShowList" ),
      SWT.getMessage( "SWT_Close" ),
    };
    clientObject.set( PROP_TOOLTIP_TEXTS, toolTipTexts );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    CTabFolder folder = ( CTabFolder )widget;
    ControlLCAUtil.renderChanges( folder );
    WidgetLCAUtil.renderCustomVariant( folder );
    renderProperty( folder, PROP_TAB_POSITION, getTabPosition( folder ), DEFAULT_TAB_POSITION );
    renderProperty( folder, PROP_TAB_HEIGHT, folder.getTabHeight(), DEFAULT_TAB_HEIGHT );
    renderProperty( folder, PROP_MIN_MAX_STATE, getMinMaxState( folder ), DEFAULT_MIN_MAX_STATE );
    renderProperty( folder, PROP_MINIMIZE_BOUNDS, getMinimizeBounds( folder ), ZERO_BOUNDS );
    renderProperty( folder, PROP_MINIMIZE_VISIBLE, folder.getMinimizeVisible(), false );
    renderProperty( folder, PROP_MAXIMIZE_BOUNDS, getMaximizeBounds( folder ), ZERO_BOUNDS );
    renderProperty( folder, PROP_MAXIMIZE_VISIBLE, folder.getMaximizeVisible(), false );
    renderProperty( folder, PROP_CHEVRON_BOUNDS, getChevronBounds( folder ), ZERO_BOUNDS );
    renderProperty( folder, PROP_CHEVRON_VISIBLE, getChevronVisible( folder ), false );
    renderProperty( folder,
                    PROP_UNSELECTED_CLOSE_VISIBLE,
                    folder.getUnselectedCloseVisible(),
                    true );
    renderProperty( folder, PROP_SELECTION, folder.getSelection(), null );
    renderProperty( folder, PROP_SELECTION_BACKGROUND, getSelectionBackground( folder ), null );
    renderProperty( folder, PROP_SELECTION_FOREGROUND, getSelectionForeground( folder ), null );
    renderProperty( folder,
                    PROP_SELECTION_BACKGROUND_IMAGE,
                    getSelectionBackgroundImage( folder ),
                    null);
    renderSelectionBackgroundGradient( folder );
    renderProperty( folder, PROP_BORDER_VISIBLE, folder.getBorderVisible(), false );
    renderListener( folder, PROP_SELECTION_LISTENER, folder.isListening( SWT.Selection ), false );
    renderListener( folder, PROP_DEFAULT_SELECTION_LISTENER,
                    folder.isListening( SWT.DefaultSelection ),
                    false );
    renderListener( folder, PROP_FOLDER_LISTENER, hasFolderListener( folder ), false );
  }

  /////////////////////////////////////////
  // Helping methods to preserve properties

  private static void preserveSelectionBgGradient( CTabFolder folder ) {
    ICTabFolderAdapter adapter = getCTabFolderAdapter( folder );
    IWidgetGraphicsAdapter gfxAdapter = adapter.getUserSelectionBackgroundGradient();
    Color[] bgGradientColors = gfxAdapter.getBackgroundGradientColors();
    int[] bgGradientPercents = gfxAdapter.getBackgroundGradientPercents();
    Boolean bgGradientVertical = Boolean.valueOf( gfxAdapter.isBackgroundGradientVertical() );
    preserveProperty( folder, PROP_SELECTION_BG_GRADIENT_COLORS, bgGradientColors );
    preserveProperty( folder, PROP_SELECTION_BG_GRADIENT_PERCENTS, bgGradientPercents );
    preserveProperty( folder, PROP_SELECTION_BG_GRADIENT_VERTICAL, bgGradientVertical );
  }

  //////////////////////////////////////
  // Helping methods to write properties

  private static void renderSelectionBackgroundGradient( CTabFolder folder ) {
    ICTabFolderAdapter adapter = getCTabFolderAdapter( folder );
    IWidgetGraphicsAdapter gfxAdapter = adapter.getUserSelectionBackgroundGradient();
    Color[] bgGradientColors = gfxAdapter.getBackgroundGradientColors();
    int[] bgGradientPercents = gfxAdapter.getBackgroundGradientPercents();
    Boolean bgGradientVertical = Boolean.valueOf( gfxAdapter.isBackgroundGradientVertical() );
    boolean hasChanged = WidgetLCAUtil.hasChanged( folder,
                                                   PROP_SELECTION_BG_GRADIENT_COLORS,
                                                   bgGradientColors,
                                                   null )
                      || WidgetLCAUtil.hasChanged( folder,
                                                   PROP_SELECTION_BG_GRADIENT_PERCENTS,
                                                   bgGradientPercents,
                                                   null )
                      || WidgetLCAUtil.hasChanged( folder,
                                                   PROP_SELECTION_BG_GRADIENT_VERTICAL,
                                                   bgGradientVertical,
                                                   Boolean.FALSE );
    if( hasChanged ) {
      Object gradient = null;
      if( bgGradientColors!= null ) {
        Object[] colors = new Object[ bgGradientColors.length ];
        Integer[] percents = new Integer[ bgGradientPercents.length ];
        for( int i = 0; i < colors.length; i++ ) {
          colors[ i ] = ProtocolUtil.getColorAsArray( bgGradientColors[ i ], false );
        }
        for( int i = 0; i < bgGradientPercents.length; i++ ) {
          percents[ i ] =  Integer.valueOf( bgGradientPercents[ i ] );
        }
        gradient = new Object[] { colors, percents, bgGradientVertical };
      }
      IClientObject clientObject = ClientObjectFactory.getClientObject( folder );
      clientObject.set( PROP_SELECTION_BG_GRADIENT, gradient );
    }
  }

  ///////////////
  // Event helper

  private static void processFolderEvent( CTabFolder folder ) {
    String eventName = ClientMessageConst.EVENT_FOLDER;
    if( wasEventSent( folder, eventName ) ) {
      String detail = readEventPropertyValue( folder, eventName, EVENT_PARAM_DETAIL );
      if( ClientMessageConst.EVENT_FOLDER_DETAIL_MINIMIZE.equals( detail ) ) {
        folder.notifyListeners( EventTypes.CTAB_FOLDER_MINIMIZE, new Event() );
      } else if( ClientMessageConst.EVENT_FOLDER_DETAIL_MAXIMIZE.equals( detail ) ) {
        folder.notifyListeners( EventTypes.CTAB_FOLDER_MAXIMIZE, new Event() );
      } else if( ClientMessageConst.EVENT_FOLDER_DETAIL_RESTORE.equals( detail ) ) {
        folder.notifyListeners( EventTypes.CTAB_FOLDER_RESTORE, new Event() );
      } else if( ClientMessageConst.EVENT_FOLDER_DETAIL_CLOSE.equals( detail ) ) {
        String itemId = readEventPropertyValue( folder, eventName, EVENT_PARAM_ITEM );
        CTabItem item = ( CTabItem )WidgetUtil.find( folder, itemId );
        notifyCloseListeners( item );
      } else if( ClientMessageConst.EVENT_FOLDER_DETAIL_SHOW_LIST.equals( detail ) ) {
        notifyShowListListeners( folder );
      }
    }
  }

  private static void notifyCloseListeners( final CTabItem item ) {
    ProcessActionRunner.add( new Runnable() {
      public void run() {
        boolean doit = sendCloseEvent( item );
        if( doit ) {
          item.dispose();
        }
      }
    } );
  }

  private static void notifyShowListListeners( final CTabFolder folder ) {
    ProcessActionRunner.add( new Runnable() {
      public void run() {
        boolean doit = sendShowListEvent( folder );
        if( doit ) {
          ICTabFolderAdapter adapter = getCTabFolderAdapter( folder );
          adapter.showListMenu();
        }
      }
    } );
  }

  private static boolean sendCloseEvent( CTabItem item ) {
    Event event = new Event();
    event.item = item;
    event.doit = true;
    item.getParent().notifyListeners( EventTypes.CTAB_FOLDER_CLOSE, event );
    return event.doit;
  }

  private static boolean sendShowListEvent( CTabFolder folder ) {
    Event event = new Event();
    Rectangle chevronRect = getChevronBounds( folder );
    event.x = chevronRect.x;
    event.y = chevronRect.y;
    event.height = chevronRect.height;
    event.width = chevronRect.width;
    event.doit = true;
    folder.notifyListeners( EventTypes.CTAB_FOLDER_SHOW_LIST, event );
    return event.doit;
  }

  //////////////////
  // Helping methods

  private static String getTabPosition( CTabFolder folder ) {
    return folder.getTabPosition() == SWT.TOP ? "top" : "bottom";
  }

  private static String getMinMaxState( CTabFolder folder ) {
    String result = "normal";
    if( folder.getMinimized() ) {
      result = "min";
    } else if( folder.getMaximized() ) {
      result = "max";
    }
    return result;
  }

  private static Rectangle getMinimizeBounds( CTabFolder folder ) {
    return getCTabFolderAdapter( folder ).getMinimizeRect();
  }

  private static Rectangle getMaximizeBounds( CTabFolder folder ) {
    return getCTabFolderAdapter( folder ).getMaximizeRect();
  }

  private static Rectangle getChevronBounds( CTabFolder folder ) {
    return getCTabFolderAdapter( folder ).getChevronRect();
  }

  private static boolean getChevronVisible( CTabFolder folder ) {
    return getCTabFolderAdapter( folder ).getChevronVisible();
  }

  private static Color getSelectionBackground( CTabFolder folder ) {
    return getCTabFolderAdapter( folder ).getUserSelectionBackground();
  }

  private static Color getSelectionForeground( CTabFolder folder ) {
    return getCTabFolderAdapter( folder ).getUserSelectionForeground();
  }

  private static Image getSelectionBackgroundImage( CTabFolder folder ) {
    return getCTabFolderAdapter( folder ).getUserSelectionBackgroundImage();
  }

  private static ICTabFolderAdapter getCTabFolderAdapter( CTabFolder folder ) {
    return folder.getAdapter( ICTabFolderAdapter.class );
  }

  private boolean hasFolderListener( CTabFolder folder ) {
    return folder.isListening( EventTypes.CTAB_FOLDER_CLOSE )
        || folder.isListening( EventTypes.CTAB_FOLDER_MAXIMIZE )
        || folder.isListening( EventTypes.CTAB_FOLDER_MINIMIZE )
        || folder.isListening( EventTypes.CTAB_FOLDER_RESTORE )
        || folder.isListening( EventTypes.CTAB_FOLDER_SHOW_LIST );
  }

}
