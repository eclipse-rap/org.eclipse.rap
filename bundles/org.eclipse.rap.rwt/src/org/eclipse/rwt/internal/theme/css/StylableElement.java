/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme.css;

import java.util.*;


public class StylableElement implements Element {

  private Map attributes = new HashMap();

  private List classes = new ArrayList();

  private List pseudoClasses = new ArrayList();

  private String name;

  private Element parent;

  public StylableElement( final String name ) {
    this( null, name );
  }

  public StylableElement( final Element parent, final String name ) {
    this.name = name;
    this.parent = parent;
  }

  public boolean hasName( final String value ) {
    return name == null || name.equals( value );
  }

  public Element getParent() {
    return parent;
  }

  public boolean hasClass( final String value ) {
    return value != null && classes.contains( value );
  }

  public boolean hasPseudoClass( final String value ) {
    return value != null && pseudoClasses.contains( value );
  }

  public boolean hasAttribute( final String name ) {
    String value = ( String )attributes.get( name );
    return value != null && value.length() > 0;
  }

  public boolean hasAttributeValue( final String name, final String value ) {
    String actualValue = ( String )attributes.get( name );
    return actualValue != null && actualValue.equals( value );
  }

  public void setClass( final String className ) {
    if( className != null ) {
      classes.add( className );
    }
  }

  public void resetClass( final String className ) {
    if( className != null && classes.contains( className ) ) {
      classes.remove( className );
    }
  }

  public void setPseudoClass( final String pseudoName ) {
    if( pseudoName != null ) {
      pseudoClasses.add( pseudoName );
    }
  }

  public void resetPseudoClass( final String pseudoName ) {
    if( pseudoName != null && pseudoClasses.contains( pseudoName ) ) {
      pseudoClasses.remove( pseudoName );
    }
  }

  public void setAttribute( final String name, final String value ) {
    if( name != null ) {
      if( value != null ) {
        attributes.put( name, value );
      } else {
        attributes.remove( name );
      }
    }
  }

  public void setAttribute( final String name ) {
    setAttribute( name, "true" );
  }

  public void resetAttribute( final String name ) {
    setAttribute( name, null );
  }
}
