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

package org.eclipse.swt.internal.widgets.toolitemkit;

import java.io.IOException;

import org.eclipse.swt.widgets.ToolItem;

final class PushToolItemLCA extends ToolItemDelegateLCA {

  private static final String PARAM_PUSH = "push";

  void preserveValues( final ToolItem toolItem ) {
    ToolItemLCAUtil.preserveValues( toolItem );
    ToolItemLCAUtil.preserveImages( toolItem );
  }

  void readData( final ToolItem toolItem ) {
    ToolItemLCAUtil.processSelection( toolItem );
  }

  void renderInitialization( final ToolItem toolItem ) throws IOException {
    ToolItemLCAUtil.renderInitialization( toolItem, PARAM_PUSH );
  }

  void renderChanges( final ToolItem toolItem ) throws IOException {
    ToolItemLCAUtil.renderChanges( toolItem );
  }
}
