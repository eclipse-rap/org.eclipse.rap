/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.toolitemkit;

import java.io.IOException;

import org.eclipse.swt.widgets.ToolItem;

final class SeparatorToolItemLCA extends ToolItemDelegateLCA {

  void preserveValues( ToolItem toolItem ) {
    ToolItemLCAUtil.preserveValues( toolItem );
  }

  void readData( ToolItem toolItem ) {
    // do nothing
  }

  void renderInitialization( ToolItem toolItem ) throws IOException {
    ToolItemLCAUtil.renderInitialization( toolItem );
  }

  void renderChanges( ToolItem toolItem ) throws IOException {
    ToolItemLCAUtil.renderChanges( toolItem );
  }
}
