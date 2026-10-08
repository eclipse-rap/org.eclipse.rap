/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.service.ApplicationContext;


public interface ThemeResource {

  String getResourcePath( ApplicationContext applicationContext );

  InputStream getResourceAsStream() throws IOException;

}
