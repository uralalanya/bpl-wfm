package com.fererlab.wfm.rest;


import com.fererlab.common.property.PropertyProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/websocket-pubsub")
public class PublishSubscribeResource {

    // ************************************************************************
    // OPEN 3 INCOGNITO WINDOWS IN CHROME TO URL
    // http://localhost:8080/mvvm/websocket.html
    // ************************************************************************

    private static final Logger logger = LoggerFactory.getLogger(PublishSubscribeResource.class);
    private static final Set<Session> subscribers = Collections.synchronizedSet(new HashSet<>());

    @OnMessage
    public void message(String message, Session publisherSession) {
        for (Session subscriberSession : subscribers) {
            if (!subscriberSession.getId().equals(publisherSession.getId())) {
                subscriberSession.getAsyncRemote().sendText(message);
            }
        }
    }

    @OnOpen
    public void open(final Session session) {
        logger.info("client registered subscribed:" + session.getId());
        if (!subscribers.contains(session)) {
            subscribers.add(session);
        }
    }

    @OnClose
    public void close(Session session, CloseReason c) {
        logger.info("client unregistered subscribed:" + session.getId());
        if (subscribers.contains(session)) {
            subscribers.remove(session);
        }
    }

    @OnError
    public void onError(Throwable t) {
        final String errorMessage = String.format("got error at web socket, error: %1s", t);
        logger.info(errorMessage, t);
    }

}
