/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;


public class QxIdentifier_Test extends TestCase {

  public void testCreate() {
    QxIdentifier identifier = new QxIdentifier( "line-through" );
    assertEquals( "line-through", identifier.value  );
  }

  public void testDefaultString() {
    QxIdentifier identifier = new QxIdentifier( "line-through" );
    assertEquals( "line-through", identifier.toDefaultString() );
  }

  public void testEquals() {
    QxIdentifier identifier1 = new QxIdentifier( "line-through" );
    QxIdentifier identifier2 = new QxIdentifier( "line-through" );
    assertEquals( identifier1, identifier2 );
  }

  public void testHashCode() {
    QxIdentifier identifier1 = new QxIdentifier( "line-through" );
    QxIdentifier identifier2 = new QxIdentifier( "line-through" );
    assertEquals( identifier1.hashCode(), identifier2.hashCode() );
  }
}
