/*******************************************************************************
 * Copyright (c) 2003, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.browser;

import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Widget;

/**
 * A <code>ProgressEvent</code> is sent by a {@link Browser} to
 * {@link ProgressListener}'s when a progress is made during the loading of the
 * current URL or when the loading of the current URL has been completed.
 *
 * @since 1.4
 */
public class ProgressEvent extends TypedEvent {

  private static final long serialVersionUID = 1L;

  /** current value */
  public int current;
  /** total value */
  public int total;

  /**
   * Constructs a new instance of this class.
   *
   * @param widget the widget that fired the event
   */
  public ProgressEvent( final Widget widget ) {
    super( widget );
  }

  /**
   * Returns a string containing a concise, human-readable description of the
   * receiver.
   *
   * @return a string representation of the event
   */
  public String toString() {
    String string = super.toString();
    return string.substring( 0, string.length() - 1 ) // remove trailing '}'
           + " current="
           + current
           + " total="
           + total
           + "}";
  }
}
