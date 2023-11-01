package dev.aleixmorgadas.merchants;

import lombok.AllArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@RestController
@RequestMapping(MerchantController.URI)
@AllArgsConstructor
public class MerchantController {
    public static final String URI = "/merchants";
    private final MerchantRepository merchantRepository;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Void> ingestMerchants(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        var reader = new InputStreamReader(file.getInputStream());
        var records = CSVFormat.newFormat(';')
                .builder()
                .setHeader(Header.class)
                .setSkipHeaderRecord(true)
                .build()
                .parse(reader);
        var merchants = records.getRecords()
                .stream()
                .map(record -> Merchant.builder()
                        .reference(record.get(Header.Reference))
                        .email(record.get(Header.Email))
                        .liveOn(record.get(Header.LiveOn))
                        .disbursementFrequency(record.get(Header.DisbursementFrequency))
                        .minimumMonthlyFee(Double.parseDouble(record.get(Header.MinimumMonthlyFee)))
                        .build())
                .collect(Collectors.toList());
        merchantRepository.saveAll(merchants);
        return ResponseEntity.ok(null);
    }

    enum Header {
        Reference,
        Email,
        LiveOn,
        DisbursementFrequency,
        MinimumMonthlyFee
    }
}
