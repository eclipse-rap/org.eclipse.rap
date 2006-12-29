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

package org.eclipse.rap.rwt.internal.widgets.tablecolumnkit;

import java.io.IOException;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.widgets.Widget;

// Note: [fappel] due to the complex structure of Table, TableItem and 
//                TableColumn all the work is done by this TableLCA
public class TableColumnLCA extends AbstractWidgetLCA {

  public void preserveValues( final Widget widget ) {
  }
  
  public void readData( final Widget widget ) {
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
  }

  public void renderChanges( final Widget widget ) throws IOException {
  }

  public void renderDispose( final Widget widget ) throws IOException {
  }
}
