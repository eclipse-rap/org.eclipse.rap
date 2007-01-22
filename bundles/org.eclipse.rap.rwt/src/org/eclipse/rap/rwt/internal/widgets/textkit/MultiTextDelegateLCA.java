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
import org.eclipse.rap.rwt.lifecycle.JSWriter;

class MultiTextDelegateLCA extends TextDelegateLCA {

  public String getClassName() {
    return "qx.ui.form.TextArea";
  }

  public JSWriter addProperty( final JSWriter writer ) throws IOException {
    // TODO: [rst] Added because !WRAP stopped working - doesn't help anyway
    writer.set( "wrap", false );
    return writer;
  }
}
