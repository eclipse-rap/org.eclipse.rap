/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.internal.design.example.business.stacks;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rwt.SessionSingletonBase;

/**
 * This class acts as a registry for ViewStackPresentations. This is necessary
 * because the same view can be in different parts. If a toolbar for one part 
 * change the others should be notified.
 */
public class ViewToolBarRegistry extends SessionSingletonBase {
  
  private List presentationList = new ArrayList();
  
  private ViewToolBarRegistry() {
    
  }
  
  public static ViewToolBarRegistry getInstance() {
    return ( ViewToolBarRegistry ) getInstance( ViewToolBarRegistry.class );
  }
  
  public void addViewPartPresentation( 
    final ViewStackPresentation presentation ) 
  {
    presentationList.add( presentation );
  }
  
  public void removeViewPartPresentation( 
    final ViewStackPresentation presentation ) {
    presentationList.remove( presentation );
  }
  
  public void fireToolBarChanged() {
    for( int i = 0; i < presentationList.size(); i++ ) {
      if( presentationList.get( i ) != null ) {
        ViewStackPresentation presentation 
          = ( ViewStackPresentation ) presentationList.get( i );
        presentation.catchToolbarChange();
      }
    }
  }
  
}
