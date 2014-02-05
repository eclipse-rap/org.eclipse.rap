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
namespace( "org.eclipse.rwt.test" );

org.eclipse.rwt.test.Startup = {

  run : function() {
    rwt.runtime.System.getInstance().addEventListener( "uiready", function() {
      org.eclipse.rwt.test.fixture.Fixture.setup();
      org.eclipse.rwt.test.Asserts.createShortcuts();
      org.eclipse.rwt.test.TestRunner.getInstance().run();
    } );
  }

};
