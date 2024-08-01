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
package org.eclipse.rap.e4.apache.jxpath.util;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Reverse comparator.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 668329 $ $Date: 2008-06-16 16:59:48 -0500 (Mon, 16 Jun 2008) $
 */
public final class ReverseComparator implements Comparator, Serializable {
    private static final long serialVersionUID = -2795475743948616649L;

    /**
     * Singleton reverse comparator instance.
     */
    public static final Comparator INSTANCE = new ReverseComparator();

    /**
     * Create a new ReverseComparator.
     */
    private ReverseComparator() {
    }

    public int compare(Object o1, Object o2) {
        return ((Comparable) o2).compareTo(o1);
    }

}
