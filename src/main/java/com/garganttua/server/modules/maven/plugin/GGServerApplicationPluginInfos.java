package com.garganttua.server.modules.maven.plugin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

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
		Manifest manifest = new Manifest();
		
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		manifest.getMainAttributes().put(new Attributes.Name("Descriptor-Version"), this.infosVersion);
		manifest.getMainAttributes().put(new Attributes.Name("Created-By"), this.createdBy);
		manifest.getMainAttributes().put(new Attributes.Name("Issuer"), this.issuer);
		manifest.getMainAttributes().put(new Attributes.Name("Plugin-Title"), this.name);
		manifest.getMainAttributes().put(new Attributes.Name("Plugin-Version"), this.version);

		StringBuilder sb = new StringBuilder();
		int i = 1;
		for(Artifact artifact: this.requiredPlugins) {
			sb.append(artifact.getGroupId());
			sb.append(":");
			sb.append(artifact.getArtifactId());
			sb.append(":");
			sb.append(artifact.getVersion());
			if( i != this.requiredPlugins.size() ) {
				sb.append(" ");
				i++;
			}
		}
		manifest.getMainAttributes().put(new Attributes.Name("Required-Plugins"), sb.toString());

		sb = new StringBuilder();
		i=1;
		for(Artifact artifact: this.requiredLibs) {
			sb.append(artifact.getGroupId());
			sb.append(":");
			sb.append(artifact.getArtifactId());
			sb.append(":");
			sb.append(artifact.getVersion());
			if( i != this.requiredLibs.size() ) {
				sb.append(" ");
				i++;
			}
		}
		manifest.getMainAttributes().put(new Attributes.Name("Required-Libs"), sb.toString());
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		try {
			manifest.write(os);	
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
		String str = os.toString(StandardCharsets.UTF_8);

		System.out.println("Generated Manifest : \n"+str);
		return str;
	}

}
