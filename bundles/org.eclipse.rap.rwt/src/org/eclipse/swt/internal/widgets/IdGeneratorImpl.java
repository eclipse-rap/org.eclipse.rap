/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.internal.SerializableCompatibility;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;


public final class IdGeneratorImpl implements IdGenerator, SerializableCompatibility {

  // TODO [rst] Start from zero when hard-coded "w1" is gone
  private final AtomicInteger sequence = new AtomicInteger( 1 );

  IdGeneratorImpl() {
    // prevent instantiation from outside
  }

  public String createId( Object object ) {
    // TODO [rst] Remove dependencies on hard-coded "w1"
    if( object instanceof Display ) {
      return "w1";
    }
    return getPrefix( object ) + sequence.incrementAndGet();
  }

  private String getPrefix( Object object ) {
    String prefix = "o";
    if( object instanceof Widget || object instanceof Display ) {
      prefix = "w";
    } else if( object instanceof String ) {
      // allow for using custom prefixes for non-widget types
      prefix = ( String )object;
    }
    return prefix;
  }

}
