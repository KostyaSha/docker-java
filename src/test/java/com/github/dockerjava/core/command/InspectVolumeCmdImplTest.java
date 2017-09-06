package com.github.dockerjava.core.command;

import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.core.AbstractJerseyDockerClientTest;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@Test(groups = "integration")
public class InspectVolumeCmdImplTest extends AbstractJerseyDockerClientTest {

    @BeforeTest
    public void beforeTest() throws Exception {
        super.beforeTest();
    }

    @AfterTest
    public void afterTest() {
        super.afterTest();
    }

    @BeforeMethod
    public void beforeMethod(Method method) {
        super.beforeMethod(method);
    }

    @AfterMethod
    public void afterMethod(ITestResult result) {
        super.afterMethod(result);
    }

    @Test
    public void inspectVolume() throws DockerException {

        String volumeName = "volume1";

        dockerClient.createVolumeCmd().withName(volumeName).withDriver("local").exec();

        InspectVolumeResponse inspectVolumeResponse = dockerClient.inspectVolumeCmd(volumeName).exec();

        assertThat(inspectVolumeResponse.getName(), equalTo("volume1"));
        assertThat(inspectVolumeResponse.getDriver(), equalTo("local"));
        assertThat(inspectVolumeResponse.getMountpoint(), containsString("/volume1/"));
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void inspectNonExistentVolume() throws DockerException {

        String volumeName = "non-existing";

        dockerClient.inspectVolumeCmd(volumeName).exec();
    }
}
