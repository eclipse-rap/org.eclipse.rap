/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.tabfolderkit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;


public class TabFolderLCA extends AbstractWidgetLCA {

  public void preserveValues( final Widget widget ) {
    ControlLCAUtil.preserveValues( ( Control )widget );
    WidgetLCAUtil.preserveCustomVariant( widget );
  }

  public void readData( final Widget widget ) {
    ControlLCAUtil.processMouseEvents( ( TabFolder )widget );
    ControlLCAUtil.processKeyEvents( ( TabFolder )widget );
    WidgetLCAUtil.processHelp( widget );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    TabFolder tabFolder = ( TabFolder )widget;
    JSWriter writer = JSWriter.getWriterFor( tabFolder );
    writer.newWidget( "qx.ui.pageview.tabview.TabView" );
    writer.set( "hideFocus", true );
    if( ( tabFolder.getStyle() & SWT.BOTTOM ) != 0 ) {
      writer.set( "placeBarOnTop", false );
    }    
    ControlLCAUtil.writeStyleFlags( tabFolder );
    writer.addListener( "keypress",
                        "org.eclipse.swt.TabUtil.onTabFolderKeyPress" );
    writer.addListener( "changeFocused",
                        "org.eclipse.swt.TabUtil.onTabFolderChangeFocused" );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    ControlLCAUtil.writeChanges( ( Control )widget );
    WidgetLCAUtil.writeCustomVariant( widget );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  public void createResetHandlerCalls( final String typePoolId )
    throws IOException
  {
  }

  public String getTypePoolId( final Widget widget ) {
    return null;
  }

  public Rectangle adjustCoordinates( final Widget widget,
                                      final Rectangle newBounds )
  {
    int border = 1;
    int hTabBar = 23;
    return new Rectangle( newBounds.x - border - 10,
                          newBounds.y - hTabBar - border -10,
                          newBounds.width,
                          newBounds.height );
  }
}
