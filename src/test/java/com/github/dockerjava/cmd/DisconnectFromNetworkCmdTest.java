package com.github.dockerjava.cmd;

import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.CreateNetworkResponse;
import com.github.dockerjava.api.model.Network;
import org.junit.Test;

import static com.github.dockerjava.junit.DockerAssume.assumeNotSwarm;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class DisconnectFromNetworkCmdTest extends CmdTest {

    @Test
    public void disconnectFromNetwork() throws InterruptedException {
        assumeNotSwarm("no network in swarm", dockerRule);

        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox").withCmd("sleep", "9999").exec();
        dockerRule.getClient().startContainerCmd(container.getId()).exec();

        CreateNetworkResponse network = dockerRule.getClient().createNetworkCmd().withName("testNetwork").exec();

        dockerRule.getClient().connectToNetworkCmd().withNetworkId(network.getId()).withContainerId(container.getId()).exec();

        Network updatedNetwork = dockerRule.getClient().inspectNetworkCmd().withNetworkId(network.getId()).exec();

        assertTrue(updatedNetwork.getContainers().containsKey(container.getId()));

        dockerRule.getClient().disconnectFromNetworkCmd().withNetworkId(network.getId()).withContainerId(container.getId()).exec();

        updatedNetwork = dockerRule.getClient().inspectNetworkCmd().withNetworkId(network.getId()).exec();

        assertFalse(updatedNetwork.getContainers().containsKey(container.getId()));
    }

    @Test
    public void forceDisconnectFromNetwork() throws InterruptedException {
        assumeNotSwarm("no network in swarm", dockerRule);

        CreateNetworkResponse network = dockerRule.getClient().createNetworkCmd().withName("testNetwork").exec();

        CreateContainerResponse container = dockerRule.getClient().createContainerCmd("busybox")
                .withNetworkMode("testNetwork")
                .withCmd("sleep", "9999")
                .exec();

        dockerRule.getClient().startContainerCmd(container.getId()).exec();

        dockerRule.getClient().disconnectFromNetworkCmd()
                .withNetworkId(network.getId())
                .withContainerId(container.getId())
                .withForce(true)
                .exec();

        Network updatedNetwork = dockerRule.getClient().inspectNetworkCmd().withNetworkId(network.getId()).exec();
        assertFalse(updatedNetwork.getContainers().containsKey(container.getId()));
    }
}
