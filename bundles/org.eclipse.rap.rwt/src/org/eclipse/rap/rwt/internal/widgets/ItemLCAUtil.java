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

package org.eclipse.rap.rwt.internal.widgets;

import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.widgets.Item;


public class ItemLCAUtil {

  private ItemLCAUtil() {
    // prevent instantiation
  }
  
  public static void preserve( final Item item ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    adapter.preserve( Props.TEXT, item.getText() );
    // TODO [rh] why preserve Imge.getPath(), wouldn't it be more straigtforward
    //      to preserve item.getImage() directly?
    adapter.preserve( Props.IMAGE, Image.getPath( item.getImage() ) );
  }
}
