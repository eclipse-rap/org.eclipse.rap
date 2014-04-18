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


public class JasmineTestsContribution implements TestContribution {

  private static final String PATH_PREFIX = "/org/eclipse/rwt/test/";

  private static final String[] TEST_FILES = new String[] {
    "spec/Jasmine.spec.js",
    "spec/Singletons.spec.js",
    "spec/Selection.spec.js",
    "spec/ServerPush.spec.js",
    "spec/DragSource.spec.js",
    "spec/DropTarget.spec.js",
    "spec/DragSourceHandler.spec.js",
    "spec/DropTargetHandler.spec.js",
    "spec/WidgetUtil.spec.js",
    "spec/Variant.spec.js",
    "spec/Style.spec.js",
    "spec/GridSynchronizer.spec.js",
    "spec/DropDownSynchronizer.spec.js",
    "spec/WidgetProxyFactory.spec.js",
    "spec/RWTQuery.spec.js",
    "spec/Numbers.spec.js",
    "spec/Strings.spec.js",
    "spec/Arrays.spec.js",
    "spec/Objects.spec.js",
    "spec/Colors.spec.js"
  };

  public String getName() {
    return "jasmine-tests";
  }

  public String[] getResources() {
    String[] result = new String[ TEST_FILES.length ];
    for( int i = 0; i < TEST_FILES.length; i++ ) {
      result[ i ] = PATH_PREFIX + TEST_FILES[ i ];
    }
    return result;
  }

  public InputStream getResourceAsStream( String resource ) throws IOException {
    return JasmineTestsContribution.class.getResourceAsStream( resource );
  }

}
