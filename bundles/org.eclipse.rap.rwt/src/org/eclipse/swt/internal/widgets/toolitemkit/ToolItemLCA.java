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

package org.eclipse.swt.internal.widgets.toolitemkit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rwt.lifecycle.JSWriter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

public final class ToolItemLCA extends AbstractWidgetLCA {
  
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
  
  public void preserveValues( final Widget widget ) {
    getLCADelegate( widget ).preserveValues( ( ToolItem )widget );
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

  public void createResetHandlerCalls( final String typePoolId ) throws IOException {
  }
  
  public String getTypePoolId( final Widget widget ) throws IOException {
    return null;
  }
  
  private static ToolItemDelegateLCA getLCADelegate( final Widget widget ) {
    ToolItemDelegateLCA result;
    int style = ( ( ToolItem )widget ).getStyle();
    if( ( style & SWT.CHECK ) != 0 ) {
      result = CHECK;
    } else if( ( style & SWT.PUSH ) != 0 ) {
      result = PUSH;
    } else if( ( style & SWT.SEPARATOR ) != 0 ) {
      result = SEPERATOR;
    } else if( ( style & SWT.DROP_DOWN ) != 0 ) {
      result = DROP_DOWN;
    } else if( ( style & SWT.RADIO ) != 0 ) {
      result = RADIO;
    } else {
      result = PUSH;
    }
    return result;
  }
}
