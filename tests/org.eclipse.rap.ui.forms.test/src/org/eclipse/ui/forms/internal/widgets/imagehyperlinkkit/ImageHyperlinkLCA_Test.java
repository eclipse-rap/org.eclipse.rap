/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.forms.internal.widgets.imagehyperlinkkit;

import junit.framework.TestCase;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

public class ImageHyperlinkLCA_Test extends TestCase {

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    ImageHyperlink hyperlink = new ImageHyperlink( shell, SWT.NONE );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( hyperlink );
    Image image = ( Image )adapter.getPreserved( ImageHyperlinkLCA.PROP_IMAGE );
    assertEquals( null, image );
    RWTFixture.clearPreserved();
    Image newImage = Graphics.getImage( RWTFixture.IMAGE1 );
    hyperlink.setImage( newImage );
    RWTFixture.preserveWidgets();
    image = ( Image )adapter.getPreserved( ImageHyperlinkLCA.PROP_IMAGE );
    assertEquals( newImage, image );
    display.dispose();
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
