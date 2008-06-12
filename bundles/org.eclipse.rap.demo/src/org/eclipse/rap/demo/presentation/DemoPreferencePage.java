/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.demo.presentation;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.*;
import org.eclipse.ui.internal.IWorkbenchConstants;
import org.eclipse.ui.internal.util.PrefUtil;


public class DemoPreferencePage
  extends FieldEditorPreferencePage
  implements IWorkbenchPreferencePage
{
  private static final String LABEL_PRESENTATION = "Presentation";

  public DemoPreferencePage() {
    super( GRID );
    setPreferenceStore( PrefUtil.getAPIPreferenceStore() );
  }

  public void init( final IWorkbench workbench ) {
  }

  protected void createFieldEditors() {
    String presentationFactoryId
      = IWorkbenchPreferenceConstants.PRESENTATION_FACTORY_ID;
    String[][] namesAndIds = new String[][] {
      { "Default Presentation", IWorkbenchConstants.DEFAULT_PRESENTATION_ID },
      { "Demo Presentation", "org.eclipse.rap.demo.presentation" }
    };
    ComboFieldEditor comboEditor
      = new ComboFieldEditor( presentationFactoryId,
                              LABEL_PRESENTATION,
                              namesAndIds,
                              getFieldEditorParent() );
    addField( comboEditor );
  }
}
