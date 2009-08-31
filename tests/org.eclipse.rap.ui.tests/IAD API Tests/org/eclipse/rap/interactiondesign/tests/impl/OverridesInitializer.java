package org.eclipse.rap.interactiondesign.tests.impl;

import org.eclipse.rap.ui.interactiondesign.layout.model.ILayoutSetInitializer;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;


public class OverridesInitializer implements ILayoutSetInitializer {

  public void initializeLayoutSet( final LayoutSet layoutSet ) {
    layoutSet.addImagePath( "aOverrideKey", "/somepath" );
  }
}
