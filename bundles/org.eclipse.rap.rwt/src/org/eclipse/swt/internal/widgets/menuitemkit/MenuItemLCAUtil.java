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

package org.eclipse.swt.internal.widgets.menuitemkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.JSWriter;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.MenuItem;

final class MenuItemLCAUtil {
  
  static void newItem( final MenuItem menuItem, 
                       final String jsClass, 
                       final boolean isQxAtom )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( menuItem );
    writer.newWidget( jsClass );
    if( isQxAtom ) {
      writer.callStatic( "org.eclipse.swt.MenuUtil.setLabelMode", 
                         new Object[] { menuItem } );
    }
    writer.call( menuItem.getParent(), "add", new Object[]{ menuItem } );
  }
  
  static void writeEnabled( final MenuItem menuItem ) throws IOException {
    Boolean newValue = Boolean.valueOf( menuItem.isEnabled() );
    JSWriter writer = JSWriter.getWriterFor( menuItem );
    Boolean defValue = Boolean.TRUE;
    writer.set( Props.ENABLED, JSConst.QX_FIELD_ENABLED, newValue, defValue );
  }

  static void writeImageAndText( final MenuItem menuItem ) throws IOException 
  {
    ItemLCAUtil.writeText( menuItem, true );
    ItemLCAUtil.writeImage( menuItem );
  }
}
