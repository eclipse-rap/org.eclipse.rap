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
package org.eclipse.rap.e4.apache.jxpath.ri.model.beans;

import org.eclipse.rap.e4.apache.jxpath.ri.QName;
import org.eclipse.rap.e4.apache.jxpath.ri.model.NodePointer;

/**
 * An iterator of attributes of a JavaBean. Returns bean properties as
 * well as the "xml:lang" attribute.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 652845 $ $Date: 2008-05-02 12:46:46 -0500 (Fri, 02 May 2008) $
 */
public class BeanAttributeIterator extends PropertyIterator {
    private NodePointer parent;
    private int position = 0;
    private boolean includeXmlLang;

    /**
     * Create a new BeanAttributeIterator.
     * @param parent parent pointer
     * @param name name of this bean
     */
    public BeanAttributeIterator(PropertyOwnerPointer parent, QName name) {
        super(
            parent,
            (name.getPrefix() == null
                && (name.getName() == null || name.getName().equals("*")))
                ? null
                : name.toString(),
            false,
            null);
        this.parent = parent;
        includeXmlLang =
            (name.getPrefix() != null && name.getPrefix().equals("xml"))
                && (name.getName().equals("lang")
                || name.getName().equals("*"));
    }

    public NodePointer getNodePointer() {
        return includeXmlLang && position == 1 ? new LangAttributePointer(parent) : super.getNodePointer();
    }

    public int getPosition() {
        return position;
    }

    public boolean setPosition(int position) {
        this.position = position;
        if (includeXmlLang) {
            return position == 1 || super.setPosition(position - 1);
        }
        return super.setPosition(position);
    }
}
