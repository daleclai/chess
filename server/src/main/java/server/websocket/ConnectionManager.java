package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();
    public void add(Integer gameID, Session session) {
        connections.computeIfAbsent(gameID, id -> ConcurrentHashMap.newKeySet()).add(session);
    }
    public void remove(Session session) {
        for (Set<Session> sessions : connections.values()) {
            sessions.remove(session);
        }
    }

    public void broadcast(Integer gameID, Session exclude, String message) throws IOException {
        Set<Session> sessions = connections.get(gameID);
        if (sessions == null) {
            return;
        }
            for (Session session : sessions) {
                if (session.isOpen() && !session.equals(exclude)) {
                    session.getRemote().sendString(message);
                }
            }
        }

        public void broadcastToAll(Integer gameID, String message) throws IOException {
            broadcast(gameID,null, message);
        }
        public void sendToRoot(Session session, String message) throws IOException {
            if (session.isOpen()) {
                session.getRemote().sendString(message);
            }
        }
    }
