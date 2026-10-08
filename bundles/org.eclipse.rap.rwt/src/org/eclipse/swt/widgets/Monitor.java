/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which
 * is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.SerializableCompatibility;

/**
 * Instances of this class are descriptions of monitors.
 *
 * @see Display
 * 
 * @since 1.2
 */
public final class Monitor implements SerializableCompatibility {
  
  final Display display;

  /**
   * Prevents uninitialized instances from being created outside the package.
   */
  Monitor( Display display ) {
    this.display = display;
  }

  /**
   * Returns a rectangle describing the receiver's size and location
   * relative to its device. Note that on multi-monitor systems the
   * origin can be negative.
   *
   * @return the receiver's bounding rectangle
   */ 
  public Rectangle getBounds() {
    return display.getBounds();
  }
  
  /**
   * Returns a rectangle which describes the area of the
   * receiver which is capable of displaying data.
   * 
   * @return the client area
   */
  public Rectangle getClientArea() {
    return display.getClientArea();
  }

}
