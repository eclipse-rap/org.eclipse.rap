/*******************************************************************************
 * Copyright (c) 2002-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.service;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.util.ParamCheck;


/**
 * {@link ISettingStoreFactory} that creates {@link FileSettingStore} 
 * instances.
 * <p>
 * This particular implementation uses the following strategy to determine
 * the path for persisting the data of a FileSettingStore:
 * <ol>
 * <li>Use the directory specified by the init-parameter 
 * <code>"org.eclipse.rwt.service.FileSettingStore.dir"</code> in the 
 * web.xml.
 * </li>
 * <li>Use the directory specified by the 
 * <code>"javax.servlet.context.tempdir"</code> attribute in the servlet context.
 * </li>
 * <li>Use the directory specified by the <code>"java.io.tempdir"</code>
 * property.
 * </li>
 * </ol>
 * The first path that can be obtained from the above choices (in the order
 * given above) will be used. If the path determined does not exist it will
 * be created.
 * <p>
 * <b>Note:</b> This setting store factory should be used in an RWT-only 
 * deployment. For a regular RAP deployment use the
 * <code>WorkbenchFileSettingStoreFactory</code>.
 * 
 * @since 1.1
 */
public final class RWTFileSettingStoreFactory implements ISettingStoreFactory {

  public ISettingStore createSettingStore( final String storeId ) {
    ParamCheck.notNullOrEmpty( storeId, "storeId" );
    ISettingStore result = new FileSettingStore( getWorkDir() );
    try {
      result.loadById( storeId );
    } catch( SettingStoreException sse ) {
      String msg = String.valueOf( sse.getMessage() );
      RWT.getRequest().getSession().getServletContext().log( msg, sse );
    }
    return result;
  }
  
  //////////////////
  // helping methods

  private ServletContext getContext() {
    HttpSession Session = ContextProvider.getRequest().getSession();
    ServletContext context = Session.getServletContext();
    return context;
  }
  
  private File getWorkDir() {
    File result = getWorkDirFromWebXml();
    if( result == null ) {
      result = getWorkDirFromServletContext();
      if ( result == null ) {
        String parent = System.getProperty( "java.io.tmpdir" );
        result = new File( parent, FileSettingStore.class.getName() );
      }
    }
    if( !result.exists() ) {
      result.mkdirs();
    }
    return result;
  }
  
  private File getWorkDirFromWebXml() {
    String key = FileSettingStore.FILE_SETTING_STORE_DIR;
    String path = getContext().getInitParameter( key );
    return ( path != null ) ? new File( path ) : null;
  }

  
  private File getWorkDirFromServletContext() {
    String key = "javax.servlet.context.tempdir";
    File parent = ( File ) getContext().getAttribute( key );
    return ( parent != null ) 
           ? new File( parent, FileSettingStore.class.getName() )
           : null;
  }
  
}
