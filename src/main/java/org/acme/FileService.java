package org.acme;

import java.io.BufferedReader;
import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;

import io.quarkus.vertx.ConsumeEvent;

@ApplicationScoped
public class FileService {

    private static final Logger LOG = Logger.getLogger(FileService.class.getName());

    @ConsumeEvent(blocking = true, value = "file-service")
    public void processFile(BufferedReader br) throws InterruptedException {

        LOG.info("processFile() begin");

        try (br) {
            String currentLine = null;
            while ((currentLine = br.readLine()) != null) {
                LOG.info("currentLine " + currentLine);
            }
        } catch (IOException e) {
            LOG.error("Error", e);
        }

        LOG.info("processFile() end");

    }

}
