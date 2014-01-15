/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.jstest.internal;

import java.io.IOException;
import java.io.InputStream;
import org.eclipse.rap.rwt.jstest.TestContribution;


public class TestRunnerContribution implements TestContribution {

  private static final String PATH_PREFIX = "/org/eclipse/rwt/test/";

  private static final String[] FILES = new String[] {
    "Presenter.js",
    "TestRunner.js",
    "Asserts.js",
    "Startup.js"
  };

  public String getName() {
    return "test-runner";
  }

  public String[] getResources() {
    String[] result = new String[ FILES.length ];
    for( int i = 0; i < FILES.length; i++ ) {
      result[ i ] = PATH_PREFIX + FILES[ i ];
    }
    return result;
  }

  public InputStream getResourceAsStream( String resource ) throws IOException {
    return TestRunnerContribution.class.getResourceAsStream( resource );
  }

}
