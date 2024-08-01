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
package org.eclipse.rap.e4.apache.jxpath.ri.model.dynamic;

import org.eclipse.rap.e4.apache.jxpath.ri.QName;
import org.eclipse.rap.e4.apache.jxpath.ri.model.beans.BeanAttributeIterator;
import org.eclipse.rap.e4.apache.jxpath.ri.model.beans.PropertyOwnerPointer;

/**
 * <code>DynamicAttributeIterator</code> is different from a regular
 * <code>BeanAttributeIterator</code> in that given a property name it
 * will always find that property (albeit with a null value).
 *
 * @author <a href="mailto:dmitri@apache.org">Dmitri Plotnikov</a>
 * @version $Id: DynamicAttributeIterator.java 652845 2008-05-02 17:46:46Z ebourg $
 */
public class DynamicAttributeIterator extends BeanAttributeIterator {

    /**
     * Create a new DynamicAttributeIterator.
     * @param parent pointer
     * @param name to match properties
     */
    public DynamicAttributeIterator(PropertyOwnerPointer parent, QName name) {
        super(parent, name);
    }

    protected void prepareForIndividualProperty(String name) {
         ((DynamicPropertyPointer) getPropertyPointer()).setPropertyName(name);
         super.prepareForIndividualProperty(name);
    }
}
