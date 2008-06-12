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
package org.eclipse.swt.internal.widgets.buttonkit;

import java.io.IOException;
import org.eclipse.swt.widgets.Button;

abstract class ButtonDelegateLCA {

  abstract void preserveValues( Button button );
  abstract void readData( Button button );
  abstract void renderInitialization( Button button ) throws IOException;
  abstract void renderChanges( Button button ) throws IOException;
  abstract void renderDispose( Button button ) throws IOException;
  abstract void createResetHandlerCalls( String typePoolId ) throws IOException;
  abstract String getTypePoolId( Button widget );
}
