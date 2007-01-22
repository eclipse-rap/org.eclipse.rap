/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.internal.custom.ctabfolderkit;

import java.io.IOException;
import org.eclipse.rap.rwt.custom.*;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Color;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.custom.ICTabFolderAdapter;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.resources.ResourceManager;
import org.eclipse.rap.rwt.widgets.Widget;
import com.w4t.IResourceManager;


public final class CTabFolderLCA extends AbstractWidgetLCA {
  
  // Request parameter that denotes the id of the selected tab item
  public static final String PARAM_SELECTED_ITEM_ID = "selectedItemId";
  // Request parameters for min/max state
  public static final String PARAM_MAXIMIZED = "maximized";
  public static final String PARAM_MINIMIZED = "minimized";

  // Request parameters that denote CTabFolderEvents
  public static final String EVENT_FOLDER_MINIMIZED
    = "org.eclipse.rap.rwt.events.ctabFolderMinimized";
  public static final String EVENT_FOLDER_MAXIMIZED
    = "org.eclipse.rap.rwt.events.ctabFolderMaximized";
  public static final String EVENT_FOLDER_RESTORED
    = "org.eclipse.rap.rwt.events.ctabFolderRestored";
  public static final String EVENT_SHOW_LIST
    = "org.eclipse.rap.rwt.events.ctabFolderShowList";
  
  // Property names for preserveValues
  public static final String PROP_SELECTION_INDEX = "selectionIndex";
  public static final String PROP_MAXIMIZED = "maximized";
  public static final String PROP_MINIMIZED = "minimized";
  public static final String PROP_MINIMIZE_VISIBLE = "minimizeVisible";
  public static final String PROP_MAXIMIZE_VISIBLE = "maximizeVisible";
  public static final String PROP_MINIMIZE_RECT = "minimizeRect";
  public static final String PROP_MAXIMIZE_RECT = "maximizeRect";
  public static final String PROP_FOLDER_LISTENERS = "folderListeners";
  public static final String PROP_TOP_RIGHT = "topRight";
  public static final String PROP_TOP_RIGHT_ALIGNMENT = "topRightAlignment";
  public static final String PROP_TAB_HEIGHT = "tabHeight";
  public static final String PROP_WIDTH = "width";
  public static final String PROP_CHEVRON_VISIBLE = "chevronVisible";
  public static final String PROP_CHEVRON_RECT = "chevronRect";
  public static final String PROP_SELECTION_BG = "selectionBg";
  public static final String PROP_SELECTION_FG = "selectionFg";
  
  // Keep in sync with value in CTabFolder.js
  private static final Integer DEFAULT_TAB_HEIGHT = new Integer( 20 );

  private static final Rectangle ZERO_RECTANGLE = new Rectangle( 0, 0, 0, 0 );

  // TODO [rh] establish a scheme for the location of images
  private static final String PREFIX = "org/eclipse/rap/rwt/custom/ctabfolder/";
  private static final String MAXIMIZE_GIF = PREFIX + "maximize.gif";
  private static final String MINIMIZE_GIF = PREFIX + "minimize.gif";
  private static final String RESTORE_GIF = PREFIX + "restore.gif";
  private static final String CLOSE_GIF = PREFIX + "close.gif";
  private static final String CLOSE_HOVER_GIF = PREFIX + "close_hover.gif";
  private static final String CHEVRON_GIF = PREFIX + "chevron.gif";

  static {
    IResourceManager resourceManager = ResourceManager.getInstance();
    ClassLoader bufferedLoader = resourceManager.getContextLoader();
    resourceManager.setContextLoader( CTabFolderLCA.class.getClassLoader() );
    try {
      resourceManager.register( MAXIMIZE_GIF );
      resourceManager.register( MINIMIZE_GIF );
      resourceManager.register( RESTORE_GIF );
      resourceManager.register( CLOSE_GIF );
      resourceManager.register( CLOSE_HOVER_GIF );
      resourceManager.register( CHEVRON_GIF );
    } finally {
      resourceManager.setContextLoader( bufferedLoader );
    }
  }

