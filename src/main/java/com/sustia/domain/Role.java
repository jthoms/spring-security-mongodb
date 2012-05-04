package com.sustia.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Role {
	
	@Id
	private String id;
		
	public Role() {
		super();
	}
	
	public Role(String id) {
		super();
		this.setId(id);
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public boolean equals(Object obj) {
        if (!(obj instanceof Role)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        Role rhs = (Role) obj;
        return new EqualsBuilder().append(id, rhs.id).isEquals();
    }

	public int hashCode() {
        return new HashCodeBuilder().append(id).toHashCode();
	}

}
