/*******************************************************************************
 * Copyright (c) 2025 EclipseSource and others.
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
package org.eclipse.rap.rwt.addons.scanner;

/**
 * <p>
 * This adapter provides an empty default implementation of {@link ScanListener}.
 * </p>
 *
 * @see ScanListener
 *
 * @since 4.4
 */
@SuppressWarnings("serial")
public class ScanAdapter implements ScanListener {

  @Override
  public void scanSucceeded( String format, String data, int[] rawData ) {
    // intended to be implemented by subclasses.
  }

  @Override
  public void scanFailed( String error ) {
    // intended to be implemented by subclasses.
  }

}
