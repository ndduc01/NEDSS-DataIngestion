package gov.cdc.dataingestion.camel.routes;

import org.apache.camel.FailedToStartRouteException;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
                .build();
        //ignoreFileNotFoundOrPermissionError
        //excludeExt=bak,da
        String sftp_server=fromSftpUrl.toString();
        log.debug("sftp_server URL:" + sftp_server);
        System.out.println("sftp_server URL:" + sftp_server);
        //# for the server we want to delay 5 seconds between polling the server
        //# and keep the downloaded file as-is
//        onException(Exception.class)
//                .log("Exception sftpRouteBuilder: ${exception}")
//                .handled(true);
//        onException(RuntimeCamelException.class)
//                .log("RuntimeCamelException sftpRouteBuilder: ${exception}")
//                .handled(true);
        System.out.println("fromSFtpUrl:"+sftp_server);
            from(sftp_server).routeId("sftpRouteId")
                    .log("The file ${file:name} content from sftp server is ${body}")
                    ///validation
                    .to("bean:hL7FileProcessComponent")
                    .end();

            //////

        ////////////
    }
    @Bean
    public HL7FileProcessComponent hL7FileProcessComponent() {
        return new HL7FileProcessComponent();
    }
}

