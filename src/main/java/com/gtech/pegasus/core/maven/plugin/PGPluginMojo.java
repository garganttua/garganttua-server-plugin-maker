package com.gtech.pegasus.core.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 */
@Mojo(name = "peg", threadSafe = true, requiresDependencyResolution = ResolutionScope.RUNTIME, requiresDependencyCollection = ResolutionScope.RUNTIME)
public class PGPluginMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.build.directory}")
	private File buildDirectory;
	
	@Parameter(defaultValue = "${project.basedir}")
	private File basedir;

	@Parameter(defaultValue = "${project}", readonly = true)
	private MavenProject mavenProject;

	@Parameter(property = "dependencies")
    private List<PGDependency> dependencies;
	
	@Parameter(property = "files")
    private List<String> files;
	
	
	public void execute() throws MojoExecutionException {
		ZipUtils zip = new ZipUtils();
		PGApplicationPluginInfos infos = new PGApplicationPluginInfos();
		List<PGDependency> libs = new ArrayList<PGDependency>();
		getLog().info("Build dir "+this.buildDirectory.getAbsolutePath());
		for (final Artifact artifact : mavenProject.getArtifacts()) {
			PGDependency e = new PGDependency(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), "included", artifact);
			
			for( PGDependency econf: this.dependencies ) {
				if( econf.equals(e) ) {
					e.setType(econf.getType());
					break;
				}
			}
			libs.add(e);
			
			System.out.println(e.toXml());
		}
		
		//create build directory
		String buildDir = this.buildDirectory.getAbsolutePath()+File.separator+this.mavenProject.getArtifactId()+"-"+this.mavenProject.getVersion();
		String confDir = this.buildDirectory.getAbsolutePath()+File.separator+this.mavenProject.getArtifactId()+"-"+this.mavenProject.getVersion()+File.separator+"conf";
		String libDir = this.buildDirectory.getAbsolutePath()+File.separator+this.mavenProject.getArtifactId()+"-"+this.mavenProject.getVersion()+File.separator+"lib";
		File f = new File(buildDir);
		if( f.exists() ) {
			f.delete();
		} 
		f.mkdirs();
		
		File confDirF = new File(confDir);
		File libDirF = new File(libDir);
		
		confDirF.mkdir();
		libDirF.mkdir();
		
		getLog().info("Created "+f.getAbsolutePath());
		getLog().info("Created "+confDirF.getAbsolutePath());
		getLog().info("Created "+libDirF.getAbsolutePath());
		
		//Copy the libs
		for( PGDependency lib: libs) {
			if( lib.getType().equals("included") ) {
				File libf = lib.getArtifact().getFile();
				
				File newLib = new File(libDir+File.separator+libf.getName());
	
				try {
					 com.google.common.io.Files.copy(libf, newLib);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				zip.addLib(lib.getArtifact().getFile());
			} else if( lib.getType().equals("provided") ) {
				
				if( lib.getArtifact().getFile().getName().endsWith(".ped") ) {
					infos.addRequiredPlugin(lib.getArtifact());
				} else {
					infos.addRequiredLib(lib.getArtifact());
				}
			}
		}
		
		//Copy the configuration 
		for( String configuration: this.files ) {
			
			File configFile = new File(this.basedir.getAbsolutePath()+File.separator+configuration);
			getLog().info("adding configuration "+configFile.getAbsolutePath());
			
			File newConfig = new File(confDir+File.separator+configFile.getName());
			
			try {
				 com.google.common.io.Files.copy(configFile, newConfig);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			zip.addConf(configFile);
		}
		
		
		//Generate the descriptor file
		infos.setInfosVersion("1.0");
		infos.setCreatedBy("pegasus-core-maven-plugin-1.0.0-SNAPSHOT");
		infos.setIssuer("Gtech");
		infos.setName(this.mavenProject.getArtifactId());
		infos.setVersion(this.mavenProject.getVersion());
		
		try {
			File infosFile = infos.write(f);
			zip.setInfos(infos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Generate archive
		File artifact = zip.zipIt(buildDir+".peg");
		
		Build build = new Build();
		build.setFinalName(buildDir);
		build.setDirectory(this.buildDirectory.getAbsolutePath());
		
		this.mavenProject.setBuild(build);
		this.mavenProject.getArtifact().setFile(artifact);
		
	}
	
	
}