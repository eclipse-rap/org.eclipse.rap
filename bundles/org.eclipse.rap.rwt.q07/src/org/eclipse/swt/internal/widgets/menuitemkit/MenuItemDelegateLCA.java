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
import org.eclipse.swt.widgets.MenuItem;


abstract class MenuItemDelegateLCA {

  abstract void preserveValues( MenuItem menuItem );

  abstract void readData( MenuItem menuItem );
  
  abstract void renderInitialization( MenuItem menuItem ) throws IOException; 
  
  abstract void renderChanges( MenuItem menuItem ) throws IOException;
  
  abstract void renderDispose( MenuItem menuItem ) throws IOException;
}
