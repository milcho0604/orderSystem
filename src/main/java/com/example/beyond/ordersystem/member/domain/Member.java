package com.example.beyond.ordersystem.member.domain;

import com.example.beyond.ordersystem.common.domain.Address;
import com.example.beyond.ordersystem.common.domain.BaseTimeEntity;
import com.example.beyond.ordersystem.member.dto.MemberListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, length = 6)
    private String password;
    @Embedded
    private Address address;
    private String name;
    private Long age;
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('ADMIN', 'USER') DEFAULT 'USER'")
    private Role role;

    public MemberListDto listFromEntity() {
        return MemberListDto.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .build();
    }
}
