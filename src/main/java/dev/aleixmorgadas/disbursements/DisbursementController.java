package dev.aleixmorgadas.disbursements;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping(DisbursementController.URI)
public class DisbursementController {
    public static final String URI = "/disbursements";
    private final DisbursementService disbursementService;
    private final DisbursementRepository disbursementRepository;

    @PostMapping("/{date}")
    public ResponseEntity<List<DisbursementResponse>> performDisbursements(
            @PathVariable String date
    ) {
        var disbursements = disbursementService.performDisbursementsOn(date);
        return ResponseEntity.ok(disbursements.stream().map(DisbursementResponse::from).toList());
    }

    @GetMapping("/by-year")
    public ResponseEntity<List<DisbursementYearReport>> byYear() {
        var df = new DecimalFormat("#");
        df.setMaximumFractionDigits(2);
        var byYearDisbursementReport = disbursementRepository.byYearDisbursementReport()
                .stream().map(disbursementReport -> new DisbursementYearReport(
                        disbursementReport.getYear().toString(),
                        disbursementReport.getDisbursements(),
                        df.format(disbursementReport.getAmount()),
                        df.format(disbursementReport.getFees())
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(byYearDisbursementReport);
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
                    disbursement.getReference().reference,
                    disbursement.getMerchant(),
                    disbursement.getAmount(),
                    disbursement.getFees(),
                    disbursement.getOrders().size(),
                    disbursement.getDate().format(DisbursementService.DATE_FORMATTER)
            );
        }
    }

    public record DisbursementYearReport(
            String year,
            long disbursements,
            String amount,
            String fees
    ) {}
}
