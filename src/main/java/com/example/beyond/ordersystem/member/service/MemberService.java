package com.example.beyond.ordersystem.member.service;

import com.example.beyond.ordersystem.member.domain.Member;
import com.example.beyond.ordersystem.member.dto.MemberListDto;
import com.example.beyond.ordersystem.member.dto.MemberSaveDto;
import com.example.beyond.ordersystem.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
//    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member memberCreate(MemberSaveDto dto) {
//        if (memberRepository.findByEmail(dto.getEmail()).isPresent()) {
//            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
//        }
//        Member member = dto.toEntity(passwordEncoder.encode(dto.getPassword()));
        Member member = dto.toEntity();
        Member savedMember = memberRepository.save(member);
        return savedMember;
    }

    public List<MemberListDto> memberList() {
        List<MemberListDto> MemberResDtoList = new ArrayList<>();
        List<Member> MemberList = memberRepository.findAll();
        for (Member Member : MemberList) {
            MemberResDtoList.add(Member.listFromEntity());
        }
        return MemberResDtoList;
    }


}
