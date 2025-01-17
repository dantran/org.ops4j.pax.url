package org.ops4j.pax.url.mvn.internal;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.UUID;

import junit.framework.Assert;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ops4j.pax.url.maven.commons.MavenConfigurationImpl;
import org.ops4j.pax.url.maven.commons.MavenConstants;
import org.ops4j.pax.url.maven.commons.MavenSettings;
import org.ops4j.pax.url.maven.commons.MavenSettingsImpl;
import org.ops4j.util.property.PropertiesPropertyResolver;

public class MirrorTest {
    private static final String TEST_PID = "test.pid";
    private Server              server;

    @Before
    public void startHttp() throws Exception {
        server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(8181);
        server.addConnector(connector);

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(false);
        resource_handler.setWelcomeFiles(new String[] {});

        resource_handler.setResourceBase("target/test-classes/repo2");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resource_handler,
                new DefaultHandler() });
        server.setHandler(handlers);

        server.start();
    }

    @After
    public void stopHttp() throws Exception {
        server.stop();
    }

    @Test
    public void mirror1() throws IOException, InterruptedException {

        Properties properties = new Properties();
        String repoPath = "target/localrepo_" + UUID.randomUUID();
        properties.setProperty(TEST_PID + MavenConstants.PROPERTY_LOCAL_REPOSITORY, repoPath);
        properties.setProperty(TEST_PID + MavenConstants.PROPERTY_REPOSITORIES, "http://google.com/repo@id=fake");
        PropertiesPropertyResolver propertyResolver = new PropertiesPropertyResolver(properties );
        MavenConfigurationImpl config = new MavenConfigurationImpl(propertyResolver, TEST_PID);

        MavenSettings settings = new MavenSettingsImpl(getClass().getResource( "/settings-mirror1.xml") );
        config.setSettings(settings);
        File localRepo = config.getLocalRepository().getFile();
        // you must exist.
        localRepo.mkdirs();
        Assert.assertEquals(new File(repoPath).getAbsoluteFile(), localRepo);

        Connection c = new Connection(new URL("file:ant/ant/1.5.1"), config);
        c.getInputStream();
        Assert.assertEquals("the artifact must be downloaded", true, new File(localRepo, "ant/ant/1.5.1/ant-1.5.1.jar").exists());
    }

    @Test()
    public void no_mirror_notfound() throws IOException
    {

        Properties properties = new Properties();
        properties.setProperty(TEST_PID + MavenConstants.PROPERTY_LOCAL_REPOSITORY, "target/localrepo2");
        properties.setProperty(TEST_PID + MavenConstants.PROPERTY_REPOSITORIES, "http://qfdqfqfqf.fra/repo@id=fake");
        PropertiesPropertyResolver propertyResolver = new PropertiesPropertyResolver(properties );
        MavenConfigurationImpl config = new MavenConfigurationImpl(propertyResolver, TEST_PID);

        File localRepo = config.getLocalRepository().getFile();
        // you must exist.
        localRepo.mkdirs();
        Assert.assertEquals(new File("target/localrepo2").getAbsoluteFile(), localRepo);

        Connection c = new Connection(new URL("file:test/test/1.5.1"), config);
        try
        {
            c.getInputStream().read();
        } catch (Exception e)
        {
            return;
        }

        Assert.fail("artifact found");
    }

    @Test
    public void mirror2() throws IOException, InterruptedException
    {
        Properties properties = new Properties();
        String repoPath = "target/localrepo_" + UUID.randomUUID();
        properties.setProperty(MirrorTest.TEST_PID
                + MavenConstants.PROPERTY_LOCAL_REPOSITORY, repoPath);
        properties.setProperty(MirrorTest.TEST_PID
                + MavenConstants.PROPERTY_REPOSITORIES,
                "http://qfdqfqfqf.fra/repo");
        PropertiesPropertyResolver propertyResolver = new PropertiesPropertyResolver(
                properties);
        MavenConfigurationImpl config = new MavenConfigurationImpl(
                propertyResolver, MirrorTest.TEST_PID);

        MavenSettings settings = new MavenSettingsImpl(new File(
                "target/test-classes/settings-mirror2.xml").toURL());
        config.setSettings(settings);
        File localRepo = config.getLocalRepository().getFile();
        // you must exist.
        localRepo.mkdirs();
        Assert.assertEquals(new File(repoPath).getAbsoluteFile(), localRepo);

        Connection c = new Connection(new URL("file:ant/ant/1.5.1"), config);
        c.getInputStream();
        Assert.assertEquals("the artifact must be downloaded", true, new File(
                localRepo, "ant/ant/1.5.1/ant-1.5.1.jar").exists());
    }
}
