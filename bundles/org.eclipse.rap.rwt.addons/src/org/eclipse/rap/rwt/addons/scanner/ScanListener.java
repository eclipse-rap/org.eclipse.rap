/*******************************************************************************
 * Copyright (c) 2025 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.addons.scanner;

import java.io.Serializable;


/**
 * <p>
 * A {@link ScanListener} is usually added to the {@link BarcodeScanner} widget to get notification about the scan results.
 * </p>
 * <p>
 * <strong>Please note:</strong> You can also use the {@link ScanAdapter} as a base implementation of your listener.
 * </p>
 *
 * @see BarcodeScanner
 * @see ScanAdapter
 *
 * @since 4.4
 */
public interface ScanListener extends Serializable {

  /**
   * <p>
   * Will be called when the barcode scan was successful.
   * </p>
   *
   * @param format the decoded format
   * @param data the decoded string
   * @param rawData the decoded raw data byte array
   */
  void scanSucceeded( String format, String data, int[] rawData );

  /**
   * <p>
   * Will be called when the barcode scan has failed. After an error occurred
   * no further events will be fired and the widget becomes unusable.
   * </p>
   *
   * @param error the error message
   */
  void scanFailed( String error );

}
