/*******************************************************************************
* Copyright (c) 2012 EclipseSource and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License 2.0
* which is available at
* https://www.eclipse.org/legal/epl-2.0
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*    EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rap.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith( RAPAllTestSuite.class )
public class RAPAllTestSuite extends Suite {

  public RAPAllTestSuite( Class<?> testClass ) throws Exception {
    super( testClass, new TestCollector().collectTests() );
  }

}
