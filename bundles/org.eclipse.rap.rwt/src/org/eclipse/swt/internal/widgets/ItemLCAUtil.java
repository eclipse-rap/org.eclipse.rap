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

package org.eclipse.swt.internal.widgets;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.widgets.Item;


public class ItemLCAUtil {

  private ItemLCAUtil() {
    // prevent instantiation
  }
  
  public static void preserve( final Item item ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    adapter.preserve( Props.TEXT, item.getText() );
    adapter.preserve( Props.IMAGE, item.getImage() );
  }
  
  public static void writeText( final Item item ) throws IOException {
    writeText( item, false );
  }
  
  public static void writeText( final Item item, final boolean escapeMnemonics ) 
    throws IOException 
  {
    String text = item.getText();
    if( WidgetLCAUtil.hasChanged( item, Props.TEXT, text ) ) {
      JSWriter writer = JSWriter.getWriterFor( item );
      text = WidgetLCAUtil.escapeText( text, escapeMnemonics );
      writer.set( JSConst.QX_FIELD_LABEL, text ); 
    }
  }
  
  public static void writeImage( final Item item ) throws IOException {
    // TODO [rh] inline method below and remove from WidgetLCAUtil, we are the
    //      only consumer
    WidgetLCAUtil.writeImage( item, item.getImage() );
  }
  
  public static void writeChanges( final Item item ) throws IOException {
    writeText( item, false );
    writeImage( item );
  }
}
