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

import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;


public class AndConditionImpl
  implements CombinatorCondition, ConditionExt
{

  private final Condition first;

  private final Condition second;

  public AndConditionImpl( final Condition first, final Condition second ) {
    this.first = first;
    this.second = second;
  }

  public Condition getFirstCondition() {
    return first;
  }

  public Condition getSecondCondition() {
    return second;
  }

  public short getConditionType() {
    return SAC_AND_CONDITION;
  }

  public boolean matches( final Element element ) {
    ElementMatcher firstMatcher = ( ElementMatcher )first;
    ElementMatcher secondMatcher = ( ElementMatcher )second;
    return firstMatcher.matches( element ) && secondMatcher.matches( element );
  }

  public int getSpecificity() {
    Specific specificFirst = ( Specific )first;
    Specific specificSecond = ( Specific )second;
    return specificFirst.getSpecificity() + specificSecond.getSpecificity();
  }

  public String[] getClasses() {
    String[] classes1 = ( ( ConditionExt )first ).getClasses();
    String[] classes2 = ( ( ConditionExt )second ).getClasses();
    String[] result = null;
    if( classes1 == null ) {
      result = classes2;
    } else if( classes2 == null ) {
      result = classes1;
    } else {
      result = new String[ classes1.length + classes2.length ];
      System.arraycopy( classes1, 0, result, 0, classes1.length );
      System.arraycopy( classes2, 0, result, classes1.length, classes2.length );
    }
    return result;
  }

  public String toString() {
    return first.toString() + second.toString();
  }
}
