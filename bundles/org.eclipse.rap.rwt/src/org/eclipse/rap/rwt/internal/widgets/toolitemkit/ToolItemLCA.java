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

package org.eclipse.rap.rwt.internal.widgets.toolitemkit;

import java.io.IOException;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.ToolItem;
import org.eclipse.rap.rwt.widgets.Widget;


public class ToolItemLCA extends AbstractWidgetLCA {
  
  private static final String PROP_FONT = "font";

  private final static ToolItemDelegateLCA PUSH 
    = new PushToolItemDelegateLCA();
  private final static ToolItemDelegateLCA CHECK
    = new CheckToolItemDelegateLCA();
  private final static ToolItemDelegateLCA RADIO
    = new RadioToolItemDelegateLCA();
  private final static ToolItemDelegateLCA SEPERATOR
    = new SeparatorToolItemDelegateLCA();
  private final static ToolItemDelegateLCA DROP_DOWN
    = new DropDownToolItemDelegateLCA();
  
  private static ToolItemDelegateLCA getLCADelegate( final Widget widget ) {
    ToolItemDelegateLCA result;
    int style = ( ( ToolItem )widget ).getStyle();
    if( ( style & RWT.CHECK ) != 0 ) {
      result = CHECK;
    } else if( ( style & RWT.PUSH ) != 0 ) {
      result = PUSH;
    } else if( ( style & RWT.SEPARATOR ) != 0 ) {
      result = SEPERATOR;
    } else if( ( style & RWT.DROP_DOWN ) != 0 ) {
      result = DROP_DOWN;
    } else if( ( style & RWT.RADIO ) != 0 ) {
      result = RADIO;
    } else {
      result = PUSH;
    }
    return result;
  }

  public void preserveValues( final Widget widget ) {
    ToolItem toolItem = ( ToolItem )widget;
    ItemLCAUtil.preserve( toolItem );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_FONT, toolItem.getParent().getFont() );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( SelectionEvent.hasListener( toolItem ) ) );
  }

  public void readData( final Widget widget ) {
    getLCADelegate( widget ).readData( ( ToolItem )widget );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    getLCADelegate( widget ).renderInitialization( ( ToolItem )widget );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    getLCADelegate( widget ).renderChanges( ( ToolItem )widget );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }
}
