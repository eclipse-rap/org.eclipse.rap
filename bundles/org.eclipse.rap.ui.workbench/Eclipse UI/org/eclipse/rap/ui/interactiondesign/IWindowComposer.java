/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.ui.interactiondesign;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * This interface is only used to bind some 
 * <code>{@link WorkbenchWindowAdvisor}</code> methods to a presentation. This 
 * is necessary to create components like a header or footer for a RAP 
 * application. But the methods can also be used to customize existing 
 * components like the perspective switcher or the toolbar and so on.
 * <p>
 * An instance of this interface is created in the 
 * <code>{@link PresentationFactory#createWindowComposer()}</code> method.
 * </p>
 * 
 * @since 1.2
 * 
 * @see WorkbenchWindowAdvisor
 * @see PresentationFactory#createWindowComposer()
 *
 */
public interface IWindowComposer {
  
  /**
   * Creates the contents of the window.
   * <p>
   * The default implementation of the <code>{@link WorkbenchWindowAdvisor}
   * </code> adds a menu bar, a cool bar, a status line, 
   * a perspective bar, and a fast view bar.  The visibility of these controls
   * can be configured using the <code>setShow*</code> methods on
   * <code>IWorkbenchWindowConfigurer</code>.
   * </p>
   * 
   * @param shell the window's shell
   * @param configurer the <code>{@link IWorkbenchWindowConfigurer}<code> because
   * the instance of the configurer is a field in the 
   * <code>WorkbenchWindowAdvisor</code> but not accessable in the 
   * <code>{@link PresentationFactory}</code>.
   * 
   * @return the result is used to call 
   * <code>{@link IWorkbenchWindowConfigurer#createPageComposite}</code>.
   * 
   * @see WorkbenchWindowAdvisor#createWindowContents(Shell)
   * @see IWorkbenchWindowConfigurer#createMenuBar
   * @see IWorkbenchWindowConfigurer#createCoolBarControl
   * @see IWorkbenchWindowConfigurer#createStatusLineControl
   * @see IWorkbenchWindowConfigurer#createPageComposite
   * @see PresentationFactory#createWindowComposer()
   */
  public Composite createWindowContents( 
    final Shell shell, 
    final IWorkbenchWindowConfigurer configurer );
                 
  /**
   * @see WorkbenchWindowAdvisor#postWindowOpen()
   * @param configurer the <code>{@link IWorkbenchWindowConfigurer}<code> 
   * because the instance of the configurer is a field in the 
   * <code>WorkbenchWindowAdvisor</code> but not accessible in the 
   * <code>{@link PresentationFactory}</code>.
   */
  public void postWindowOpen( final IWorkbenchWindowConfigurer configurer );
                 
  /**
   * @see WorkbenchWindowAdvisor#preWindowOpen()
   * 
   * @param configurer the <code>{@link IWorkbenchWindowConfigurer}<code> 
   * because the instance of the configurer is a field in the 
   * <code>WorkbenchWindowAdvisor</code> but not accessible in the 
   * <code>{@link PresentationFactory}</code>.
   */
  public void preWindowOpen( final IWorkbenchWindowConfigurer configurer );

 
}
