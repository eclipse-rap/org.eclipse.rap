/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.json;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.swt.layout.GridLayout;
import org.junit.Test;


public class JsonUtil_Test {

  @Test
  public void testMessageWithIllegalParameterType() {
    GridLayout wrongParameter = new GridLayout();

    try {
      JsonUtil.createJsonValue( wrongParameter );
      fail();
    } catch( IllegalArgumentException exception ) {
      String expected = "Parameter object can not be converted to JSON value";
      assertTrue( exception.getMessage().startsWith( expected ) );
    }
  }

}
