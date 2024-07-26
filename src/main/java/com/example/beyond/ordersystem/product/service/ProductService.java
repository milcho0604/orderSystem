package com.example.beyond.ordersystem.product.service;

import com.example.beyond.ordersystem.product.domain.Product;
import com.example.beyond.ordersystem.product.dto.ProductResDto;
import com.example.beyond.ordersystem.product.dto.ProductSaveDto;
import com.example.beyond.ordersystem.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product productCreate(ProductSaveDto dto) {
        MultipartFile image = dto.getProductImage();
        Product product = null;
        try {
            product = productRepository.save(dto.toEntity());
            byte[] bytes = image.getBytes();
            Path path = Paths.get("/Users/milcho/etc/tmp/",
                    product.getId() + "_" + image.getOriginalFilename());
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            product.updateImagePath(path.toString());
            // 위는 dirtyChecking 과정을 거쳐 변경을 감지한다. -> 다시 save 할 필요가 없음. !!
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장에 실패했습니다.");
        }
        return product;
    }

    @Transactional
    public Page<ProductResDto> productList(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(a -> a.fromEntity());
    }
}
