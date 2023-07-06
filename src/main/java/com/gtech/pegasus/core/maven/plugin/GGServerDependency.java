package com.gtech.pegasus.core.maven.plugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugins.annotations.Parameter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class GGServerDependency implements Comparable<GGServerDependency>{

	@Getter
	@Setter
	@Parameter(property = "groupId")
    private String groupId;
	
	@Getter
	@Setter
	@Parameter(property = "artifactId")
    private String artifactId;

	@Getter
	@Setter
	@Parameter(property = "type")
	//exclusion/provided
	private String type = "";
	
	@Getter
	@Setter
	@Parameter(property = "version")
	private String version = "";
	
	@Setter
	@Getter
	private Artifact artifact;

	@Override
	public boolean equals(Object obj) {
		return compareTo((GGServerDependency) obj)==0?true:false;
	}
	
	@Override
	public int compareTo(GGServerDependency o) {
		if( o.artifactId.equals(this.artifactId) && o.groupId.equals(this.groupId) ) {
			return 0;
		}
		return -1;
	}
	
	@Override
	public String toString() {
		return this.groupId+":"+this.artifactId;
	}

	public String toXml() {
		return "<dependency>\n   <groupId>"+this.groupId+"</groupId>\n   <artifactId>"+this.artifactId+"</artifactId>\n   <type>included</type>\n</dependency>";
	}
	
}
