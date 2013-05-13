package org.generationcp.middleware.v2.domain;

import java.util.List;

import org.generationcp.middleware.v2.util.Debug;

public class Term {

	private int id;
	
	private String name;
	
	private String definition;
	
	private List<NameSynonym> nameSynonyms;

	public Term() { }
	
	public Term(int id, String name, String definition) {
		this.id = id;
		this.name = name;
		this.definition = definition;
	}

	public Term(int id, String name, String definition, List<NameSynonym> nameSynonyms) {
		this.id = id;
		this.name = name;
		this.definition = definition;
		this.nameSynonyms = nameSynonyms;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}
	
	public List<NameSynonym> getNameSynonyms() {
		return nameSynonyms;
	}

	public void setNameSynonyms(List<NameSynonym> nameSynonyms) {
		this.nameSynonyms = nameSynonyms;
	}

	public void print(int indent) {
		Debug.println(indent, "Id: " + getId());
		Debug.println(indent, "Name: " + getName());
	    Debug.println(indent, "Definition: " + getDefinition());
	    if (nameSynonyms != null) {
	    	Debug.println(indent, "NameSynonyms: " + nameSynonyms);
	    }
	}
	
	@Override
	public int hashCode() {
		return getId();
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Term)) return false;
		Term other = (Term) obj;
		return getId() == other.getId();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Term [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", definition=");
		builder.append(definition);
		if (nameSynonyms != null) {
			builder.append(", nameSynonyms=");
			builder.append(nameSynonyms);
		}
		builder.append("]");
		return builder.toString();
	}
}
