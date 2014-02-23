/*
 * Copyright (c) 2010-2013 Evolveum
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

package com.evolveum.midpoint.repo.sql.data.common;

import com.evolveum.midpoint.repo.sql.util.RUtil;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author lazyman
 */
@Entity
@Table(name = "m_org_closure")
@org.hibernate.annotations.Table(appliesTo = "m_org_closure",
        indexes = {@Index(name = "iAncestorDepth", columnNames = {"ancestor_id","ancestor_oid","depthValue"})})
public class ROrgClosure implements Serializable {

    private Long id;

    private RObject ancestor;
    private String ancestorOid;
    private Short ancestorId;

    private RObject descendant;
    private String descendantOid;
    private Short descendantId;

    private int depth;

    public ROrgClosure() {
    }

    public ROrgClosure(String ancestorOid, String descendantOid, int depth) {
        if (ancestorOid != null) {
            this.ancestorOid = ancestorOid;
            this.ancestorId = 0;
        }
        if (descendantOid != null) {
            this.descendantOid = descendantOid;
            this.descendantId = 0;
        }
        this.depth = depth;
    }

    public ROrgClosure(RObject ancestor, RObject descendant, int depth) {
        this.ancestor = ancestor;
        this.descendant = descendant;
        this.depth = depth;
    }

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumns({@JoinColumn(name = "ancestor_oid", referencedColumnName = "oid"),
            @JoinColumn(name = "ancestor_id", referencedColumnName = "id")})
    @ForeignKey(name = "fk_ancestor")
    public RObject getAncestor() {
        return ancestor;
    }

    @Column(name = "ancestor_id", insertable = false, updatable = false)
    public Short getAncestorId() {
        if (ancestorId == null && ancestor != null) {
            ancestorId = ancestor.getId();
        }
        return ancestorId;
    }

    @Column(name = "ancestor_oid", length = RUtil.COLUMN_LENGTH_OID, insertable = false, updatable = false)
    public String getAncestorOid() {
        if (ancestorOid == null && ancestor.getOid() != null) {
            ancestorOid = ancestor.getOid();
        }
        return ancestorOid;
    }

    public void setAncestor(RObject ancestor) {
        this.ancestor = ancestor;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumns({@JoinColumn(name = "descendant_oid", referencedColumnName = "oid"),
            @JoinColumn(name = "descendant_id", referencedColumnName = "id")})
    @ForeignKey(name = "fk_descendant")
    public RObject getDescendant() {
        return descendant;
    }

    @Column(name = "descendant_id", insertable = false, updatable = false)
    public Short getDescendantId() {
        if (descendantId == null && descendant != null) {
            descendantId = descendant.getId();
        }
        return descendantId;
    }

    @Column(name = "descendant_oid", length = RUtil.COLUMN_LENGTH_OID, insertable = false, updatable = false)
    public String getDescendantOid() {
        if (descendantOid == null && descendant.getOid() != null) {
            descendantOid = descendant.getOid();
        }
        return descendantOid;
    }

    public void setDescendant(RObject descendant) {
        this.descendant = descendant;
    }

    @Column(name = "depthValue")
    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setAncestorId(Short ancestorId) {
        this.ancestorId = ancestorId;
    }

    public void setAncestorOid(String ancestorOid) {
        this.ancestorOid = ancestorOid;
    }

    public void setDescendantId(Short descendantId) {
        this.descendantId = descendantId;
    }

    public void setDescendantOid(String descendantOid) {
        this.descendantOid = descendantOid;
    }

    @Override
    public int hashCode() {
        int result = depth;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        ROrgClosure that = (ROrgClosure) obj;

        if (depth != that.depth)
            return false;
        if (ancestor != null ? !ancestor.equals(that.ancestor) : that.ancestor != null)
            return false;
        if (descendant != null ? !descendant.equals(that.descendant) : that.descendant != null)
            return false;

        return true;
    }
}
