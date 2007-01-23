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

package org.eclipse.rap.rwt.internal.widgets.textkit;

import java.io.IOException;
import org.eclipse.rap.rwt.widgets.Text;

abstract class TextDelegateLCA {

  abstract void preserveValues( Text text );
  abstract void readData( Text text );
  abstract void renderInitialization( Text text ) throws IOException;
  abstract void renderChanges( Text text ) throws IOException;
  abstract void renderDispose( Text text ) throws IOException;
  
}
