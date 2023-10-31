package dev.aleixmorgadas.orders;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping(OrderController.URI)
public class OrderController {
    public static final String URI = "/orders";

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Void> ingestOrders(
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(null);
    }
}
