package com.aipark.biz.service;

import com.aipark.biz.domain.member.Member;
import com.aipark.biz.domain.member.MemberRepository;
import com.aipark.exception.MemberErrorResult;
import com.aipark.exception.MemberException;
import com.aipark.web.dto.MailDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final RedisService redisService;
    private final MemberRepository memberRepository;

    private static String ePw = "";
    private final char[] characterTable = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
            't', 'u','v', 'w', 'x', 'y','z','1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };


    public MailDto.AuthKeyResponse sendAuthKey(MailDto.SendAuthKeyRequest sendRequestDto){
        Member member = memberRepository.findByUsername(sendRequestDto.getUsername()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));
        boolean check1 = member.getEmail().equals(sendRequestDto.getEmail());
        boolean check2 = member.getName().equals(sendRequestDto.getName());
        if(!member.getEmail().equals(sendRequestDto.getEmail())
            || !member.getName().equals(sendRequestDto.getName())){
            throw new MemberException(MemberErrorResult.AUTH_FAIL);
        }
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("wkdghwns97@gamil.com");
            helper.setTo(sendRequestDto.getEmail());
            helper.setSubject("매일 인증하세요");
            helper.setText(createAuthMessage(), true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        redisService.setValues(ePw, sendRequestDto.getEmail(), Duration.ofMillis(1000 * 60 * 30));

        String uuid = createRandom(15);
        redisService.setValues(uuid, sendRequestDto.getUsername(), Duration.ofMillis(1000 * 60 * 30));

        return MailDto.AuthKeyResponse.of(uuid);
    }
    public String createRandom(int size){
        Random random = new Random(System.currentTimeMillis());
        int len = characterTable.length;
        StringBuilder sb = new StringBuilder();

        for(int i=0; i < size; i++){
            sb.append(characterTable[random.nextInt(len)]);
        }

        return sb.toString();
    }

    public void verifyEmail(MailDto.VerifyRequest requestDto) {
        Member member = memberRepository.findByEmail(requestDto.getEmail()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));

        String uuid = redisService.getValues(requestDto.getToken());
        if(uuid.equals(member.getUsername())){
            throw new MemberException(MemberErrorResult.AUTH_FAIL);
        }
        String values = redisService.getValues(requestDto.getKey());
        if(!values.equals(requestDto.getEmail())){
            throw new MemberException(MemberErrorResult.AUTH_FAIL);
        }
    }

    public void sendId(MailDto.SendIdRequest sendIdRequestDto) {
        Member member = memberRepository.findByEmail(sendIdRequestDto.getEmail()).orElseThrow(
                () -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));
        if(!member.getName().equals(sendIdRequestDto.getName())){
            throw new MemberException(MemberErrorResult.AUTH_FAIL);
        }
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("wkdghwns97@gamil.com");
            helper.setTo(sendIdRequestDto.getEmail());
            helper.setSubject("아이디 확인하세요");
            helper.setText(createIdMessage(member.getUsername()), true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public String createAuthMessage(){
        ePw = createRandom(8);


        return  "<link rel='preconnect' href='https://fonts.googleapis.com'>"+
                "<link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>"+
                "<link href='https://fonts.googleapis.com/css2?family=Gowun+Dodum&display=swap' rel='stylesheet'>"+
                "<style> * {" +
                "font-family: 'Gowun Dodum', sans-serif;" +
                "</style>" +
                "<div style='margin:100px;'>" +
                "<h1 style='font-family:Gowun Dodum;'> 안녕하세요 </h1>" +
                "<h1 style='font-family:Gowun Dodum;'> AIPark 인증입니다. </h1>" +
                "<br>" +
                "<h1 style='font-family:Gowun Dodum;'> 아래 코드를 비밀번호 찾기 창으로 돌아가 입력해주세요 </h1>" +
                "<br>" +
                "<h1 style='font-family:Gowun Dodum;'> AIPark는 당신의 꿈을 응원합니다. 감사합니다!!</h1>" +
                "<br>" +
                "<div align='center' style='border:1px solid black; font-family:verdana';>" +
                "<h3 style='color:blue;'> 인증 코드 </h3>" +
                "<div style='font-size:130%'>" +
                "<strong>" +
                ePw +
                "</strong><div><br />" +
                "</div>";
    }
    public String createIdMessage(String username){
        return  "<link rel='preconnect' href='https://fonts.googleapis.com'>"+
                "<link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>"+
                "<link href='https://fonts.googleapis.com/css2?family=Gowun+Dodum&display=swap' rel='stylesheet'>"+
                "<style> * {" +
                "font-family: 'Gowun Dodum', sans-serif;" +
                "</style>" +
                "<div style='margin:100px;'>" +
                "<h1 style='font-family:Gowun Dodum;'> 안녕하세요 </h1>" +
                "<h1 style='font-family:Gowun Dodum;'> AIPark 인증입니다. </h1>" +
                "<br>" +
                "<h1 style='font-family:Gowun Dodum;'> 회원 아이디를 보내드립니다. 확인하세요 </h1>" +
                "<br>" +
                "<h1 style='font-family:Gowun Dodum;'> AIPark는 당신의 꿈을 응원합니다. 감사합니다!!</h1>" +
                "<br>" +
                "<div align='center' style='border:1px solid black; font-family:verdana';>" +
                "<h3 style='color:blue;'> 아이디 </h3>" +
                "<div style='font-size:130%'>" +
                "<strong>" +
                username +
                "</strong><div><br />" +
                "</div>";
    }
}

