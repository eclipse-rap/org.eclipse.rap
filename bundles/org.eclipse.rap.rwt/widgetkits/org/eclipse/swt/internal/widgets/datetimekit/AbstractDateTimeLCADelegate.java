/*******************************************************************************
 * Copyright (c) 2008, 2013 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.datetimekit;

import java.io.IOException;

import org.eclipse.swt.widgets.DateTime;

abstract class AbstractDateTimeLCADelegate {

  abstract void preserveValues( DateTime dateTime );
  abstract void renderInitialization( DateTime dateTime ) throws IOException;
  abstract void renderChanges( DateTime dateTime ) throws IOException;

}
