package com.jpmorgan.quorum.tessera.sync;

import com.quorum.tessera.encryption.PublicKey;
import com.quorum.tessera.node.model.Party;
import com.quorum.tessera.node.model.PartyInfo;
import com.quorum.tessera.node.model.Recipient;
import java.net.URI;
import java.util.Base64;
import java.util.Collections;
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import static org.assertj.core.api.Assertions.assertThat;
import org.glassfish.tyrus.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartyInfoSyncIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(PartyInfoSyncIT.class);
    
    private Server server;

    @Before
    public void onSetUp() throws Exception {
        server = new Server("localhost", 8025, "/", null, PartyInfoEndpoint.class);
        server.start();
    }

    @After
    public void onTearDown() {
        server.stop();
    }

    @Test
    public void doStuff() throws Exception {

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        
        PartyInfoClientEndpoint client = new PartyInfoClientEndpoint();
        
        Session clientSession = container.connectToServer(client, URI.create("ws://localhost:8025/sync"));
        LOGGER.info("Client sesssion : {}",clientSession.getId());
        PublicKey publicKey = PublicKey.from(Base64.getDecoder().decode("ROAZBWtSacxXQrOe3FGAqJDyJjFePR5ce4TSIzmJ0Bc="));
        
        PartyInfo partyInfo = new PartyInfo("http://bogus.com:9999",
                Collections.singleton(new Recipient(publicKey,"http://bogus.com:9998")),
                Collections.singleton(new Party("http://bogus.com:9997")));
        
        clientSession.getBasicRemote().sendObject(partyInfo);

        
        
        assertThat(server).isNotNull();

    }

}
