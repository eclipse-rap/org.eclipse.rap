/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.lifecycle;

import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.internal.util.ParamCheck;


public class DefaultEntryPointFactory implements IEntryPointFactory {

  private final Class<? extends IEntryPoint> type;

  public DefaultEntryPointFactory( Class<? extends IEntryPoint> type ) {
    ParamCheck.notNull( type, "type" );

    this.type = type;
  }

  public IEntryPoint create() {
    return ClassUtil.newInstance( type );
  }
}
