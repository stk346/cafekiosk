package sample.cafekiosk.spring.domain.product;

import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
public enum ProductType {

    HANDMADE("제조 음료"),
    BOTTLE("병 음료"),
    BAKERY("베이커리");

    private final String text;

    // 재고를 차감하는 타입인지 체크해주는 메서드
    public static boolean containsStockType(ProductType type) {
        return List.of(BOTTLE, BAKERY).contains(type);
    }
}
