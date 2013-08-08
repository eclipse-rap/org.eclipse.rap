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
package org.eclipse.rap.rwt.internal.scripting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.scripting.ClientListener;
import org.eclipse.swt.SWT;
import org.junit.Test;


public class ClientListenerBinding_Test {

  @Test
  public void testCreation() {
    ClientListener listener = mock( ClientListener.class );

    ClientListenerBinding binding = new ClientListenerBinding( listener, SWT.Selection );

    assertSame( listener, binding.getListener() );
    assertEquals( SWT.Selection, binding.getEventType() );
  }

}
