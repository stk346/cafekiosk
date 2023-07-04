package sample.cafekiosk.spring.api.service.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;
import sample.cafekiosk.spring.domain.product.dto.request.ProductCreateRequest;

import java.util.List;
import java.util.stream.Collectors;

import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductType.*;

/**
 * readOnly = true: 읽기 전용
 * CRUD 에서 CUD 동작 X / only Read
 * JPA : CUD 스냅샷 저장, 변경감지 X (성능 향상)
 *
 * CQRS - Command / Read -> Comment랑 Read를 분리하자.
 * 서비스 단에서도 분리하면 좋음!
 */
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 동시성 이슈가 있을 수 있음. 이를 해결하기 위한 방법은 여러가지
     * 필드에 유니크 인덱스를 걸고 시도가 실패하면 3회 재시도
     * UUID 활용
     */
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        String nextProductNumber = createNextProductNumber();

        Product product = request.toEntity(nextProductNumber);
        Product savedProduct = productRepository.save(product);

        return ProductResponse.of(savedProduct);
    }

    private String createNextProductNumber() {
        String latestProductNumber = productRepository.findLatestProductNumber();
        if (latestProductNumber == null) {
            return "001";
        }

        int latestProductNumberInt = Integer.parseInt(latestProductNumber);
        int nextProductNumber = latestProductNumberInt + 1;

        // 9 -> 009 10 -> 010
        return String.format("%03d", nextProductNumber);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getSellingProducts() {
        List<Product> products = productRepository.findAllBySellingStatusIn(forDisplay());

        return products.stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }
}
