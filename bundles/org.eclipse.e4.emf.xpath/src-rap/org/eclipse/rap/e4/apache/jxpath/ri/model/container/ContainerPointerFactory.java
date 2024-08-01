/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.rap.e4.apache.jxpath.ri.model.container;

import java.util.Locale;

import org.eclipse.rap.e4.apache.jxpath.Container;
import org.eclipse.rap.e4.apache.jxpath.ri.QName;
import org.eclipse.rap.e4.apache.jxpath.ri.model.NodePointer;
import org.eclipse.rap.e4.apache.jxpath.ri.model.NodePointerFactory;

/**
 * Implements NodePointerFactory for {@link Container} objects.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 652845 $ $Date: 2008-05-02 12:46:46 -0500 (Fri, 02 May 2008) $
 */
public class ContainerPointerFactory implements NodePointerFactory {
    /** factory order for this factory */
    public static final int CONTAINER_POINTER_FACTORY_ORDER = 200;

    public int getOrder() {
        return CONTAINER_POINTER_FACTORY_ORDER;
    }

    public NodePointer createNodePointer(QName name, Object bean, Locale locale) {
        return bean instanceof Container ? new ContainerPointer(
                (Container) bean, locale) : null;
    }

    public NodePointer createNodePointer(NodePointer parent, QName name,
            Object bean) {
        return bean instanceof Container ? new ContainerPointer(parent,
                (Container) bean) : null;
    }
}
