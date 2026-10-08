/*******************************************************************************
 * Copyright (c) 2008, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme.css;

import org.w3c.css.sac.AttributeCondition;


public class NullAttributeCondition implements AttributeCondition, ConditionExt
{

  @Override
  public String getLocalName() {
    return null;
  }

  @Override
  public String getNamespaceURI() {
    return null;
  }

  @Override
  public boolean getSpecified() {
    return false;
  }

  @Override
  public String getValue() {
    return null;
  }

  @Override
  public short getConditionType() {
    return SAC_ATTRIBUTE_CONDITION;
  }

  @Override
  public int getSpecificity() {
    return 0;
  }

  @Override
  public String[] getConstraints() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    return "null condition";
  }

}
