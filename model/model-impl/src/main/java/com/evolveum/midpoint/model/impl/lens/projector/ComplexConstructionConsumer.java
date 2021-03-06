/**
 * Copyright (c) 2017 Evolveum
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
package com.evolveum.midpoint.model.impl.lens.projector;

import com.evolveum.midpoint.model.impl.lens.AbstractConstruction;
import com.evolveum.midpoint.model.impl.lens.ConstructionPack;
import com.evolveum.midpoint.prism.delta.DeltaMapTriple;
import com.evolveum.midpoint.util.exception.SchemaException;

/**
 * @author semancik
 *
 */
public interface ComplexConstructionConsumer<K, T extends AbstractConstruction> {

	boolean before(K key);

	void onAssigned(K key, String desc) throws SchemaException;

	void onUnchangedValid(K key, String desc) throws SchemaException;

	void onUnchangedInvalid(K key, String desc) throws SchemaException;

	void onUnassigned(K key, String desc) throws SchemaException;

	void after(K key, String desc, DeltaMapTriple<K, ConstructionPack<T>> constructionMapTriple);
}
