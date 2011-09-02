package org.ops4j.pax.url.mvn.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

public class MetadataUrlTest {

    protected static File basedir = new File( System.getProperty( "basedir", "." ) );

    protected static File builddir = new File( basedir, "target" );

    @BeforeClass
    public static void beforeClass()
        throws IOException {
        
        System.setProperty( "basedir", System.getProperty( "basedir", "." ) );
        
        URL.setURLStreamHandlerFactory( new PaxURLStreamHandlerFactory() );

        String repo = "file:///" + builddir.getAbsolutePath();
        System.setProperty( "org.ops4j.pax.url.mvn.defaultRepositories", repo );

        createRepository();
    }

    @Test
    public void urlWithMetadata()
        throws IOException {

        tryIt( "mvn:group/artifact////metadata" );
    }

    @Test(expected = MalformedURLException.class)
    public void urlWithoudGroup()
        throws IOException {

        tryIt( "mvn:/artifact////metadata" );

    }

    @Test(expected = MalformedURLException.class)
    public void urlBogusMetadata()
        throws IOException {

        tryIt( "mvn:group/artifact////any" );

    }

    @Test
    public void urlWithVersion()
        throws IOException {

        tryIt( "mvn:group/artifact/version-SNAPSHOT///metadata" );
    }

    @Test
    public void urlLatestInRepo()
        throws IOException {

        tryIt( "mvn:group/artifact/someVersion///metadata" );
    }

    
    private void tryIt( String urlString )
        throws IOException {

        URL url = new URL( urlString );

        URLConnection urlConnection = url.openConnection();

        Assert.assertNotNull( urlConnection.getInputStream() );

    }

    /**
     * Repository:
     * 
     * group/artifact/maven-metadata.xml 
     * group/artifact/version-SNAPSHOT/maven-metadata.xml 
     * 
     * @throws IOException
     * 
     */
    private static void createRepository()
        throws IOException {
        
        File group = new File( builddir, "group" );
        File artifact = new File( group, "artifact" );
        File version = new File( artifact, "version-SNAPSHOT" );

        version.mkdirs();

        storeMetadata( artifact ); 
        storeMetadata( version ); 

    }

    private static void storeMetadata( File dir )
        throws IOException {

        File f = new File( dir, "maven-metadata.xml" );
        String content = "<metadata><groupId>group</groupId><artifactId>artifact</artifactId><versioning><latest>1.1.0</latest><release/><versions><version>1.1.0</version></versions><lastUpdated>20110714123053</lastUpdated></versioning></metadata>";

        Writer out = new OutputStreamWriter( new FileOutputStream( f ) );
        try {
            out.write( content );
        }
        finally {
            out.close();
        }

    }

    static class PaxURLStreamHandlerFactory
        implements URLStreamHandlerFactory {
        private static final String MVN_URI_PREFIX = "mvn";

        public URLStreamHandler createURLStreamHandler( String protocol ) {
            if ( protocol.equals( MVN_URI_PREFIX ) ) {
                return new org.ops4j.pax.url.mvn.Handler();
            }
            return null;
        }
    }

}
