package dev.aleixmorgadas.disbursements;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(DisbursementController.URI)
public class DisbursementController {
    public static final String URI = "/disbursements";
    private final DisbursementService disbursementService;

    @PostMapping("/{date}")
    public ResponseEntity<List<DisbursementResponse>> performDisbursements(
            @PathVariable String date
    ) {
        var disbursements = disbursementService.performDisbursementsOn(date);
        return ResponseEntity.ok(disbursements.stream().map(DisbursementResponse::from).toList());
    }

    public record DisbursementResponse(
            String reference,
            String merchant,
            double amount,
            double fees,
            int ordersAmount,
            String date
    ) {
        static DisbursementResponse from(Disbursement disbursement) {
            return new DisbursementResponse(
                    disbursement.getReference(),
                    disbursement.getMerchant(),
                    disbursement.getAmount(),
                    disbursement.getFees(),
                    disbursement.getOrders().size(),
                    disbursement.getDate().format(DisbursementService.DATE_FORMATTER)
            );
        }
    }
}
