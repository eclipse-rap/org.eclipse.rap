/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.util;

/** <p>A CssClass represents a css style class with class name and 
  * content.</p>
  * 
  * Global classloader namespace
  */
//TODO [rh] JavaDoc: update needed?
public class CssClass {

  private final String className;
  private final String content;
  private StringBuffer stringRepresentation;
  
  public CssClass( final String className, final String content ) {
    this.className = className;
    this.content = content;
  }

  public String toString() {
    if( stringRepresentation == null ) {
      stringRepresentation = new StringBuffer();
      stringRepresentation.append( "org.eclipse.rap.types.CssClass [ " );
      stringRepresentation.append( getClassName() );
      stringRepresentation.append( ", " );
      stringRepresentation.append( getContent() );
      stringRepresentation.append( " ]" );
    }
    return stringRepresentation.toString();
  }


  // attribute getters and setters
  ////////////////////////////////

  /** <p>returns the class name of this CssClass.</p> */
  public String getClassName() {
    return className;
  }

  /** <p>returns the content of the css class, that is the actual 
    * style definitions in this CssClass.</p> */
  public String getContent() {
    return content;
  }
}