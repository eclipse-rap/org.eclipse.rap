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

import org.w3c.css.sac.AttributeCondition;


public class OneOfAttributeCondition implements AttributeCondition, ConditionExt
{

  private final String localName;
  private final String value;
  private final boolean specified;

  public OneOfAttributeCondition( final String localName,
                                  final String value,
                                  final boolean specified )
  {
    this.localName = localName;
    this.value = value;
    this.specified = specified;
  }

  public String getLocalName() {
    return localName;
  }

  public String getNamespaceURI() {
    return null;
  }

  public boolean getSpecified() {
    return specified;
  }

  public String getValue() {
    return value;
  }

  public short getConditionType() {
    return SAC_ONE_OF_ATTRIBUTE_CONDITION;
  }

  public boolean matches( final Element element ) {
    boolean result = false;
    if( localName != null && value != null ) {
      String attr = element.getAttribute( localName );
      // TODO improve this
      if( attr != null ) {
        String[] parts = attr.split( "\\s+" );
        for( int i = 0; i < parts.length && !result; i++ ) {
          result |= parts[ i ].equals( value );
        }
      }
    }
    return result;
  }

  public int getSpecificity() {
    return ATTR_SPEC;
  }

  public String[] getClasses() {
    return null;
  }

  public String toString() {
    return "[" + getLocalName() + "~=\"" + getValue() + "\"]";
  }
}
