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
import org.eclipse.rap.rwt.widgets.ToolItem;


abstract class ToolItemDelegateLCA {
  
  abstract void preserveValues( ToolItem toolItem );
  
  abstract void readData( ToolItem toolItem );
  
  abstract void renderInitialization( ToolItem toolItem )
    throws IOException;
  
  abstract void renderChanges( ToolItem toolItem ) 
    throws IOException;
}
