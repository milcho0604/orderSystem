package com.example.beyond.ordersystem.ordering.service;

import com.example.beyond.ordersystem.member.domain.Member;
import com.example.beyond.ordersystem.member.repository.MemberRepository;
import com.example.beyond.ordersystem.ordering.domain.OrderDetail;
import com.example.beyond.ordersystem.ordering.domain.Ordering;
import com.example.beyond.ordersystem.ordering.dto.OrderListResDto;
import com.example.beyond.ordersystem.ordering.dto.OrderSaveReqDto;
import com.example.beyond.ordersystem.ordering.repository.OrderDetailRepository;
import com.example.beyond.ordersystem.ordering.repository.OrderingRepository;
import com.example.beyond.ordersystem.product.domain.Product;
import com.example.beyond.ordersystem.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class OrderingService {

    private final OrderingRepository orderingRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    public OrderingService(OrderingRepository orderingRepository, MemberRepository memberRepository, ProductRepository productRepository, OrderDetailRepository orderDetailRepository) {
        this.orderingRepository = orderingRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    @Transactional
    public Ordering orderCreate(@ModelAttribute OrderSaveReqDto dto) {
//        //        방법1.쉬운방식
////        Ordering생성 : member_id, status
//        Member member = memberRepository.findById(dto.getMember_id()).orElseThrow(() -> new EntityNotFoundException("없음"));
//        Ordering ordering = orderingRepository.save(dto.toEntity(member));
//
////        OrderDetail생성 : order_id, product_id, quantity
//        for (OrderSaveReqDto.OrderDetailDto orderDto : dto.getOrderDetailDtoList()) {
//            Product product = productRepository.findById(orderDto.getProductId()).orElse(null);
//            int quantity = orderDto.getProductCount();
//            OrderDetail orderDetail = OrderDetail.builder()
//                    .product(product)
//                    .quantity(quantity)
//                    .ordering(ordering)
//                    .build();
//            orderDetailRepository.save(orderDetail);
//        }
//        return ordering;
//    }

        // 방법2 : JPA 최적화된 방식
        // Ordering 생성: member_id, status
        Member member = memberRepository.findById(dto.getMember_id())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        Ordering ordering = Ordering.builder()
                .member(member)
                .build();
        // OrderDetail생성 : order_id, product_id, quantity
        for (OrderSaveReqDto.OrderDetailDto orderDto : dto.getOrderDetailDtoList()) {
            Product product = productRepository.findById(orderDto.getProductId())
                    .orElseThrow(()-> new EntityNotFoundException("존재하지 않는 상품입니다."));
            int quantity = orderDto.getProductCount();
            if(quantity > product.getStock_quantity()){
                throw new IllegalArgumentException("재고가 부족합니다");
            }else {
                // 변경감지로 인해 별도의 save 불필요
                product.UpdatStockQuantity(quantity);
            }
            OrderDetail orderDetail = OrderDetail.builder()
                    .product(product)
                    .quantity(quantity)
                    .ordering(ordering)
                    // orderingRepository.save(ordering);을 하지 않아,
                    // ordering_id 는 아직 생성되지 않았지만, JPA가 자동으로 순서를 정렬하여 ordering_id 를 삽입한다.
                    .build();
            ordering.getOrderDetails().add(orderDetail);
        }
        Ordering savedOreder = orderingRepository.save(ordering);
        return savedOreder;
    }

    @Transactional
    public List<OrderListResDto> orderList (){
        List<Ordering> orderings = orderingRepository.findAll();
        List<OrderListResDto> orderListResDtos = new ArrayList<>();
        for(Ordering ordering : orderings){
            orderListResDtos.add(ordering.fromEntity());
        }
        return orderListResDtos;
    }
}
