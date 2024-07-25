package com.example.beyond.ordersystem.member.dto;

import com.example.beyond.ordersystem.member.domain.Member;
import com.example.beyond.ordersystem.member.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberSaveDto {

    private String name;
    private String email;
    private String password;
    private String age;
    private String phone;
    private String city;
    private String street;
    private String zipcode;
    private Role role;

    public Member toEntity() {
        return Member.builder()
                .password(this.password)
                .name(this.name)
                .email(this.email)
                .phone(this.phone)
                .role(this.role != null ? this.role : Role.USER)  // 기본값 설정
                .build();
    }
}
