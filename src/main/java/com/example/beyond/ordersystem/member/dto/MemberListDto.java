package com.example.beyond.ordersystem.member.dto;

import com.example.beyond.ordersystem.member.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberListDto {
    private Long id;
    private String email;
    private String name;
    private Long age;
    private String city;
    private String street;
    private String zipcode;
}
