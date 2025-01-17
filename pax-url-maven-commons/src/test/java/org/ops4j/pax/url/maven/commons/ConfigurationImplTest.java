/*
 * Copyright 2007 Alin Dreghiciu.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.url.maven.commons;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.Test;
import org.ops4j.io.FileUtils;
import org.ops4j.util.property.PropertyResolver;

public class ConfigurationImplTest
{

    private static final String PID = "test.pid";

    @Test( expected = IllegalArgumentException.class )
    public void constructorWithNullResolver()
    {
        new MavenConfigurationImpl( null, PID );
    }

    @Test
    public void getCertificateCheck()
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.certificateCheck" ) ).andReturn( "true" );
        replay( propertyResolver );
        MavenConfiguration config = new MavenConfigurationImpl( propertyResolver, PID );
        assertEquals( "Certificate check", true, config.getCertificateCheck() );
        verify( propertyResolver );
    }

    @Test
    public void getDefaultCertificateCheck()
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.certificateCheck" ) ).andReturn( null );
        replay( propertyResolver );
        MavenConfiguration config = new MavenConfigurationImpl( propertyResolver, PID );
        assertEquals( "Certificate check", false, config.getCertificateCheck() );
        verify( propertyResolver );
    }

    @Test
    public void getSettingsAsURL()
        throws MalformedURLException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.settings" ) ).andReturn(
            "file:somewhere/settings.xml"
        );
        replay( propertyResolver );
        MavenConfiguration config = new MavenConfigurationImpl( propertyResolver, PID );
        assertEquals( "Settings", new URL( "file:somewhere/settings.xml" ), config.getSettingsFileUrl() );
        verify( propertyResolver );
    }

    @Test
    public void getSettingsAsFilePath()
        throws MalformedURLException, FileNotFoundException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        File validSettings = FileUtils.getFileFromClasspath( "configuration/settings.xml" );
        expect( propertyResolver.get( "test.pid.settings" ) ).andReturn(
            validSettings.getAbsolutePath()
        );
        replay( propertyResolver );
        MavenConfiguration config = new MavenConfigurationImpl( propertyResolver, PID );
        assertEquals( "Settings", validSettings.toURL(), config.getSettingsFileUrl() );
        verify( propertyResolver );
    }

    /**
     * Test that a malformed url will not trigger an exception ( will be skipped)
     *
     * @throws MalformedURLException not expected
     */
    @Test
    public void getSettingsAsMalformedURL()
        throws MalformedURLException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.settings" ) ).andReturn( "noprotocol:settings.xml" );
        replay( propertyResolver );
        new MavenConfigurationImpl( propertyResolver, PID ).getSettingsFileUrl();
    }

    @Test
    public void getSettingsWithoutPropertySet()
        throws MalformedURLException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.settings" ) ).andReturn( null );
        replay( propertyResolver );
        MavenConfiguration config = new MavenConfigurationImpl( propertyResolver, PID );
        assertEquals( "Settings", null, config.getSettingsFileUrl() );
        verify( propertyResolver );
    }

    @Test
    public void getRepositoriesWithOneRepository()
        throws MalformedURLException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.repositories" ) ).andReturn( "file:repository1/@id=repository1" );
        expect( propertyResolver.get( "test.pid.localRepository" ) ).andReturn( null );
        replay( propertyResolver );
        MavenConfiguration config = new MavenConfigurationImpl( propertyResolver, PID );
        List<MavenRepositoryURL> repositories = config.getRepositories();
        assertNotNull( "Repositories is null", repositories );
        assertEquals( "Repositories size", 1, repositories.size() );
        assertEquals( "Repository", new URL( "file:repository1/" ), repositories.get( 0 ).getURL() );
        verify( propertyResolver );
    }

    @Test
    public void getRepositoriesWithoutSlash()
        throws MalformedURLException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.repositories" ) ).andReturn( "file:repository1@id=repository1" );
        expect( propertyResolver.get( "test.pid.localRepository" ) ).andReturn( null );
        replay( propertyResolver );
        MavenConfiguration config = new MavenConfigurationImpl( propertyResolver, PID );
        List<MavenRepositoryURL> repositories = config.getRepositories();
        assertNotNull( "Repositories is null", repositories );
        assertEquals( "Repositories size", 1, repositories.size() );
        assertEquals( "Repository", new URL( "file:repository1/" ), repositories.get( 0 ).getURL() );
        verify( propertyResolver );
    }

    @Test
    public void getRepositoriesWithSlash()
        throws MalformedURLException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.repositories" ) ).andReturn( "file:repository1/@id=repository1" );
        expect( propertyResolver.get( "test.pid.localRepository" ) ).andReturn( null );
        replay( propertyResolver );
        MavenConfiguration config = new MavenConfigurationImpl( propertyResolver, PID );
        List<MavenRepositoryURL> repositories = config.getRepositories();
        assertNotNull( "Repositories is null", repositories );
        assertEquals( "Repositories size", 1, repositories.size() );
        assertEquals( "Repository", new URL( "file:repository1/" ), repositories.get( 0 ).getURL() );
        verify( propertyResolver );
    }

    @Test
    public void getRepositoriesWithBackSlash()
        throws MalformedURLException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.repositories" ) ).andReturn( "file:repository1\\@id=repository1" );
        expect( propertyResolver.get( "test.pid.localRepository" ) ).andReturn( null );
        replay( propertyResolver );
        MavenConfiguration config = new MavenConfigurationImpl( propertyResolver, PID );
        List<MavenRepositoryURL> repositories = config.getRepositories();
        assertNotNull( "Repositories is null", repositories );
        assertEquals( "Repositories size", 1, repositories.size() );
        assertEquals( "Repository", new URL( "file:repository1\\" ), repositories.get( 0 ).getURL() );
        verify( propertyResolver );
    }

    @Test
    public void getRepositoriesWithAtSnapshotsAndAtNoReleases()
        throws MalformedURLException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.repositories" ) ).andReturn(
            "http:repository1@snapshots@noreleases@id=repository1"
        );
        expect( propertyResolver.get( "test.pid.localRepository" ) ).andReturn( null );
        replay( propertyResolver );
        MavenConfiguration config = new MavenConfigurationImpl( propertyResolver, PID );
        List<MavenRepositoryURL> repositories = config.getRepositories();
        assertTrue( "Repository as snapshot enabled:", repositories.get( 0 ).isSnapshotsEnabled() );
        assertFalse( "Repository as release enabled:", repositories.get( 0 ).isReleasesEnabled() );
        verify( propertyResolver );
    }

    @Test
    public void getRepositoriesWithAtSnapshots()
        throws MalformedURLException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.repositories" ) ).andReturn(
            "http:repository1@snapshots@id=repository1"
        );
        expect( propertyResolver.get( "test.pid.localRepository" ) ).andReturn( null );
        replay( propertyResolver );
        MavenConfiguration config = new MavenConfigurationImpl( propertyResolver, PID );
        List<MavenRepositoryURL> repositories = config.getRepositories();
        assertTrue( "Repository as snapshot enabled:", repositories.get( 0 ).isSnapshotsEnabled() );
        assertTrue( "Repository as release enabled:", repositories.get( 0 ).isReleasesEnabled() );
        verify( propertyResolver );
    }

    @Test
    public void getRepositoriesWithAtNoReleases()
        throws MalformedURLException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.repositories" ) ).andReturn(
            "http:repository1@noreleases@id=repository1"
        );
        expect( propertyResolver.get( "test.pid.localRepository" ) ).andReturn( null );
        replay( propertyResolver );
        MavenConfiguration config = new MavenConfigurationImpl( propertyResolver, PID );
        List<MavenRepositoryURL> repositories = config.getRepositories();
        assertFalse( "Repository as snapshot not enabled:", repositories.get( 0 ).isSnapshotsEnabled() );
        assertFalse( "Repository as release not enabled:", repositories.get( 0 ).isReleasesEnabled() );
        verify( propertyResolver );
    }

    @Test
    public void getRepositoriesWithMoreRepositories()
        throws MalformedURLException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.repositories" ) ).andReturn(
            "file:repository1/@id=repository1,file:repository2/@id=repository2"
        );
        expect( propertyResolver.get( "test.pid.localRepository" ) ).andReturn( null );
        replay( propertyResolver );
        MavenConfiguration config = new MavenConfigurationImpl( propertyResolver, PID );
        List<MavenRepositoryURL> repositories = config.getRepositories();
        assertNotNull( "Repositories is null", repositories );
        assertEquals( "Repositories size", 2, repositories.size() );
        assertEquals( "Repository 1", new URL( "file:repository1/" ), repositories.get( 0 ).getURL() );
        assertEquals( "Repository 2", new URL( "file:repository2/" ), repositories.get( 1 ).getURL() );
        verify( propertyResolver );
    }

    @Test
    public void getRepositoriesFromSettings()
        throws MalformedURLException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.repositories" ) ).andReturn( null );
        expect( propertyResolver.get( "test.pid.localRepository" ) ).andReturn( null );
        MavenSettings settings = createMock( MavenSettings.class );
        expect( settings.getRepositories() ).andReturn( "file:repository1/@id=repository1" );
        expect( settings.getLocalRepository() ).andReturn( null );
        replay( propertyResolver, settings );
        MavenConfigurationImpl config = new MavenConfigurationImpl( propertyResolver, PID );
        config.setSettings( settings );
        List<MavenRepositoryURL> repositories = config.getRepositories();
        assertNotNull( "Repositories is null", repositories );
        assertEquals( "Repositories size", 1, repositories.size() );
        assertEquals( "Repository", new URL( "file:repository1/" ), repositories.get( 0 ).getURL() );
        verify( propertyResolver );
    }

    @Test
    public void getRepositoriesFromConfigAndSettings()
        throws MalformedURLException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.repositories" ) ).andReturn( "+file:repository1/@id=repository1" );
        expect( propertyResolver.get( "test.pid.localRepository" ) ).andReturn( null );
        MavenSettings settings = createMock( MavenSettings.class );
        expect( settings.getRepositories() ).andReturn( "file:repository2/@id=repository2" );
        expect( settings.getLocalRepository() ).andReturn( null );
        replay( propertyResolver, settings );
        MavenConfigurationImpl config = new MavenConfigurationImpl( propertyResolver, PID );
        config.setSettings( settings );
        List<MavenRepositoryURL> repositories = config.getRepositories();
        assertNotNull( "Repositories is null", repositories );
        assertEquals( "Repositories size", 2, repositories.size() );
        assertEquals( "Repository 1", new URL( "file:repository1/" ), repositories.get( 0 ).getURL() );
        assertEquals( "Repository 2", new URL( "file:repository2/" ), repositories.get( 1 ).getURL() );
        verify( propertyResolver );
    }

    @Test
    public void getRepositoriesWithLocalRepository()
        throws MalformedURLException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.repositories" ) ).andReturn( "file:repository1/@id=repository1" );
        expect( propertyResolver.get( "test.pid.localRepository" ) ).andReturn(
            "file:localRepository/"
        );
        replay( propertyResolver );
        MavenConfiguration config = new MavenConfigurationImpl( propertyResolver, PID );
        List<MavenRepositoryURL> repositories = config.getRepositories();
        assertNotNull( "Repositories is null", repositories );
        assertEquals( "Repositories size", 2, repositories.size() );
        assertEquals( "Local repository", new URL( "file:localRepository/" ), repositories.get( 0 ).getURL()
        );
        assertEquals( "Repository", new URL( "file:repository1/" ), repositories.get( 1 ).getURL() );
        verify( propertyResolver );
    }

    @Test
    public void getLocalRepositoryAsURL()
        throws MalformedURLException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.localRepository" ) ).andReturn(
            "file:somewhere/localrepository/"
        );
        replay( propertyResolver );
        MavenConfiguration config = new MavenConfigurationImpl( propertyResolver, PID );
        assertEquals( "Local repository",
                      new URL( "file:somewhere/localrepository/" ),
                      config.getLocalRepository().getURL()
        );
        verify( propertyResolver );
    }

    @Test
    public void getLocalRepositoryAsURLWithoutSlash()
        throws MalformedURLException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.localRepository" ) ).andReturn(
            "file:somewhere/localrepository"
        );
        replay( propertyResolver );
        MavenConfiguration config = new MavenConfigurationImpl( propertyResolver, PID );
        assertEquals( "Local repository",
                      new URL( "file:somewhere/localrepository/" ),
                      config.getLocalRepository().getURL()
        );
        verify( propertyResolver );
    }

    @Test
    public void getLocalRepositoryAsURLWithBackSlash()
        throws MalformedURLException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.localRepository" ) ).andReturn(
            "file:somewhere/localrepository\\"
        );
        replay( propertyResolver );
        MavenConfiguration config = new MavenConfigurationImpl( propertyResolver, PID );
        assertEquals( "Local repository",
                      new URL( "file:somewhere/localrepository\\" ),
                      config.getLocalRepository().getURL()
        );
        verify( propertyResolver );
    }

    @Test
    public void getLocalRepositoryAsFilePath()
        throws MalformedURLException, FileNotFoundException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        File valid = FileUtils.getFileFromClasspath( "configuration/localrepository" );
        expect( propertyResolver.get( "test.pid.localRepository" ) ).andReturn(
            valid.getAbsolutePath()
        );
        replay( propertyResolver );
        MavenConfiguration config = new MavenConfigurationImpl( propertyResolver, PID );
        assertEquals( "Local repository",
                      valid.toURL(),
                      config.getLocalRepository().getURL()
        );
        verify( propertyResolver );
    }

    @Test
    public void getLocalRepositoryAsFilePathWithoutSlash()
        throws MalformedURLException, FileNotFoundException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        File valid = FileUtils.getFileFromClasspath( "configuration/localrepository" );
        File validWithSlash = FileUtils.getFileFromClasspath( "configuration/localrepository/" );
        expect( propertyResolver.get( "test.pid.localRepository" ) ).andReturn(
            valid.getAbsolutePath()
        );
        replay( propertyResolver );
        MavenConfiguration config = new MavenConfigurationImpl( propertyResolver, PID );
        assertEquals( "Local repository",
                      validWithSlash.toURL(),
                      config.getLocalRepository().getURL()
        );
        verify( propertyResolver );
    }

    /**
     * Test that an url that is malformed will not trigger an exception.
     *
     * @throws MalformedURLException never
     */
    @Test
    public void getLocalRepositoryAsMalformedURL()
        throws MalformedURLException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.localRepository" ) ).andReturn(
            "noprotocol://localrepository"
        );
        replay( propertyResolver );
        new MavenConfigurationImpl( propertyResolver, PID ).getLocalRepository();
    }

    /**
     * Test that a path to a file that does not exist will not trigger an exception.
     *
     * @throws MalformedURLException never
     */
    @Test
    public void getLocalRepositoryToAnInexistentDirectory()
        throws MalformedURLException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.localRepository" ) ).andReturn(
            "c:/this/should/be/an/inexistent/directory"
        );
        replay( propertyResolver );
        new MavenConfigurationImpl( propertyResolver, PID ).getLocalRepository();
    }

    @Test
    public void getLocalRepositoryWithoutPropertySet()
        throws MalformedURLException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.localRepository" ) ).andReturn( null );
        replay( propertyResolver );
        MavenConfiguration config = new MavenConfigurationImpl( propertyResolver, PID );
        assertEquals( "Local repository", null, config.getLocalRepository() );
        verify( propertyResolver );
    }

    @Test
    public void getLocalRepositoryFromSettings()
        throws MalformedURLException
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.localRepository" ) ).andReturn( null );
        MavenSettings settings = createMock( MavenSettings.class );
        expect( settings.getLocalRepository() ).andReturn( "file:somewhere/localrepository/" );
        replay( propertyResolver, settings );
        MavenConfigurationImpl config = new MavenConfigurationImpl( propertyResolver, PID );
        config.setSettings( settings );
        assertEquals( "Local repository",
                      new URL( "file:somewhere/localrepository/" ),
                      config.getLocalRepository().getURL()
        );
        verify( propertyResolver, settings );
    }

    @Test
    public void useFallbackRepositories()
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.useFallbackRepositories" ) ).andReturn( "true" );
        replay( propertyResolver );
        MavenConfiguration config = new MavenConfigurationImpl( propertyResolver, PID );
        assertEquals( "Use Fallback Repositories", true, config.useFallbackRepositories() );
        verify( propertyResolver );
    }

    @Test
    public void defaultUseFallbackRepositories()
    {
        PropertyResolver propertyResolver = createMock( PropertyResolver.class );
        expect( propertyResolver.get( "test.pid.useFallbackRepositories" ) ).andReturn( null );
        replay( propertyResolver );
        MavenConfiguration config = new MavenConfigurationImpl( propertyResolver, PID );
        assertEquals("Use Fallback Repositories", true, config.useFallbackRepositories());
        verify( propertyResolver );
    }

}
