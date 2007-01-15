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

package org.eclipse.rap.rwt.internal.custom.scrolledcompositekit;

import java.io.IOException;
import org.eclipse.rap.rwt.custom.ScrolledComposite;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.widgets.ControlLCAUtil;
import org.eclipse.rap.rwt.internal.widgets.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;


public final class CopyOfScrolledCompositeLCA extends AbstractWidgetLCA {
  
  private static final String ASSIGN_HORIZONTAL_BAR 
    = "org.eclipse.rap.rwt.custom.ScrolledComposite.assignHorizontalBar";
  private static final String ASSIGN_VERTICAL_BAR 
    = "org.eclipse.rap.rwt.custom.ScrolledComposite.assignVerticalBar";
  
  private static final ScrollBarLCA SCROLL_BAR_LCA = new ScrollBarLCA();
  
  private static final String PROP_CONTENT = "content";
  private static final String PROP_CLIENT_AREA = "clientArea";

  public void preserveValues( final Widget widget ) {
    ScrolledComposite composite = ( ScrolledComposite )widget;
    ControlLCAUtil.preserveValues( composite );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( composite );
    adapter.preserve( PROP_CONTENT, composite.getContent() );
    adapter.preserve( PROP_CLIENT_AREA, composite.getClientArea() );
    preserveScrollBarValues( composite );
  }

  public void readData( final Widget widget ) {
    ScrolledComposite composite = ( ScrolledComposite )widget;
    readScrollBarData( composite );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    final ScrolledComposite composite = ( ScrolledComposite )widget;
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.newWidget( "org.eclipse.rap.rwt.custom.ScrolledComposite" );
    // TODO [rh] HACK: JSWriter works only if one widget is rendered at once -
    //      cannot mix rendering of widgets ('w' widget reference is not set
    //      correctly) this problem would vanish if ScrollBar were included
    //      in the widget hierarchy and had an ordinary LCA.
    renderScrollBarInitialization( composite );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    ScrolledComposite composite = ( ScrolledComposite )widget;
    ControlLCAUtil.writeChanges( composite );
//    writeContent( composite );
    writeClientArea( composite );
    renderScrollBarChanges( composite );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    ScrolledComposite composite = ( ScrolledComposite )widget;
    JSWriter writer = JSWriter.getWriterFor( composite );
    writer.dispose();
    renderScrollBarDispose( composite );
  }

  ///////////////////////////////////
  // Helping methods to write changes

  private static void writeContent( final ScrolledComposite composite ) 
    throws IOException 
  {
    final Control newContent = composite.getContent();
    if( WidgetUtil.hasChanged( composite, PROP_CONTENT, newContent, null ) ) {
      IWidgetAdapter adapter = WidgetUtil.getAdapter( composite );
      Object oldContent = adapter.getPreserved( PROP_CONTENT );
      JSWriter writer = JSWriter.getWriterFor( composite );
      if( oldContent != null ) {
        writer.call( "remove", new Object[] { oldContent } );
      }
      IWidgetAdapter contentAdapter = WidgetUtil.getAdapter( newContent );
      contentAdapter.setRenderRunnable( new IRenderRunnable() {
        public void afterRender() throws IOException {
          JSWriter writer = JSWriter.getWriterFor( newContent );
          Object[] args = new Object[] { 
            newContent, 
            WidgetUtil.getId( composite ) 
          };
          writer.call( JSWriter.WIDGET_MANAGER_REF, "setParent", args );
        }
      } );
    }
  }

  private static void writeClientArea( final ScrolledComposite composite ) 
    throws IOException 
  {
    Rectangle clientArea = composite.getClientArea();
    if( WidgetUtil.hasChanged( composite, PROP_CLIENT_AREA, clientArea, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( composite );
      writer.set( "clipWidth", clientArea.width );
      writer.set( "clipHeight", clientArea.height );
    }
  }

  //////////////////////////////////
  // Helping methods for scroll bars

  private static void preserveScrollBarValues( 
    final ScrolledComposite composite ) 
  {
    ScrollBar horizontalBar = composite.getHorizontalBar();
    if( horizontalBar != null ) {
      SCROLL_BAR_LCA.preserveValues( horizontalBar );
    }
    ScrollBar verticalBar = composite.getVerticalBar();
    if( verticalBar != null ) {
      SCROLL_BAR_LCA.preserveValues( verticalBar );
    }
  }

  private static void readScrollBarData( final ScrolledComposite composite ) {
    ScrollBar horizontalBar = composite.getHorizontalBar();
    if( horizontalBar != null ) {
      SCROLL_BAR_LCA.readData( horizontalBar );
    }
    ScrollBar verticalBar = composite.getVerticalBar();
    if( verticalBar != null ) {
      SCROLL_BAR_LCA.readData( verticalBar );
    }
  }
  
  private static void renderScrollBarInitialization( 
    final ScrolledComposite composite ) 
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( composite );
    adapter.setRenderRunnable( new IRenderRunnable() {
      public void afterRender() throws IOException {
        ScrollBar horizontalBar = composite.getHorizontalBar();
        if( horizontalBar != null ) {
          SCROLL_BAR_LCA.renderInitialization( horizontalBar );
          Object[] args = new Object[] { 
            WidgetUtil.getId( composite ), 
            WidgetUtil.getId( horizontalBar ) 
          };
          JSWriter writer = JSWriter.getWriterFor( horizontalBar );
          writer.callStatic( ASSIGN_HORIZONTAL_BAR, args );
          SCROLL_BAR_LCA.renderChanges( horizontalBar );
        }
        ScrollBar verticalBar = composite.getVerticalBar();
        if( verticalBar != null ) {
          SCROLL_BAR_LCA.renderInitialization( verticalBar );
          JSWriter writer = JSWriter.getWriterFor( verticalBar );
          Object[] args = new Object[] { 
            WidgetUtil.getId( composite ), 
            WidgetUtil.getId( verticalBar ) 
          };
          writer.callStatic( ASSIGN_VERTICAL_BAR, args );
          SCROLL_BAR_LCA.renderChanges( verticalBar );
        }
      }
    } );
  }

  private static void renderScrollBarChanges( 
    final ScrolledComposite composite ) 
    throws IOException 
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( composite );
    if( adapter.isInitialized() ) {
      ScrollBar horizontalBar = composite.getHorizontalBar();
      if( horizontalBar != null ) {
        SCROLL_BAR_LCA.renderChanges( horizontalBar );
      }
      ScrollBar verticalBar = composite.getVerticalBar();
      if( verticalBar != null ) {
        SCROLL_BAR_LCA.renderChanges( verticalBar );
      }
    }
  }

  private static void renderScrollBarDispose( 
    final ScrolledComposite composite ) 
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( composite );
    adapter.setRenderRunnable( new IRenderRunnable() {
      public void afterRender() throws IOException {
        ScrollBar horizontalBar = composite.getHorizontalBar();
        if( horizontalBar != null ) {
          SCROLL_BAR_LCA.renderDispose( horizontalBar );
        }
        ScrollBar verticalBar = composite.getVerticalBar();
        if( verticalBar != null ) {
          SCROLL_BAR_LCA.renderDispose( verticalBar );
        }
      }
    } );
  }
}
