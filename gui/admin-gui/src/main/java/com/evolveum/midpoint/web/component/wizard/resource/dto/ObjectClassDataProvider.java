/*
 * Copyright (c) 2010-2014 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evolveum.midpoint.web.component.wizard.resource.dto;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author lazyman
 */
public class ObjectClassDataProvider implements IDataProvider<ObjectClassDto> {

    private IModel<List<ObjectClassDto>> allClasses;
    private List<ObjectClassDto> filteredClasses;

    public ObjectClassDataProvider(IModel<List<ObjectClassDto>> allClasses) {
        setAllClasses(allClasses);
    }

    @Override
    public Iterator<? extends ObjectClassDto> iterator(long first, long count) {
        List<ObjectClassDto> data = new ArrayList<>();

        for (int i = (int) first; i < getFilteredClasses().size() && i < first + count; i++) {
            data.add(getFilteredClasses().get(i));
        }

        return data.iterator();
    }

    @Override
    public long size() {
        return getFilteredClasses().size();
    }

    @Override
    public IModel<ObjectClassDto> model(ObjectClassDto object) {
        return new Model(object);
    }

    public void setAllClasses(IModel<List<ObjectClassDto>> allClasses) {
        this.allClasses = allClasses;
    }

    private List<ObjectClassDto> getFilteredClasses() {
        if (filteredClasses == null) {
            filterClasses(null);
        }
        return filteredClasses;
    }

    private List<ObjectClassDto> getAllClasses() {
        List<ObjectClassDto> list = allClasses.getObject();
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    public void filterClasses(String value) {
        if (filteredClasses == null) {
            filteredClasses = new ArrayList<>();
        }

        filteredClasses.clear();

        if (StringUtils.isEmpty(value)) {
            filteredClasses.addAll(getAllClasses());
            return;
        }

        for (ObjectClassDto dto : getAllClasses()) {
            if (StringUtils.containsIgnoreCase(dto.getName(), value)) {
                filteredClasses.add(dto);
            }
        }
    }

    @Override
    public void detach() {
    }

	public boolean isDisplayed(String name) {
		for (ObjectClassDto objectClass : getFilteredClasses()) {
			if (objectClass.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
}
