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
import org.eclipse.rap.rwt.custom.CTabFolder;
import org.eclipse.rap.rwt.custom.CTabItem;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.resources.ResourceManager;
import org.eclipse.rap.rwt.widgets.Control;
import org.eclipse.rap.rwt.widgets.Widget;
import com.w4t.IResourceManager;


public class CTabFolderLCA extends AbstractWidgetLCA {
  
  // TODO [rh] establish a scheme for the location of images
  private static final String PREFIX = "org/eclipse/rap/rwt/custom/ctabfolder/";
  private static final String MAXIMIZE_GIF = PREFIX + "maximize.gif";
  private static final String MINIMIZE_GIF = PREFIX + "minimize.gif";
  private static final String RESTORE_GIF = PREFIX + "restore.gif";
  private static final String CLOSE_GIF = PREFIX + "close.gif";
  
  private static final Integer DEFAULT_TAB_HEIGHT 
    = new Integer( CTabFolder.DEFAULT_TAB_HEIGHT );
  private static final Integer MINUS_ONE = new Integer( -1 );
  private static final String WIDTH = "width";

  private static final JSListenerInfo JS_LISTENER_INFO
    = new JSListenerInfo( "changeSelection",
                          "org.eclipse.rap.rwt.custom.CTabFolder.tabSelected",
                          JSListenerType.STATE_AND_ACTION );

  static {
    IResourceManager resourceManager = ResourceManager.getInstance();
    ClassLoader bufferedLoader = resourceManager.getContextLoader();
    resourceManager.setContextLoader( CTabFolderLCA.class.getClassLoader() );
    try {
      resourceManager.register( MAXIMIZE_GIF );
      resourceManager.register( MINIMIZE_GIF );
      resourceManager.register( RESTORE_GIF );
      resourceManager.register( CLOSE_GIF );
    } finally {
      resourceManager.setContextLoader( bufferedLoader );
    }
  }
  
