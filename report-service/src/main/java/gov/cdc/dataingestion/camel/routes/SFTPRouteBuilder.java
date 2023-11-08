package gov.cdc.dataingestion.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.zipfile.ZipSplitter;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@ConditionalOnProperty(name = "di.camel.sftp-endpoint.enabled", havingValue = "true")
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

    @Override
    public void configure() throws Exception {
        //shutdown faster in case of in-flight messages stack up
        getContext().getShutdownStrategy().setTimeout(10);

        if (sftpDirectory == null || !sftpDirectory.startsWith("/")) {
            sftpDirectory = "/" + sftpDirectory;
        }

        URI fromSftpUrl = new URIBuilder()
                .setScheme("sftp")
                .setHost(sftpHost)
                .setPort(22)
                .setPath(sftpDirectory)
                .addParameter("username", sftpUserName)
                .addParameter("password", sftpPassword)
                .addParameter("autoCreate", "true")
                .addParameter("passiveMode", "true")
                .addParameter("initialDelay", "2000")
                .addParameter("delay", "1000")
                .addParameter("noop", "true")
                .addParameter("delete", "true")
                .addParameter("localWorkDirectory", "files/download") //check
                .addParameter("recursive","true")
                .addParameter("maximumReconnectAttempts","2")
                .addParameter("reconnectDelay","5000")
                .addParameter("excludeExt","bak,da")
                .addParameter("includeExt","txt,TXT,zip,ZIP")
                .build();

        String sftp_server=fromSftpUrl.toString();
        log.debug("sftp_server URL:" + sftp_server);
        //# for the server we want to delay 5 seconds between polling the server
        //# and keep the downloaded file as-is

        log.debug("Calling sftpRouteId");
        from(sftp_server).routeId("sftpRouteId")
                .log("The file from sftpRouteId: ${file:name}")
                .choice()
                    .when(simple("${file:name} endsWith '.zip'"))
                        .log(" when .zip condition...The file ${file:name}")
                        .to("file:files/sftpdownload")
                    .otherwise()
                        .log(" Otherwise condition for txt files ...The file ${file:name} content from sftp server is ${body}")
                        .to("bean:hL7FileProcessComponent")
                .end();

        log.debug("Calling unzipfileRouteId");
        from("file:files/sftpdownload")
                .routeId("unzipfileRouteId")
                .split(new ZipSplitter()).streaming()
                .to("file:files/sftpdownloadUnzip")
                .end();

        log.debug("Calling sftpdownloadUnzip");
        from("file:files/sftpdownloadUnzip?includeExt=txt")
                .routeId("ReadFromUnzipDirRouteId")
                .log(" Read from unzipped files folder ...The file ${file:name}")
                .to("bean:hL7FileProcessComponent")
                .end();
    }
    @Bean
    public HL7FileProcessComponent hL7FileProcessComponent() {
        return new HL7FileProcessComponent();
    }
}

