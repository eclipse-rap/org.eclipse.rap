/*******************************************************************************
 * Copyright (c) 2007 NOMAD business software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Samy Abou-Shama NOMAD business software GmbH - initial Databinding migration
 ******************************************************************************/

package org.eclipse.rap.demo.databinding;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * View that holds some snippets from the org.eclipse.jface.examples.databindung
 * project All the snippets provided have been adapted to run in Views
 * 
 * @author Samy
 */
public class DatabindingSnippetsView extends ViewPart {

  public static final int UNDEFINDED = -1;
  public static final int GROUP_WIDTH = 700;
  public static final int TOP_MARGIN = 5;
  public static final int VIEW_MIN_HEIGHT = 200;
  public static final int GROUP_MARGIN_HEIGHT = 5;
  public static final int GROUP_MARGIN_WIDTH = 5;
  public static final int STD_LABEL_WIDTH = 80;
  public static final int STD_LABEL_WIDTH_LARGE = 110;
  public static final int STD_TEXT_WIDTH = 95;
  public static final int STD_TEXT_WIDTH_LARGE = 200;

  public void createPartControl( final Composite parent ) {
    FormLayout formLayout = new FormLayout();
    formLayout.marginHeight = GROUP_MARGIN_HEIGHT;
    formLayout.marginWidth = GROUP_MARGIN_WIDTH;
    parent.setLayout( new FormLayout() );
    Snippet000HelloWorld snippet000 = new Snippet000HelloWorld( parent,
                                                                SWT.NONE );
    FormData data = new FormData( GROUP_WIDTH, SWT.DEFAULT );
    data.top = new FormAttachment( 0, TOP_MARGIN );
    data.left = new FormAttachment( 0, GROUP_MARGIN_WIDTH );
    snippet000.setLayoutData( data );
    Snippet001NestedSelectionWithCombo snippet001
      = new Snippet001NestedSelectionWithCombo( parent,
                                                                                            SWT.NONE );
    data = new FormData( GROUP_WIDTH, SWT.DEFAULT );
    data.top = new FormAttachment( snippet000, TOP_MARGIN );
    data.left = new FormAttachment( 0, GROUP_MARGIN_WIDTH );
    snippet001.setLayoutData( data );
    Snippet004DataBindingContextErrorLabel snippet004
      = new Snippet004DataBindingContextErrorLabel( parent,
                                                                                                    SWT.NONE );
    data = new FormData( GROUP_WIDTH, SWT.DEFAULT );
    data.top = new FormAttachment( snippet001, TOP_MARGIN );
    data.left = new FormAttachment( 0, GROUP_MARGIN_WIDTH );
    snippet004.setLayoutData( data );
  }

  public void setFocus() {
  }
}
