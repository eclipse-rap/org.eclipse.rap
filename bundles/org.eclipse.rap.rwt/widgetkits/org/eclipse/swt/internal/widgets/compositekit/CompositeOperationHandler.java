/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
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
package org.eclipse.swt.internal.widgets.compositekit;

import org.eclipse.rap.rwt.internal.protocol.ControlOperationHandler;
import org.eclipse.swt.widgets.Composite;


public class CompositeOperationHandler extends ControlOperationHandler<Composite> {

  public CompositeOperationHandler( Composite composite ) {
    super( composite );
  }

}
