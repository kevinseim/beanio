package org.beanio.parser.inlinemaps;

import java.util.Map;

@SuppressWarnings("rawtypes")
public class Job {

	private String id;
	private Map codes;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Map getCodes() {
		return codes;
	}
	public void setCodes(Map codes) {
		this.codes = codes;
	}
}
