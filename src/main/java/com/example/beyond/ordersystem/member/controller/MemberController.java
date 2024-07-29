package com.example.beyond.ordersystem.member.controller;

import com.example.beyond.ordersystem.common.auth.JwtTokenProvider;
import com.example.beyond.ordersystem.common.dto.CommonErrorDto;
import com.example.beyond.ordersystem.common.dto.CommonResDto;
import com.example.beyond.ordersystem.member.domain.Member;
import com.example.beyond.ordersystem.member.dto.MemberListDto;
import com.example.beyond.ordersystem.member.dto.MemberLoginDto;
import com.example.beyond.ordersystem.member.dto.MemberRefreshDto;
import com.example.beyond.ordersystem.member.dto.MemberSaveDto;
import com.example.beyond.ordersystem.member.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/member")
@Transactional(readOnly = true)
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    @Value("${jwt.secretKey}Rt")
    private String secretKeyRt;

    @Autowired
    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
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

    @PostMapping("/doLogin")
    public ResponseEntity<?> doLogin(@RequestBody MemberLoginDto dto) {
        // email, password 일치하는지 검증
        Member member = memberService.login(dto);
        // 일치할 경우 accessToken 생성
        String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());

        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail(), member.getRole().toString());
        // 생성된 토큰을 CommonResDto 에 담아 사용자에게 return
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", member.getId());
        loginInfo.put("token", jwtToken);
        loginInfo.put("refreshToken", refreshToken);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "로그인에 성공하였습니다.", loginInfo);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // admin 권한만 회원목록전체조회 가능
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<Object> memberList(Pageable pageable) {
        Page<MemberListDto> memberListDtos = memberService.memberList(pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "회원 목록을 조회합니다.", memberListDtos);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 본인은 본인회원정보만 조회 가능
    @GetMapping("/myInfo")
    public ResponseEntity<Object> myInfo() {
        MemberListDto dto = memberService.myInfo();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "마이페이지로 이동합니다.",dto);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> generateNewAccessToken(@RequestBody MemberRefreshDto dto){
        String rt = dto.getRefreshToken();
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(secretKeyRt).parseClaimsJws(rt).getBody(); // -> 이 한줄이 토큰 검증 코드
        }catch (Exception e){
            return new ResponseEntity<>(new CommonErrorDto(HttpStatus.UNAUTHORIZED, "invalid refresh token"),HttpStatus.UNAUTHORIZED);
        }
        String email = claims.getSubject();
        String role = claims.get("role").toString();

        String newAccessToken = jwtTokenProvider.createToken(email, role);
        Map<String, Object> info = new HashMap<>();
        info.put("token", newAccessToken);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "!!!AccessToken is renewed!!!", info);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);

    }
}
