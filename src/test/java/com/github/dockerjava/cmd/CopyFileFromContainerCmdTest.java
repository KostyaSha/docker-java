package com.github.dockerjava.cmd;

import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

import static com.github.dockerjava.core.RemoteApiVersion.VERSION_1_24;
import static com.github.dockerjava.junit.DockerMatchers.isGreaterOrEqual;
import static com.github.dockerjava.junit.DockerAssume.assumeNotSwarm;
import static com.github.dockerjava.junit.TestUtils.asString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;

public class CopyFileFromContainerCmdTest extends CmdTest {
    public static final Logger LOG = LoggerFactory.getLogger(CopyFileFromContainerCmdTest.class);

    @Test
    public void copyFromContainer() throws Exception {
        assumeThat("Doesn't work since 1.24", dockerRule, isGreaterOrEqual(VERSION_1_24));

        assumeNotSwarm("", dockerRule);

        // TODO extract this into a shared method
        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox")
                .withName("docker-java-itest-copyFromContainer").withCmd("touch", "/copyFromContainer").exec();

        LOG.info("Created container: {}", container);
        assertThat(container.getId(), not(isEmptyOrNullString()));

        dockerRule.getClient().startContainerCmd(container.getId()).exec();

        InputStream response = dockerRule.getClient().copyFileFromContainerCmd(container.getId(), "/copyFromContainer").exec();
        Boolean bytesAvailable = response.available() > 0;
        assertTrue("The file was not copied from the container.", bytesAvailable );

        // read the stream fully. Otherwise, the underlying stream will not be closed.
        String responseAsString = asString(response);
        assertNotNull(responseAsString);
        assertTrue(responseAsString.length() > 0);
    }

    @Test(expected = NotFoundException.class)
    public void copyFromNonExistingContainer() throws Exception {

        dockerRule.getClient().copyFileFromContainerCmd("non-existing", "/test").exec();
    }
}
