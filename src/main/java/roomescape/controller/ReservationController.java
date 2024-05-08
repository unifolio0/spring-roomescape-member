package roomescape.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.request.UserReservationRequest;
import roomescape.controller.response.ReservationResponse;
import roomescape.service.ReservationService;
import roomescape.ui.MemberIdConverter;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findAll() {
        List<ReservationResponse> reservationResponses = reservationService.findAll();

        return ResponseEntity.ok(reservationResponses);
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> save(@MemberIdConverter Long memberId, @RequestBody UserReservationRequest userReservationRequest) {
        ReservationResponse reservationResponse = reservationService.saveByUser(memberId, userReservationRequest);

        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        reservationService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
