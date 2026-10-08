/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.custom;

import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertNotNull;

import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class CBanner_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Shell shell;

  private CBanner banner;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    banner = new CBanner( shell, SWT.NONE );
  }

  @Test
  public void testIsSerializable() throws Exception {
    banner.setLeft( new CCombo( banner, SWT.NONE ) );
    banner.setRight( new CCombo( banner, SWT.NONE ) );

    CBanner deserializedBanner = serializeAndDeserialize( banner );

    assertNotNull( deserializedBanner.getLeft() );
    assertNotNull( deserializedBanner.getRight() );
  }

}
