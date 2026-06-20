package com.order_service.order_servie.Controller;

import com.order_service.order_servie.DTO.ReturnRequest1;
import com.order_service.order_servie.Entity.ReturnRequest;
import com.order_service.order_servie.Service.ReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/return")
public class ReturnController {

    @Autowired
    private ReturnService returnService;

    @PostMapping("/create/returnRequest")
    public ResponseEntity<?> createReturnRequest(@RequestBody ReturnRequest1 returnRequest1)
    {
        returnService.createReturnRequest(returnRequest1);
        return ResponseEntity.ok("Done");
    }
}
