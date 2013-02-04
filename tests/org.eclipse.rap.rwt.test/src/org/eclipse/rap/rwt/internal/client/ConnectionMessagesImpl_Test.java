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

import org.junit.Test;


public class ConnectionMessagesImpl_Test {

  @Test
  public void testWaitHintTimeoutDefaultValue() {
    ConnectionMessagesImpl messages = new ConnectionMessagesImpl();

    assertEquals( 1000, messages.getWaitHintTimeout() );
  }

  @Test
  public void testChangeWaitHintTimeout() {
    ConnectionMessagesImpl messages = new ConnectionMessagesImpl();
    messages.setWaitHintTimeout( 2000 );

    assertEquals( 2000, messages.getWaitHintTimeout() );
  }

}
