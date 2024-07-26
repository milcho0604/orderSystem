package com.example.beyond.ordersystem.member.service;

import com.example.beyond.ordersystem.member.domain.Member;
import com.example.beyond.ordersystem.member.dto.MemberListDto;
import com.example.beyond.ordersystem.member.dto.MemberSaveDto;
import com.example.beyond.ordersystem.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
//    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member memberCreate(MemberSaveDto dto) {
//        if (memberRepository.findByEmail(dto.getEmail()).isPresent()) {
//            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
//        }
//        Member member = dto.toEntity(passwordEncoder.encode(dto.getPassword()));
        Member member = memberRepository.save(dto.toEntity());
        return member;
    }

    public Page<MemberListDto> memberList(Pageable pageable) {
        Page<Member> members = memberRepository.findAll(pageable);
        return members.map(a -> a.listFromEntity());
//        Page<Member> members = memberRepository.findAll(pageable);
//        Page<MemberListDto> memberListDtos = members.map(a -> a.listFromEntity());
//        return memberListDtos;
    }


}
