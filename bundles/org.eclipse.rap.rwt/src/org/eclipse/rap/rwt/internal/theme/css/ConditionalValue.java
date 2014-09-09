/*******************************************************************************
 * Copyright (c) 2008, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme.css;

import org.eclipse.rap.rwt.internal.theme.CssType;


/**
 * A value that can only be applied to a widget that meets certain constraints.
 */
public class ConditionalValue {
  public final String[] constraints;
  public final CssType value;

  public ConditionalValue( String[] constraints, CssType value ) {
    this.constraints = constraints;
    this.value = value;
  }

  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    buffer.append( "ConditionalValue{ constraints=(" );
    for( int j = 0; j < constraints.length; j++ ) {
      if( j > 0 ) {
        buffer.append( ", " );
      }
      buffer.append( constraints[ j ] );
    }
    buffer.append( ") value=" );
    buffer.append( value );
    buffer.append( " }" );
    return buffer.toString();
  }
}