  public void preserveValues( final Widget widget ) {
    CTabFolder tabFolder = ( CTabFolder )widget;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    ControlLCAUtil.preserveValues( tabFolder );
    boolean hasListeners = SelectionEvent.hasListener( tabFolder );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( hasListeners ) );
    adapter.preserve( Props.SELECTION_INDEX,
                      new Integer( tabFolder.getSelectionIndex() ) );
    adapter.preserve( WIDTH, new Integer( tabFolder.getBounds().width ) );
    adapter.preserve( Props.MINIMIZE_VISIBLE, 
                      Boolean.valueOf( tabFolder.getMinimizeVisible() ) );
    adapter.preserve( Props.MAXIMIZE_VISIBLE, 
                      Boolean.valueOf( tabFolder.getMaximizeVisible() ) );
    adapter.preserve( Props.MINIMIZED, 
                      Boolean.valueOf( tabFolder.getMinimized() ) );
    adapter.preserve( Props.MAXIMIZED, 
                      Boolean.valueOf( tabFolder.getMaximized() ) );
    adapter.preserve( Props.TAB_HEIGHT, 
                      new Integer( tabFolder.getTabHeight() ) );
    adapter.preserve( Props.TOP_RIGHT, tabFolder.getTopRight() );
    adapter.preserve( Props.TOP_RIGHT_ALIGNMENT, 
                      new Integer( tabFolder.getTopRightAlignment() ) );
  }
  
  public void readData( final Widget widget ) {
    CTabFolder tabFolder = ( CTabFolder )widget;
    String value = WidgetUtil.readPropertyValue( tabFolder, 
                                                 Props.SELECTION_INDEX );
    if( value != null ) {
      int index = Integer.parseInt( value );
      tabFolder.setSelection( index );
    }
    value = WidgetUtil.readPropertyValue( tabFolder, Props.MINIMIZED );
    if( value != null ) {
      tabFolder.setMinimized( !tabFolder.getMinimized() );
    }
    value = WidgetUtil.readPropertyValue( tabFolder, Props.MAXIMIZED );
    if( value != null ) {
      tabFolder.setMaximized( !tabFolder.getMaximized() );
    }
    CTabItem item = null;
    if( tabFolder.getSelectionIndex() != -1 ) {
      item = tabFolder.getItem( tabFolder.getSelectionIndex() );
    }
    ControlLCAUtil.processSelection( tabFolder, item, true );
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.newWidget( "org.eclipse.rap.rwt.custom.CTabFolder" );
    writer.call( "registerTopRightArea", null );
  }
  
  public void renderChanges( final Widget widget ) throws IOException {
    CTabFolder tabFolder = ( CTabFolder )widget;
    ControlLCAUtil.writeChanges( tabFolder );
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.updateListener( JS_LISTENER_INFO, 
                           Props.SELECTION_LISTENERS, 
                           SelectionEvent.hasListener( widget ) );
    writeSelectionIndex( writer, tabFolder );
    writeTabHeight( writer, tabFolder );
    Integer width = new Integer( tabFolder.getBounds().width );
    if( WidgetUtil.hasChanged( widget, WIDTH, width ) ) {
      writer.set( "maxBarWidth", width );
    }
    writeMinimizeVisible( writer, tabFolder );
    writeMaximizeVisible( writer, tabFolder );
    writeMinimized( writer, tabFolder );
    writeMaximized( writer, tabFolder );
    writeTopRight( writer, tabFolder );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  //////////////////////////////////////
  // Helping methods to write properties
  
  private static void writeSelectionIndex( final JSWriter writer, 
                                           final CTabFolder tabFolder ) 
    throws IOException 
  {
    Integer newIndex = new Integer( tabFolder.getSelectionIndex() );
    String property = Props.SELECTION_INDEX;
    if( WidgetUtil.hasChanged( tabFolder, property, newIndex, MINUS_ONE ) ) {
      writer.set( JSConst.QX_FIELD_SELECTION, tabFolder.getSelectionIndex() );
    }
  }

  private static void writeTabHeight( final JSWriter writer, 
                                      final CTabFolder tabFolder ) 
    throws IOException
  {
    Integer tabHeight = new Integer( tabFolder.getTabHeight() );
    Integer def = DEFAULT_TAB_HEIGHT;
    if( WidgetUtil.hasChanged( tabFolder, Props.TAB_HEIGHT, tabHeight, def ) ) {
      writer.set( "tabHeight", tabFolder.getTabHeight() );
    }
  }

  private static void writeMaximizeVisible( final JSWriter writer, 
                                            final CTabFolder tabFolder ) 
    throws IOException 
  {
    Boolean maxVisible = Boolean.valueOf( tabFolder.getMaximizeVisible() );
    String prop = Props.MAXIMIZE_VISIBLE;
    if( WidgetUtil.hasChanged( tabFolder, prop, maxVisible, Boolean.TRUE ) ) {
      writer.set( "maximizeVisible", tabFolder.getMaximizeVisible() );
    }
  }

  private static void writeMinimizeVisible( final JSWriter writer, 
                                            final CTabFolder tabFolder ) 
    throws IOException 
  {
    Boolean minVisible = Boolean.valueOf( tabFolder.getMinimizeVisible() );
    String prop = Props.MINIMIZE_VISIBLE;
    if( WidgetUtil.hasChanged( tabFolder, prop, minVisible, Boolean.TRUE ) ) {
      writer.set( "minimizeVisible", tabFolder.getMinimizeVisible() );
    }
  }

  private static void writeMinimized( final JSWriter writer, 
                                      final CTabFolder tabFolder )
    throws IOException
  {
    String prop = Props.MINIMIZED;
    Boolean minimized = Boolean.valueOf( tabFolder.getMinimized() );
    if( WidgetUtil.hasChanged( tabFolder, prop, minimized, Boolean.FALSE ) ) {
      writer.set( "minimized", tabFolder.getMinimized() );
    }
  }

  private static void writeMaximized( final JSWriter writer, 
                                      final CTabFolder tabFolder )
    throws IOException
  {
    Boolean maximized = Boolean.valueOf( tabFolder.getMaximized() );
    String prop = Props.MAXIMIZED;
    if( WidgetUtil.hasChanged( tabFolder, prop, maximized, Boolean.FALSE ) ) {
      writer.set( "maximized", tabFolder.getMaximized() );
    }
  }
  
  private static void writeTopRight( final JSWriter writer, 
                                     final CTabFolder tabFolder ) 
  {
    final Control topRight = tabFolder.getTopRight();
    if( WidgetUtil.hasChanged( tabFolder, Props.TOP_RIGHT, topRight, null ) ) {
      IWidgetAdapter adapter = WidgetUtil.getAdapter( tabFolder );
      adapter.setRenderRunnable( new IRenderRunnable() {
        public void afterRender() throws IOException {
          writer.set( "topRight", topRight );
        }
      } );
    }
    if( topRight != null ) {
      adjustTopRightJSParent( topRight );
    }
  }

  private static void adjustTopRightJSParent( final Control control ) {
    StringBuffer replacementId = new StringBuffer();
    String parentId = WidgetUtil.getId( control.getParent() );
    replacementId.append( parentId );
    replacementId.append( "topRight" );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( control );
    adapter.setJSParent( replacementId.toString() );
  }
}
