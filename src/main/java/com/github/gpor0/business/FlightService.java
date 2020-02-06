package com.github.gpor0.business;

import javax.enterprise.context.ApplicationScoped;
import java.net.URI;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class FlightService {

    private static final Map<String, Map<URI, String>> RESERVATIONS = new HashMap<>();

    public static final Integer MAX_SEATS = 5;

    public String reserve(URI lraId, String customer, String flightCode) {

        synchronized (RESERVATIONS) {
            flightCode = flightCode.toUpperCase();

            if (!RESERVATIONS.containsKey(flightCode)) {
                RESERVATIONS.put(flightCode, new HashMap<>());
            }

            Map<URI, String> flightTravelers = RESERVATIONS.get(flightCode);

            if (flightTravelers.size() == MAX_SEATS) {
                throw new RuntimeException("No more seats available on flight " + flightCode);
            }

            flightTravelers.put(lraId, customer);
            return flightCode;
        }
    }

    public Map<String, Map<URI, String>> getReservations() {
        return RESERVATIONS;
    }

    public AbstractMap.SimpleEntry<String, String> getReservation(URI lraId) {

        for (Map.Entry<String, Map<URI, String>> e : RESERVATIONS.entrySet()) {
            Map<URI, String> flightReservations = e.getValue();
            if (flightReservations.containsKey(lraId)) {
                String traveler = flightReservations.get(lraId);
                return new AbstractMap.SimpleEntry<>(e.getKey(), traveler);
            }
        }

        return null;

    }

    public void rollback(URI lraId) {
        for (Map.Entry<String, Map<URI, String>> e : RESERVATIONS.entrySet()) {
            Map<URI, String> flightReservations = e.getValue();
            if (flightReservations.containsKey(lraId)) {
                flightReservations.remove(lraId);
                RESERVATIONS.put(e.getKey(), flightReservations);
                break;
            }
        }
    }

}
