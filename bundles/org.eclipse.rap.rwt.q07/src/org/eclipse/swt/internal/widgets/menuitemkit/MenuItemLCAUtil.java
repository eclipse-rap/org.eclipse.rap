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

package org.eclipse.swt.internal.widgets.menuitemkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.MenuItem;

final class MenuItemLCAUtil {

  static final String PROP_ENABLED = "enabled";

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
    int index = menuItem.getParent().indexOf( menuItem );
    writer.call( menuItem.getParent(), 
                 "addAt", 
                 new Object[]{ menuItem, new Integer( index ) } );
  }

  static void preserveEnabled( final MenuItem menuItem ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menuItem );
    adapter.preserve( Props.ENABLED,
                      Boolean.valueOf( menuItem.getEnabled() ) );
  }

  static void writeEnabled( final MenuItem menuItem ) throws IOException {
    Boolean newValue = Boolean.valueOf( menuItem.getEnabled() );
    JSWriter writer = JSWriter.getWriterFor( menuItem );
    Boolean defValue = Boolean.TRUE;
    writer.set( PROP_ENABLED, JSConst.QX_FIELD_ENABLED, newValue, defValue );
  }

  static void writeImageAndText( final MenuItem menuItem ) throws IOException {
    ItemLCAUtil.writeText( menuItem, true );
    ItemLCAUtil.writeImage( menuItem );
  }
}
