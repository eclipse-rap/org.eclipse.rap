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

package org.eclipse.rap.rwt.internal.widgets.tablekit;

import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.widgets.Table;


public final class TableLCAUtil {
  
  private static final String PROP_COLUMN_COUNT = "columnCount";
  
  public static String getItemJSParent( final Table table ) {
    StringBuffer parentId = new StringBuffer();
    parentId.append( WidgetUtil.getId( table ) );
    parentId.append( "_clientArea"  );
    return parentId.toString();
  }
  
  public static void preserveColumnCount( final Table table ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( table );
    adapter.preserve( PROP_COLUMN_COUNT, 
                      new Integer( table.getColumnCount() ) );
  }
  
  public static int getPreservedColumnCount( final Table table ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( table );
    Integer preserved = ( Integer )adapter.getPreserved( PROP_COLUMN_COUNT );
    int result;
    if( preserved == null ) {
      result = -1;
    } else {
      result = preserved.intValue();
    }
    return result; 
  }
}
