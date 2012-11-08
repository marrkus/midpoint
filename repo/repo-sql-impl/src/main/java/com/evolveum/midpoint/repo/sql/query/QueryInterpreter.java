/*
 * Copyright (c) 2012 Evolveum
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.opensource.org/licenses/cddl1 or
 * CDDLv1.0.txt file in the source code distribution.
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 *
 * Portions Copyrighted 2012 [name of copyright owner]
 */

package com.evolveum.midpoint.repo.sql.query;

import com.evolveum.midpoint.prism.*;
import com.evolveum.midpoint.prism.path.ItemPath;
import com.evolveum.midpoint.prism.path.ItemPathSegment;
import com.evolveum.midpoint.prism.path.NameItemPathSegment;
import com.evolveum.midpoint.prism.query.LogicalFilter;
import com.evolveum.midpoint.prism.query.ObjectFilter;
import com.evolveum.midpoint.prism.query.OrgFilter;
import com.evolveum.midpoint.prism.query.ValueFilter;
import com.evolveum.midpoint.prism.schema.SchemaRegistry;
import com.evolveum.midpoint.repo.sql.util.ClassMapper;
import com.evolveum.midpoint.schema.constants.ObjectTypes;
import com.evolveum.midpoint.schema.holder.XPathHolder;
import com.evolveum.midpoint.util.DOMUtil;
import com.evolveum.midpoint.util.logging.Trace;
import com.evolveum.midpoint.util.logging.TraceManager;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ObjectType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lazyman
 */
public class QueryInterpreter {

	private static final Trace LOGGER = TraceManager.getTrace(QueryInterpreter.class);
	private PrismContext prismContext;
	// query context stuff
	private Class<? extends ObjectType> type;
	private Map<ItemPath, Criteria> criterions = new HashMap<ItemPath, Criteria>();
	private Map<ItemPath, String> aliases = new HashMap<ItemPath, String>();

	public QueryInterpreter(Session session, Class<? extends ObjectType> type, PrismContext prismContext) {
		this.prismContext = prismContext;
		this.type = type;

		String alias = createAlias(ObjectTypes.getObjectType(type).getQName());
		Criteria criteria = session.createCriteria(ClassMapper.getHQLTypeClass(type), alias);
		setCriteria(null, criteria);
		setAlias(null, alias);
	}

	public Criteria interpret(ObjectFilter filter) throws QueryException {
		Validate.notNull(filter, "Element filter must not be null.");

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Interpreting query '{}', query on trace level.", new Object[] { filter.getClass() });
			LOGGER.trace("Query filter:\n{}", new Object[] { filter.dump() });
		}

		try {
			Criterion criterion = interpret(filter, false);

			Criteria criteria = getCriteria(null);
			criteria.add(criterion);

			return criteria;
		} catch (QueryException ex) {
			LOGGER.trace(ex.getMessage(), ex);
			throw ex;
		} catch (Exception ex) {
			LOGGER.trace(ex.getMessage(), ex);
			throw new QueryException(ex.getMessage(), ex);
		}
	}

	public Criterion interpret(ObjectFilter filter, boolean pushNot) throws QueryException {
		// todo fix operation choosing and initialization...

		Op operation = getOpType(filter);
		return operation.interpret(filter, pushNot);

	}

	private Op getOpType(ObjectFilter filter) throws QueryException {
		if (LogicalFilter.class.isAssignableFrom(filter.getClass())) {
			return new LogicalOp(this);
		}
		if (ValueFilter.class.isAssignableFrom(filter.getClass())) {
			return new SimpleOp(this);
		}
		if (OrgFilter.class.isAssignableFrom(filter.getClass())) {
			return new TreeOp(this);
		}
		throw new QueryException("Unsupported query filter '" + filter.getClass().getSimpleName() + "'.");
	}

	public Class<? extends ObjectType> getType() {
		return type;
	}

	public PrismContext getPrismContext() {
		return prismContext;
	}

	public ItemDefinition findDefinition(Element path, QName name) {
		LOGGER.trace("Looking for '{}' definition on path '{}'",
				new Object[] { name, (path != null ? DOMUtil.serializeDOMToString(path) : null) });
		SchemaRegistry registry = prismContext.getSchemaRegistry();
		PrismObjectDefinition objectDef = registry.findObjectDefinitionByCompileTimeClass(type);

		ItemPath propertyPath = createPropertyPath(path);
		if (propertyPath == null) {
			propertyPath = new ItemPath();
		}

		List<ItemPathSegment> segments = propertyPath.getSegments();
		segments.add(new NameItemPathSegment(name));
		propertyPath = new ItemPath(segments);
		LOGGER.trace("Checking item definition on path {}", new Object[] { propertyPath });
		ItemDefinition def = objectDef.findItemDefinition(propertyPath);
		if (def != null) {
			return def;
		}

		LOGGER.trace("Definition not found, checking global definitions.");
		ItemDefinition definition = registry.findItemDefinitionByElementName(name);
		LOGGER.trace("Found definition {}", definition);
		return definition;
	}

	public ItemPath createPropertyPath(Element path) {
		ItemPath propertyPath = null;
		if (path != null && StringUtils.isNotEmpty(path.getTextContent())) {
			propertyPath = new XPathHolder(path).toPropertyPath();
		}

		return propertyPath;
	}

	public String createAlias(QName qname) {
		String prefix = Character.toString(qname.getLocalPart().charAt(0)).toLowerCase();
		int index = 1;

		String alias = prefix;
		while (hasAlias(alias)) {
			alias = prefix + Integer.toString(index);
			index++;

			if (index > 20) {
				throw new IllegalStateException("Alias index for segment '" + qname
						+ "' is more than 20? Should not happen.");
			}
		}

		return alias;
	}

	public Criteria getCriteria(ItemPath path) {
		return criterions.get(path);
	}

	public void setCriteria(ItemPath path, Criteria criteria) {
		Validate.notNull(criteria, "Criteria must not be null.");
		if (criterions.containsKey(path)) {
			throw new IllegalArgumentException("Already has criteria with this path '" + path + "'");
		}

		criterions.put(path, criteria);
	}

	public String getAlias(ItemPath path) {
		return aliases.get(path);
	}

	public void setAlias(ItemPath path, String alias) {
		Validate.notNull(alias, "Alias must not be null.");
		if (aliases.containsValue(alias)) {
			throw new IllegalArgumentException("Already has alias '" + alias + "' with this path '" + path + "'.");
		}

		aliases.put(path, alias);
	}

	public boolean hasAlias(String alias) {
		return aliases.containsValue(alias);
	}
}
