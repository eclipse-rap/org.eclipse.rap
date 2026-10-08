/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.service;

import java.io.IOException;
import java.io.InputStream;


/**
 * A resource loader is used to load the contents of a named resource.
 *
 * @since 2.0
 */
public interface ResourceLoader {

  /**
   * Returns an input stream to the resource contents.
   *
   * @param a name to identify the resource
   * @return an input stream or <code>null</code> if the resource could not be found
   */
  InputStream getResourceAsStream( String resourceName ) throws IOException;

}
