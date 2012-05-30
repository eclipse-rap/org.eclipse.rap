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


/**
 * A default entrypoint factory that creates entrypoint instances from a given
 * class. Note that this does not work with member classes, which cannot be
 * instantiated.
 *
 * @since 1.5
 */
public class DefaultEntryPointFactory implements IEntryPointFactory {

  private final Class<? extends IEntryPoint> type;

  /**
   * Creates a new entrypoint factory for the given class.
   *
   * @param type the entrypoint class, must not be a non-static inner class
   */
  public DefaultEntryPointFactory( Class<? extends IEntryPoint> type ) {
    ParamCheck.notNull( type, "type" );

    this.type = type;
  }

  public IEntryPoint create() {
    return ClassUtil.newInstance( type );
  }
}
