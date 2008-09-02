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

import org.w3c.css.sac.*;


public class NullSiblingSelector implements SiblingSelector, SelectorExt {

  public Selector getSelector() {
    return null;
  }

  public SimpleSelector getSiblingSelector() {
    return null;
  }

  public short getSelectorType() {
    return SAC_DIRECT_ADJACENT_SELECTOR;
  }

  public short getNodeType() {
    return ANY_NODE;
  }

  public boolean matches( final Element element ) {
    return false;
  }

  public String getElementName() {
    return null;
  }

  public String[] getClasses() {
    return null;
  }

  public int getSpecificity() {
    return 0;
  }

  public String[] getConstraints() {
    throw new UnsupportedOperationException();
  }

  public String toString() {
    return "null selector";
  }
}
