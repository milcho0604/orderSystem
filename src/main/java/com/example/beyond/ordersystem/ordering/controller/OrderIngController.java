package com.example.beyond.ordersystem.ordering.controller;

import com.example.beyond.ordersystem.common.dto.CommonResDto;
import com.example.beyond.ordersystem.ordering.domain.Ordering;
import com.example.beyond.ordersystem.ordering.dto.OrderListResDto;
import com.example.beyond.ordersystem.ordering.dto.OrderSaveReqDto;
import com.example.beyond.ordersystem.ordering.service.OrderingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("order")
@Transactional
public class OrderIngController {
    private final OrderingService orderingService;

    @Autowired
    public OrderIngController(OrderingService orderingService) {
        this.orderingService = orderingService;
    }

    @PostMapping("create")
    public ResponseEntity<?> orderCreate(@RequestBody OrderSaveReqDto dto) {
        Ordering ordering = orderingService.orderCreate(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "주문완료", ordering.getId());
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @GetMapping("list")
    public ResponseEntity<?> orderList() {
        List<OrderListResDto> orderList = orderingService.orderList();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "주문 목록 조회", orderList);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

}