  public void preserveValues( final Widget widget ) {
    CTabFolder tabFolder = ( CTabFolder )widget;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    ICTabFolderAdapter tabFolderAdapter = getCTabFolderAdapter( tabFolder );
    ControlLCAUtil.preserveValues( tabFolder );
    boolean hasListeners = SelectionEvent.hasListener( tabFolder );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( hasListeners ) );
    hasListeners = CTabFolderEvent.hasListener( tabFolder );
    adapter.preserve( PROP_FOLDER_LISTENERS,
                      Boolean.valueOf( hasListeners ) );
    adapter.preserve( PROP_SELECTION_INDEX, 
                      new Integer( tabFolder.getSelectionIndex() ) );
    adapter.preserve( PROP_WIDTH, new Integer( tabFolder.getBounds().width ) );
    adapter.preserve( PROP_MINIMIZE_VISIBLE, 
                      Boolean.valueOf( tabFolder.getMinimizeVisible() ) );
    adapter.preserve( PROP_MAXIMIZE_VISIBLE, 
                      Boolean.valueOf( tabFolder.getMaximizeVisible() ) );
    adapter.preserve( PROP_MINIMIZE_RECT, 
                      tabFolderAdapter.getMinimizeRect() );
    adapter.preserve( PROP_MAXIMIZE_RECT, 
                      tabFolderAdapter.getMaximizeRect() );
    adapter.preserve( PROP_MINIMIZED, 
                      Boolean.valueOf( tabFolder.getMinimized() ) );
    adapter.preserve( PROP_MAXIMIZED, 
                      Boolean.valueOf( tabFolder.getMaximized() ) );
    adapter.preserve( PROP_TAB_HEIGHT, 
                      new Integer( tabFolder.getTabHeight() ) );
    adapter.preserve( PROP_TOP_RIGHT, tabFolder.getTopRight() );
    adapter.preserve( PROP_SELECTION_BG, tabFolder.getSelectionBackground() );
    adapter.preserve( PROP_SELECTION_FG, tabFolder.getSelectionForeground() );
    adapter.preserve( PROP_CHEVRON_VISIBLE, 
                      Boolean.valueOf( tabFolderAdapter.getChevronVisible() ) );
    adapter.preserve( PROP_CHEVRON_RECT, tabFolderAdapter.getChevronRect() );
  }

  public void readData( final Widget widget ) {
    final CTabFolder tabFolder = ( CTabFolder )widget;
    // Read minimized state
    String value = WidgetUtil.readPropertyValue( tabFolder, PARAM_MINIMIZED );
    if( value != null ) {
      tabFolder.setMinimized( Boolean.valueOf( value ).booleanValue() );
    }
    // Read maximized state
    value = WidgetUtil.readPropertyValue( tabFolder, PARAM_MAXIMIZED );
    if( value != null ) {
      tabFolder.setMaximized( Boolean.valueOf( value ).booleanValue() );
    }
    // Minimized event
    if( WidgetUtil.wasEventSent( tabFolder, EVENT_FOLDER_MINIMIZED ) ) {
      CTabFolderEvent event = CTabFolderEvent.minimize( tabFolder ); 
      event.processEvent();
    }
    // Maximized event
    if( WidgetUtil.wasEventSent( tabFolder, EVENT_FOLDER_MAXIMIZED ) ) {
      CTabFolderEvent event = CTabFolderEvent.maximize( tabFolder ); 
      event.processEvent();
    }
    // Restore event
    if( WidgetUtil.wasEventSent( tabFolder, EVENT_FOLDER_RESTORED )) {
      CTabFolderEvent event = CTabFolderEvent.restore( tabFolder ); 
      event.processEvent();
    }
    // TODO [rh] it's a hack: necessary because folder.setSelection changes
    //      the visibility of tabItem.control; but preserveValues stores
    //      the already changed visibility and thus no JavaScript is rendered
    // Read selected item and process selection event
    final String value1 
      = WidgetUtil.readPropertyValue( tabFolder, PARAM_SELECTED_ITEM_ID );
    if( value1 != null ) {
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          CTabItem tabItem = ( CTabItem )WidgetUtil.find( tabFolder, value1 );
          tabFolder.setSelection( tabItem );
          ControlLCAUtil.processSelection( tabFolder, tabItem, false );
        }
      } );
    }
    // ShowList event
    if( WidgetUtil.wasEventSent( tabFolder, EVENT_SHOW_LIST ) ) {
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          CTabFolderEvent event = CTabFolderEvent.showList( tabFolder ); 
          event.processEvent();
          if( event.doit ) {
            ICTabFolderAdapter adapter = getCTabFolderAdapter( tabFolder );
            adapter.showListMenu();
          }
        }
      } );
    }
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.newWidget( "org.eclipse.rap.rwt.custom.CTabFolder" );
  }
  
  public void renderChanges( final Widget widget ) throws IOException {
    CTabFolder tabFolder = ( CTabFolder )widget;
    ControlLCAUtil.writeChanges( tabFolder );
    writeTabHeight( tabFolder );
    writeMinMaxVisible( tabFolder );
    writeMinMaxState( tabFolder );
    writeListener( tabFolder );
    writeChevron( tabFolder );
    writeColors( tabFolder );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }
  
