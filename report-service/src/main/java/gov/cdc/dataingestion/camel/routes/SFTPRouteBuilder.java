package gov.cdc.dataingestion.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SFTPRouteBuilder extends RouteBuilder {
    private static Logger log = LoggerFactory.getLogger(SFTPRouteBuilder.class);
    @Value("${sftp.host}")
    private String sftpHost;
    @Value("${sftp.port}")
    private String sftpPort;
    @Value("${sftp.username}")
    private String sftpUserName;
    @Value("${sftp.password}")
    private String sftpPassword;
    @Value("${sftp.directory}")
    private String sftpDirectory;

    @Autowired
    private HL7FileProcessor hl7FileProcessor;

    @Override
    public void configure() throws Exception {
        //shutdown faster in case of in-flight messages stack up
        getContext().getShutdownStrategy().setTimeout(10);

        if (sftpDirectory == null || !sftpDirectory.startsWith("/")) {
            sftpDirectory = "/" + sftpDirectory;
        }
        String ftp_server = "sftp://" + sftpHost + ":" + sftpPort + sftpDirectory + "?autoCreate=true&username=" + sftpUserName +
                "&password=" + sftpPassword + "&passiveMode=true&binary=true" +
                "&localWorkDirectory=files/download" +
                "&delay=5000&noop=true&delete=true";

        log.debug("sftp_server URL:" + ftp_server);
        System.out.println("sftp_server URL:" + ftp_server);
        //# for the server we want to delay 5 seconds between polling the server
        //# and keep the downloaded file as-is
        try {
            from(ftp_server).routeId("sftpRouteId")
                    .log("The file ${file:name} content from sftp server is ${body}")
                    .setHeader("msgType", constant("HL7"))
                    .setHeader("validationActive", constant("false"))
                    .process(hl7FileProcessor)
                    .end();
//            from(ftp_server).routeId("sftpRouteId")
//                    .log("The file ${file:name} content from sftp server is ${body}")
//                    .end();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}

