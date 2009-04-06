/*******************************************************************************
 * Copyright (c) 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;


public class QxIdentifier implements QxType {

  public final String value;

  public QxIdentifier( final String value ) {
    this.value = value;
  }

  public String toDefaultString() {
    return value;
  }

  public String toString() {
    return "QxIdentifier{ " + value + " }";
  }
}
