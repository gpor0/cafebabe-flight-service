package com.github.gpor0.resources;

import com.github.gpor0.business.FlightService;
import com.github.gpor0.business.model.Reservation;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import org.eclipse.microprofile.lra.annotation.Compensate;
import org.eclipse.microprofile.lra.annotation.Complete;
import org.eclipse.microprofile.lra.annotation.ParticipantStatus;
import org.eclipse.microprofile.lra.annotation.ws.rs.LRA;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.gpor0.business.FlightService.MAX_SEATS;
import static org.eclipse.microprofile.lra.annotation.ws.rs.LRA.LRA_HTTP_CONTEXT_HEADER;

@Path("/flights")
@RequestScoped
public class FlightResource {

    private static final Logger LOG = LogManager.getLogger("FlightService");

    @Inject
    private FlightService flightService;

    @GET
    @Path("/{flightCode}")
    @LRA(value = LRA.Type.MANDATORY, end = false)
    public Response reserve(@HeaderParam(LRA_HTTP_CONTEXT_HEADER) URI lraId, @PathParam("flightCode") String flightCode,
                            @QueryParam("customer") String customer) {
        LOG.info("Received reservation for flight {} and customer {}", flightCode, customer);
        try {
            flightCode = flightService.reserve(lraId, customer, flightCode);
            LOG.info("Flight {} is available", flightCode);
            return Response.ok(flightCode).build();
        } catch (Exception e) {
            LOG.error("Flight {} is NOT available", flightCode);
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
    }

    @GET
    @Produces("application/json")
    @Path("/")
    public Response getFlights() {
        Map<String, Map<URI, String>> reservations = flightService.getReservations();

        List<Reservation> result = reservations.entrySet().stream().map(e -> new Reservation(e.getKey(),
                e.getValue().entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList()),
                MAX_SEATS, MAX_SEATS - e.getValue().size())
        ).collect(Collectors.toList());

        return Response.ok(result).build();
    }

    @Complete
    @Path("/complete")
    @PUT
    public Response completeWork(@HeaderParam(LRA_HTTP_CONTEXT_HEADER) URI lraId) {
        AbstractMap.SimpleEntry<String, String> reservation = flightService.getReservation(lraId);

        if (reservation != null) {
            LOG.info("Reservation for flight {} and customer {} confirmed", reservation.getKey(), reservation.getValue());
        }

        return Response.ok(ParticipantStatus.Completed.name()).build();
    }

    @PUT
    @Path("/compensate")
    @Compensate
    public Response compensate(@HeaderParam(LRA.LRA_HTTP_CONTEXT_HEADER) URI lraId) {
        AbstractMap.SimpleEntry<String, String> reservation = flightService.getReservation(lraId);

        if (reservation != null) {
            LOG.info("StartingCompensation procedure for flight {} and customer {}", reservation.getKey(), reservation.getValue());

            flightService.rollback(lraId);

            LOG.info("Compensation procedure for flight {} and customer {} completed", reservation.getKey(), reservation.getValue());

        }
        return Response.ok(ParticipantStatus.Compensated.name()).build();
    }

}
