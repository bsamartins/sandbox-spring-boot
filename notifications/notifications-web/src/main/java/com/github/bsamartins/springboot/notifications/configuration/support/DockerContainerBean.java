package com.github.bsamartins.springboot.notifications.configuration.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;

public class DockerContainerBean implements DisposableBean {

    private static Logger LOGGER = LoggerFactory.getLogger(DockerContainerBean.class);

    private GenericContainer container;

    public DockerContainerBean(String image, int hostPort, int containerPort) {
        container = new FixedHostPortGenericContainer(image)
                .withFixedExposedPort(hostPort, containerPort);
        LOGGER.info("Starting container {} with port mapping {} -> {}", image, hostPort, containerPort);
        container.start();
    }

    @Override
    public void destroy() {
        try {
            this.container.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
