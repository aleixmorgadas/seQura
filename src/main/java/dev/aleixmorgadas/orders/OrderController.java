package dev.aleixmorgadas.orders;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(OrderController.URI)
@AllArgsConstructor
public class OrderController {
    public static final String URI = "/orders";
    private final OrderService orderService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Void> ingestOrders(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        var reader = new InputStreamReader(file.getInputStream());
        var records = CSVFormat.newFormat(';')
                .builder()
                .setHeader(Header.class)
                .setSkipHeaderRecord(true)
                .build()
                .parse(reader);
        var orders = records.getRecords()
                .stream()
                .map(record -> Order.builder()
                        .merchantReference(record.get(Header.MerchantReference))
                        .amount(record.get(Header.Amount))
                        .createdAt(LocalDate.parse(record.get(Header.CreatedAt)))
                        .build())
                .collect(Collectors.toList());
        orderService.saveAll(orders);
        return ResponseEntity.ok(null);
    }

    enum Header {
        MerchantReference,
        Amount,
        CreatedAt
    }
}
