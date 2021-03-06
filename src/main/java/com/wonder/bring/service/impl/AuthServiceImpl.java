package com.wonder.bring.service.impl;

import com.wonder.bring.dto.User;
import com.wonder.bring.mapper.UserMapper;
import com.wonder.bring.model.DefaultRes;
import com.wonder.bring.model.LoginReq;
import com.wonder.bring.service.AuthService;
import com.wonder.bring.utils.Message;
import com.wonder.bring.utils.Status;
import org.springframework.stereotype.Service;

import static com.wonder.bring.utils.Encryption.encrypt;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final JwtServiceImpl jwtServiceImpl;

    public AuthServiceImpl(final UserMapper userMapper, final JwtServiceImpl jwtServiceImpl) {
        this.userMapper = userMapper;
        this.jwtServiceImpl = jwtServiceImpl;
    }

    @Override
    public DefaultRes<JwtServiceImpl.TokenRes> login(final LoginReq loginReq) {
        final User user = userMapper.findByIdAndPassword(loginReq.getId(),
                encrypt(loginReq.getPassword()));

        if (user != null) {
            //토큰 생성
            final JwtServiceImpl.TokenRes tokenDto = new JwtServiceImpl.TokenRes(jwtServiceImpl.create(user.getUserIdx()));

            //user의 fcmToken값 저장
            int userIdx = userMapper.findByUserId(loginReq.getId());
            userMapper.saveFcmToken(loginReq.getFcmToken(), userIdx);

            return DefaultRes.res(Status.OK, Message.LOGIN_SUCCESS, tokenDto);
        }
        return DefaultRes.res(Status.BAD_REQUEST, Message.LOGIN_FAIL);
    }

    @Override
    public DefaultRes<Boolean> checkToken() {
        return DefaultRes.res(Status.OK, "유효한 토큰입니다");
    }

}
