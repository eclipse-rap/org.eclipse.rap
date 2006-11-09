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

package org.eclipse.rap.rwt.internal.widgets.buttonkit;

import java.io.IOException;
import org.eclipse.rap.rwt.widgets.Widget;

public abstract class ButtonDelegateLCA {

  void renderInitialization( final Widget widget ) throws IOException {
    delegateRenderInitialization( widget );
  }

  public void processAction( final Widget widget ) {
    delegateProcessAction( widget );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    delegateRenderChanges( widget );
  }

  abstract public void delegateRenderChanges( final Widget widget )
    throws IOException;

  abstract public void delegateProcessAction( final Widget widget );

  abstract public void delegateRenderInitialization( final Widget widget )
    throws IOException;
}
