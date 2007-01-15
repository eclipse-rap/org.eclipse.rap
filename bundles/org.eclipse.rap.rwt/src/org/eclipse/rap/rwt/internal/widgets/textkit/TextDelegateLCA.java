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
import org.eclipse.rap.rwt.internal.widgets.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.JSConst;
import org.eclipse.rap.rwt.lifecycle.JSWriter;
import org.eclipse.rap.rwt.widgets.Widget;

public abstract class TextDelegateLCA {

  void renderInitialization( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.newWidget( getClassName() );
    writer = addProperty( writer );
    writer.addListener( JSConst.QX_EVENT_BLUR, JSConst.JS_TEXT_MODIFIED );
    writer.addListener( JSConst.QX_EVENT_INPUT, JSConst.JS_TEXT_MODIFIED );
    ControlLCAUtil.writeStyleFlags( widget );
  }

  abstract public String getClassName();

  abstract public JSWriter addProperty( JSWriter writer ) throws IOException;
}
