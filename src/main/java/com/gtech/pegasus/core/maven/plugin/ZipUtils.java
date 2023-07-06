package com.gtech.pegasus.core.maven.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import lombok.Setter;

public class ZipUtils {

    private List<File> libList;
    private List<File> confList;
    
    @Setter
    private GGServerApplicationPluginInfos infos;

    public ZipUtils() {
    	libList = new ArrayList < File > ();
    	confList = new ArrayList < File > ();
    }

    public File zipIt(String zipFile) {
        byte[] buffer = new byte[1024];
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos);

            System.out.println("Output to Zip : " + zipFile);
            FileInputStream in = null;

            for (File file: this.libList) {
                System.out.println("Lib Added : " + file.getName());
                
                ZipEntry ze = new ZipEntry("lib" + File.separator + file.getName());
                zos.putNextEntry(ze);
                try {
                    in = new FileInputStream(file.getAbsolutePath());
                    int len;
                    while ((len = in .read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                } finally {
                	if( in != null )
                		in.close();
                }
            }
            
            for (File file: this.confList) {
                System.out.println("Conf Added : " + file.getName());
                
                ZipEntry ze = new ZipEntry("conf" + File.separator + file.getName());
                zos.putNextEntry(ze);
                try {
                    in = new FileInputStream(file.getAbsolutePath());
                    int len;
                    while ((len = in .read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                } finally {
                	if( in != null )
                		in.close();
                }
            }
            
//            if( this.infos != null ) {
            	System.out.println("Infos Added : " + this.infos.getFile().getName());
	            ZipEntry ze = new ZipEntry(this.infos.getFile().getName());
	            zos.putNextEntry(ze);
	            try {
	                in = new FileInputStream(this.infos.getFile().getAbsolutePath());
	                int len;
	                while ((len = in .read(buffer)) > 0) {
	                    zos.write(buffer, 0, len);
	                }
	            } finally {
                	if( in != null )
                		in.close();
	            }
//            }

            zos.closeEntry();
            System.out.println("Folder successfully compressed");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		return new File( zipFile );
    }

    public void addLib(File lib) {
        // add file only
        if (lib.isFile()) {
            libList.add(lib);
        }

    }
    
    public void addConf(File conf) {
        // add file only
        if (conf.isFile()) {
            confList.add(conf);
        }

    }

}