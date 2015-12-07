/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.jmeter.report.processor;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.jmeter.report.core.ArgumentNullException;

/**
 * The class MapResultData provides a hash map of results from samples
 * processing.
 * 
 * @since 2.14
 */
public class MapResultData implements ResultData {

    private HashMap<String, ResultData> map = new HashMap<String, ResultData>();

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.jmeter.report.processor.ResultData#accept(org.apache.jmeter
     * .report.processor.ResultDataVisitor)
     */
    @Override
    public <TVisit> TVisit accept(ResultDataVisitor<TVisit> visitor) {
	if (visitor == null)
	    throw new ArgumentNullException("visitor");
	return visitor.visitMapResult(this);
    }

    /**
     * Get the entry set.
     *
     * @return the sets of entries of the map
     */
    public Set<Entry<String, ResultData>> entrySet() {
	return map.entrySet();
    }

    /**
     * Gets the result with the specified name.
     *
     * @param name
     *            the name of the result
     * @return the result
     */
    public ResultData getResult(String name) {
	return map.get(name);
    }

    /**
     * Sets the specified result to the map.
     *
     * @param name
     *            the name of the result
     * @param result
     *            the result
     * @return the previously result data associated with the specified name
     */
    public ResultData setResult(String name, ResultData result) {
	return map.put(name, result);
    }

    /**
     * Removes the result with the specified name.
     *
     * @param name
     *            the name of the result
     * @return the removed result data
     */
    public ResultData removeResult(String name) {
	return map.remove(name);
    }
}
