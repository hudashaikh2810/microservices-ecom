package com.example.product_service.Controller;

import com.example.product_service.DTO.ImageDto;
import com.example.product_service.DTO.ProductDto;
import com.example.product_service.DTO.ProductPrice;
import com.example.product_service.DTO.SKUDto;
import com.example.product_service.Service.SkuCreatedEvent;
import com.example.product_service.Service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/product/sku/")
public class SkuController {
    @Autowired
    private SkuService skuService;

    @Autowired
    private SkuCreatedEvent skuCreatedEvent;

    @PostMapping(value = "/admin/add/{id}")
    public ResponseEntity<?> addSku(@PathVariable Long id, @RequestPart("sku") SKUDto skuDto, @RequestPart("images") List<MultipartFile> file, @RequestPart("imageDto")List<ImageDto> imageDto) {
        SKUDto savedSkuDto = skuService.createSku(id,skuDto,file,imageDto);
       // skuCreatedEvent.SkuCreated(savedSkuDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSkuDto);
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<?> deleteSku(@PathVariable String id) {
        skuService.deleteSku(id);
        return ResponseEntity.ok("Product deleted successfully");
    }

    @PutMapping(value = "/admin/update/{id}")
    public ResponseEntity<?> updateSku(@RequestPart("sku") SKUDto productDto, @PathVariable String id,@RequestPart("images") List<MultipartFile> file,List<ImageDto> imageDto) {
        SKUDto updatedSkuDto = skuService.updateSku(id, productDto,file,imageDto);
        return ResponseEntity.ok(updatedSkuDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(skuService.getSku(id));
    }
    @GetMapping("/price/{id}")
    public ResponseEntity<?> getSkuPrice(@PathVariable String id)
    {
        return ResponseEntity.ok(skuService.getSkuPrice(id));
    }
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getSkuDetail(@PathVariable String id)
    {
        return ResponseEntity.ok(skuService.getSkuDetail(id));
    }

    @GetMapping("/price")
    public ResponseEntity<?> getSkusPrice(@RequestBody List<String> productPriceList)
    {
        return ResponseEntity.ok(skuService.getSkuPrice(productPriceList));
    }
}
