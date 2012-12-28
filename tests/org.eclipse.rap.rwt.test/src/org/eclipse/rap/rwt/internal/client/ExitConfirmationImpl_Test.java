/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.rap.rwt.client.service.ExitConfirmation;
import org.junit.Test;


public class ExitConfirmationImpl_Test {

  @Test
  public void testMessageIsNullByDefault() {
    ExitConfirmation exitConfirmation = new ExitConfirmationImpl();

    assertNull( exitConfirmation.getMessage() );
  }

  @Test
  public void testKeepsMessage() {
    ExitConfirmation exitConfirmation = new ExitConfirmationImpl();

    exitConfirmation.setMessage( "test" );

    assertEquals( "test", exitConfirmation.getMessage() );
  }

}