//  public Rectangle adjustCoordinates( final Rectangle bounds ) {
//    int border = 1;
//    int hTabBar = 23;
//    return new Rectangle( bounds.x - border - 10, 
//                          bounds.y - hTabBar - border -10, 
//                          bounds.width, 
//                          bounds.height );
//  }


  //////////////////////////////////////
  // Helping methods to write properties
  
  private static void writeTabHeight( final CTabFolder tabFolder ) 
    throws IOException
  {
    Integer tabHeight = new Integer( tabFolder.getTabHeight() );
    Integer def = DEFAULT_TAB_HEIGHT;
    if( WidgetUtil.hasChanged( tabFolder, PROP_TAB_HEIGHT, tabHeight, def ) ) {
      JSWriter writer = JSWriter.getWriterFor( tabFolder );
      writer.set( "tabHeight", tabFolder.getTabHeight() );
    }
  }

//  // TODO [rh] revise this mess; layout code should go to CTabFolder
//  private static void writeMinMaxVisible( final CTabFolder tabFolder ) 
//    throws IOException 
//  {
//    JSWriter writer = JSWriter.getWriterFor( tabFolder );
//    // bounds changed?
//    String prop = Props.BOUNDS;
//    Rectangle newBounds = tabFolder.getBounds();
//    boolean boundsChanged 
//      = WidgetUtil.hasChanged( tabFolder, prop, newBounds, null );
//    // max button changed?
//    Boolean maxVisible = Boolean.valueOf( tabFolder.getMaximizeVisible() );
//    prop = PROP_MAXIMIZE_VISIBLE;
//    boolean maxChanged 
//      = WidgetUtil.hasChanged( tabFolder, prop, maxVisible, Boolean.FALSE );
//    // min button changed?
//    Boolean minVisible = Boolean.valueOf( tabFolder.getMinimizeVisible() );
//    prop = PROP_MINIMIZE_VISIBLE;
//    boolean minChanged 
//      = WidgetUtil.hasChanged( tabFolder, prop, minVisible, Boolean.FALSE );
//    
//    if( boundsChanged || minChanged || maxChanged ) {
//      int left = tabFolder.getClientArea().width;
//      if( tabFolder.getMaximizeVisible() ) {
//        left -= MIN_MAX_BUTTON_WIDTH;
//        writer.call( "showMaxButton", new Object[] { new Integer( left ) } );
//      } else {
//        writer.call( "hideMaxButton", null );
//      }
//      if( tabFolder.getMinimizeVisible() ) {
//        left -= MIN_MAX_BUTTON_WIDTH;
//        writer.call( "showMinButton", new Object[] { new Integer( left ) } );
//      } else {
//        writer.call( "hideMinButton", null );
//      }
//    }
//    if(    minChanged && minVisible.booleanValue() 
//        || maxChanged && maxVisible.booleanValue() ) 
//    {
//      Object[] args = new Object[] { "Minimize", "Maximize" };
//      writer.call( "setMinMaxToolTips", args );
//    }
//  }
  
  private static void writeMinMaxVisible( final CTabFolder tabFolder ) 
    throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( tabFolder );
    boolean minChanged = hasMinChanged( tabFolder );
    boolean maxChanged = hasMaxChanged( tabFolder );
    if( minChanged || maxChanged ) {
      Object adapter = tabFolder.getAdapter( ICTabFolderAdapter.class );
      ICTabFolderAdapter tabFolderAdapter = ( ICTabFolderAdapter )adapter;
      if( tabFolder.getMaximizeVisible() ) {
        int left = tabFolderAdapter.getMaximizeRect().x;
        Object[] args = new Object[] { new Integer( left ), "Maximize" };
        writer.call( "showMaxButton", args );
      } else {
        writer.call( "hideMaxButton", null );
      }
      if( tabFolder.getMinimizeVisible() ) {
        int left = tabFolderAdapter.getMinimizeRect().x;
        Object[] args = new Object[] { new Integer( left ), "Minimize" };
        writer.call( "showMinButton", args );
      } else {
        writer.call( "hideMinButton", null );
      }
    }
  }
  
  private static boolean hasMinChanged( final CTabFolder tabFolder ) {
    Object adapter = tabFolder.getAdapter( ICTabFolderAdapter.class );
    ICTabFolderAdapter tabFolderAdapter = ( ICTabFolderAdapter )adapter;
    Boolean minVisible = Boolean.valueOf( tabFolder.getMinimizeVisible() );
    boolean visibilityChanged;
    visibilityChanged = WidgetUtil.hasChanged( tabFolder, 
                                               PROP_MINIMIZE_VISIBLE, 
                                               minVisible, 
                                               Boolean.TRUE );
    boolean boundsChanged = false;
    if( !visibilityChanged ) {
      Rectangle newBounds = tabFolderAdapter.getMinimizeRect();
      boundsChanged = WidgetUtil.hasChanged( tabFolder, 
                                             PROP_MINIMIZE_RECT, 
                                             newBounds, 
                                             ZERO_RECTANGLE );
      
    }
    return visibilityChanged || boundsChanged;
  }
  
  private static boolean hasMaxChanged( final CTabFolder tabFolder ) {
    Object adapter = tabFolder.getAdapter( ICTabFolderAdapter.class );
    ICTabFolderAdapter tabFolderAdapter = ( ICTabFolderAdapter )adapter;
    Boolean maxVisible = Boolean.valueOf( tabFolder.getMaximizeVisible() );
    boolean visibilityChanged;
    visibilityChanged = WidgetUtil.hasChanged( tabFolder, 
                                               PROP_MAXIMIZE_VISIBLE, 
                                               maxVisible, 
                                               Boolean.TRUE );
    boolean boundsChanged = false;
    if( !visibilityChanged ) {
      Rectangle newBounds = tabFolderAdapter.getMaximizeRect();
      boundsChanged = WidgetUtil.hasChanged( tabFolder, 
                                             PROP_MAXIMIZE_RECT, 
                                             newBounds, 
                                             ZERO_RECTANGLE );
      
    }
    return visibilityChanged || boundsChanged;
  }
  
  private static void writeMinMaxState( final CTabFolder tabFolder ) 
    throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( tabFolder );
    String minProp = CTabFolderLCA.PARAM_MINIMIZED;
    String maxProp = CTabFolderLCA.PARAM_MAXIMIZED;
    Boolean minimized = Boolean.valueOf( tabFolder.getMinimized() );
    Boolean maximized = Boolean.valueOf( tabFolder.getMaximized() );
    Boolean defValue = Boolean.FALSE;
    if(    WidgetUtil.hasChanged( tabFolder, minProp, minimized, defValue ) 
        || WidgetUtil.hasChanged( tabFolder, maxProp, maximized, defValue ) ) 
    {
      String state;
      if( !tabFolder.getMinimized() && !tabFolder.getMaximized() ) {
        state = "normal";
      } else if( tabFolder.getMinimized() ){
        state = "min";
      } else {
        state = "max";
      }
      writer.set( "minMaxState", state );
    }
  }

  private static void writeListener( final CTabFolder tabFolder ) 
    throws IOException 
  {
    boolean hasListener = CTabFolderEvent.hasListener( tabFolder );
    Boolean newValue = Boolean.valueOf( hasListener );
    if( WidgetUtil.hasChanged( tabFolder, PROP_FOLDER_LISTENERS, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( tabFolder );
      writer.set( "hasFolderListener", newValue );
    }
  }

  private void writeChevron( final CTabFolder tabFolder ) throws IOException {
    ICTabFolderAdapter tabFolderAdapter = getCTabFolderAdapter( tabFolder );
    Boolean visible = Boolean.valueOf( tabFolderAdapter.getChevronVisible() );
    Boolean defValue = Boolean.FALSE;
    String prop = PROP_CHEVRON_VISIBLE;
    boolean visibilityChanged 
      = WidgetUtil.hasChanged( tabFolder, prop, visible, defValue );
    prop = PROP_CHEVRON_RECT;
    Rectangle chevronRect = tabFolderAdapter.getChevronRect();
    boolean rectangleChanged 
      = WidgetUtil.hasChanged( tabFolder, prop, chevronRect, null );
    if( visibilityChanged || rectangleChanged ) {
      JSWriter writer = JSWriter.getWriterFor( tabFolder );
      if( visible.booleanValue() ) {
        Object[] args = new Object[] { 
          new Integer( tabFolderAdapter.getChevronRect().x ) 
        };
        writer.call( "showChevron", args );
        if( visibilityChanged && tabFolderAdapter.getChevronVisible() ) {
          writer.set( "chevronToolTip", "Show List" );
        }
      } else {
        writer.call( "hideChevron", null );
      }
    }
  }

  private static void writeColors( final CTabFolder tabFolder ) 
    throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( tabFolder );
    Color bg = tabFolder.getSelectionBackground();
    writer.set( PROP_SELECTION_BG, "selectionBackground", bg, null );
  }

  private static ICTabFolderAdapter getCTabFolderAdapter( 
    final CTabFolder tabFolder ) 
  {
    Object adapter = tabFolder.getAdapter( ICTabFolderAdapter.class );
    return ( ICTabFolderAdapter )adapter;
  }
}
