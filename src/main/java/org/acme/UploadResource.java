package org.acme;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.MultipartForm;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import io.vertx.core.eventbus.EventBus;

@RequestScoped
@Path("upload")
public class UploadResource {

    private static final Logger LOG = Logger.getLogger(UploadResource.class.getName());

    @Inject
    EventBus bus;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @APIResponse(responseCode = "202")
    public Response upload(
            @MultipartForm MultipartBody body) throws IOException {

        LOG.info("upload() quantity of files + " + body.files.size());

        for (FileUpload file : body.files) {

            LOG.info("filePath " + file.filePath());

            BufferedReader br = Files.newBufferedReader(file.filePath());

            bus.send("file-service", br);
        }

        LOG.info("upload() before response Accepted");

        return Response
                .accepted()
                .build();
    }

    // Class that will define the OpenAPI schema for the binary type input (upload)
    @Schema(type = SchemaType.STRING, format = "binary")
    public interface UploadItemSchema {
    }

    // Class that will be used to define the request body, and with that
    // it will allow uploading of "N" files
    public class UploadFormSchema {
        public List<UploadItemSchema> files;
    }

    // We instruct OpenAPI to use the schema provided by the 'UploadFormSchema'
    // class implementation and thus define a valid OpenAPI schema for the Swagger
    // UI
    @Schema(implementation = UploadFormSchema.class)
    public static class MultipartBody {
        @RestForm("files")
        public List<FileUpload> files;
    }

}
