package com.example.beyond.ordersystem.member.controller;

import com.example.beyond.ordersystem.common.dto.CommonErrorDto;
import com.example.beyond.ordersystem.common.dto.CommonResDto;
import com.example.beyond.ordersystem.member.domain.Member;
import com.example.beyond.ordersystem.member.dto.MemberListDto;
import com.example.beyond.ordersystem.member.dto.MemberSaveDto;
import com.example.beyond.ordersystem.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/member")
@Transactional(readOnly = true)
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/create")
    public ResponseEntity<Object> memberCreatePost(@Valid @RequestBody MemberSaveDto dto) {
        try {
            Member member = memberService.memberCreate(dto);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "회원가입에 성공하였습니다.", member.getId());
            return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST, e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Object> memberList(Pageable pageable){
        Page<MemberListDto> memberListDtos = memberService.memberList(pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "회원 목록을 조회합니다.", memberListDtos);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}
