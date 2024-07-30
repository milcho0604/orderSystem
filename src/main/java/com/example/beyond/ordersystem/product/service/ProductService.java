package com.example.beyond.ordersystem.product.service;

import com.example.beyond.ordersystem.common.service.StockInventoryService;
import com.example.beyond.ordersystem.product.domain.Product;
import com.example.beyond.ordersystem.product.dto.ProductResDto;
import com.example.beyond.ordersystem.product.dto.ProductSaveDto;
import com.example.beyond.ordersystem.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;
    private final S3Client s3Client;
    private final StockInventoryService stockInventoryService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Autowired
    public ProductService(ProductRepository productRepository, S3Client s3Client, StockInventoryService stockInventoryService) {
        this.productRepository = productRepository;
        this.s3Client = s3Client;
        this.stockInventoryService = stockInventoryService;
    }


    // 방법1. local pc에 임시 저장 - file.write
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

            if (dto.getName().contains("sale")){
                stockInventoryService.increaseStock(product.getId(), dto.getStock_quantity());
            }
            // 위는 dirtyChecking 과정을 거쳐 변경을 감지한다. -> 다시 save 할 필요가 없음. !!
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장에 실패했습니다.");
        }
        return product;
    }


    // 방법2 : aws 에 pc에 저장된 파일을 업로드
    @Transactional
    public Product productAwsCreate(ProductSaveDto dto) {
        MultipartFile image = dto.getProductImage();
        Product product = null;
        try {
            product = productRepository.save(dto.toEntity());
            byte[] bytes = image.getBytes();
            String filName = product.getId() + "_" + image.getOriginalFilename();
            Path path = Paths.get("/Users/milcho/etc/tmp/", filName);
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(filName)
                    .build();
            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest,
                    RequestBody.fromFile(path)
                    );
            String s3Path = s3Client.utilities().getUrl(a -> a.bucket(bucket).key(filName)).toExternalForm();
            // 위는 dirtyChecking 과정을 거쳐 변경을 감지한다. -> 다시 save 할 필요가 없음. !!
            product.updateImagePath(s3Path);
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
