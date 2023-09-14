package com.garganttua.server.modules.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * Descriptor-Version: 1.0 Created-By: Maven Garganttua Plugins Plugin 1.0.0
 * Issuer: Gtech Inc Plugin-Title: garganttua-backend-plugin-synchronization
 * Plugin-Version: 0.0.1-SNAPSHOT Required-Plugin: Required-Lib:
 *
 */
@Getter
@Setter
public class GGServerApplicationPluginInfos {

	private String infosVersion;

	private String createdBy;

	private String issuer;

	private String name;

	private String version;

	private List<Artifact> requiredPlugins = new ArrayList<Artifact>();
	
	public void addRequiredPlugin(Artifact artifact) {
		this.requiredPlugins.add(artifact);
	}

	private List<Artifact> requiredLibs = new ArrayList<Artifact>();

	@Getter
	private File file;
	
	public void addRequiredLib(Artifact artifact) {
		this.requiredLibs.add(artifact);
	}

	public File write(File folder) throws IOException {
		// infos.ggd
		if (!folder.exists()) {
			folder.mkdirs();
		}

		this.file = new File(folder.getAbsolutePath() + File.separator + "infos.ggd");
		System.out.println("Writing file "+this.file.getAbsolutePath());
		
		Files.write(this.file.toPath(), this.toString().getBytes());
		return this.file;

	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Descriptor-Version: ");
		sb.append(this.infosVersion);
		sb.append("\nCreated-By: ");
		sb.append(this.createdBy);
		sb.append("\nIssuer: ");
		sb.append(this.issuer);
		sb.append("\nPlugin-Title: ");
		sb.append(this.name);
		sb.append("\nPlugin-Version: ");
		sb.append(this.version);
		sb.append("\nRequired-Plugins: ");
		int i = 1;
		for(Artifact artifact: this.requiredPlugins) {
			sb.append(artifact.getGroupId());
			sb.append(":");
			sb.append(artifact.getArtifactId());
			sb.append(":");
			sb.append(artifact.getVersion());
			if( i != this.requiredPlugins.size() ) {
				sb.append(";");
				i++;
			}
		}
		i=1;
		sb.append("\nRequired-Libs: ");
		for(Artifact artifact: this.requiredLibs) {
			sb.append(artifact.getGroupId());
			sb.append(":");
			sb.append(artifact.getArtifactId());
			sb.append(":");
			sb.append(artifact.getVersion());
			if( i != this.requiredLibs.size() ) {
				sb.append(";");
				i++;
			}
		}
		return sb.toString();
	}

}
