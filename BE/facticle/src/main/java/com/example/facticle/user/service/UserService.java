package com.example.facticle.user.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.facticle.common.authority.JwtTokenProvider;
import com.example.facticle.common.authority.TokenInfo;
import com.example.facticle.common.authority.TokenValidationResult;
import com.example.facticle.common.dto.CustomUserDetails;
import com.example.facticle.common.exception.ExpiredTokenException;
import com.example.facticle.common.exception.InvalidInputException;
import com.example.facticle.common.exception.InvalidTokenException;
import com.example.facticle.user.dto.LocalLoginRequestDto;
import com.example.facticle.user.dto.LocalSignupRequestDto;
import com.example.facticle.user.dto.UpdateProfileRequestDto;
import com.example.facticle.user.entity.*;
import com.example.facticle.user.repository.RefreshTokenRepository;
import com.example.facticle.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    //S3 설정 및 사진 규격
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String BUCKET_NAME;
    private static final String FOLDER_NAME = "profile-images/";
    private static final String DEFAULT_PROFILE_IMAGE_URL = "https://facticle-profile-images.s3.ap-northeast-2.amazonaws.com/profile-images/default.png"; //기본 프로필 이미지 URL
    private static final List<String> ALLOWED_FILE_TYPES = List.of("image/jpeg", "image/png");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB 제한

    public Long saveUser(LocalSignupRequestDto localSignupRequestDto){
        checkLocalSignupDto(localSignupRequestDto);

        String hashedPassword = passwordEncoder.encode(localSignupRequestDto.getPassword());
        LocalAuth localAuth = new LocalAuth(localSignupRequestDto.getUsername(), hashedPassword);
        User user = User.builder()
                .localAuth(localAuth)
                .nickname(localSignupRequestDto.getNickname())
                .role(UserRole.USER)
                .signupType(SignupType.LOCAL)
                .build();

        userRepository.save(user);

        return user.getUserId();
    }

    @Transactional(readOnly = true)
    private void checkLocalSignupDto(LocalSignupRequestDto localSignupRequestDto) {
        Map<String, String> errors = new HashMap<>();

        //만약 username이 동일한 경우 혹은 nickname이 동일한 경우라면 illegalArgumentException 예외를 발생하는 로직 추가
        if(userRepository.existsByLocalAuthUsername(localSignupRequestDto.getUsername())){
            errors.put("username", "username already exists.");
        }

        if(userRepository.existsByNickname(localSignupRequestDto.getNickname())){
            errors.put("nickname", "nickname already exists.");
        }

        if(!errors.isEmpty()){
            throw new InvalidInputException("Invalid input", errors);
        }
    }

    @Transactional(readOnly = true)
    public boolean checkUsername(String username) {
        return !userRepository.existsByLocalAuthUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean checkNickname(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    /**
     * 최초 로그인 시 spring security 전 과정을 통해 로그인 정보를 검증 => 검증이 완료되면 반환된 Authentication 객체를 통해 token을 생성
     * DB에서 username으로 user를 찾고, 비밀번호를 검증하는 과정은 authenticationManager.authenticate() 과정에서 이루어짐
     */
    public TokenInfo localLogin(LocalLoginRequestDto localLoginRequestDto) {
        //인증을 위한 UsernamePasswordAuthenticationToken을 생성
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(localLoginRequestDto.getUsername(), localLoginRequestDto.getPassword());

        //spring security를 통해 인증 수행 => authenticationManager에게 생성한 토큰 인증을 요구, 인증 완료 후 결과를 Authentication으로 받음
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        //인증받은 Authentication을 통해 token을 발급 받음
        String newAccessToken = jwtTokenProvider.createAccessToken(authenticate);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(authenticate);

        //authenticate를 기반으로 실제 User 획득
        CustomUserDetails userDetails = (CustomUserDetails)authenticate.getPrincipal();
        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        //새로 refresh token을 발급받았으니 기존의 refresh token 중 유효한 token은 모두 revoke
        refreshTokenRepository.revokeAllByUser(user);

        //새로 발급한 refresh token 생성
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .hashedRefreshToken(passwordEncoder.encode(newRefreshToken))
                .isRevoked(false)
                .issuedAt(jwtTokenProvider.getIssuedAt(newRefreshToken))
                .expiresAt(jwtTokenProvider.getExpiresAt(newRefreshToken))
                .build();

        //refresh token 저장
        user.addRefreshToken(refreshToken);
        refreshTokenRepository.save(refreshToken);

        //user의 lastLogin 필드 update
        user.updateLastLogin(LocalDateTime.now());

        return new TokenInfo("Bearer", newAccessToken, newRefreshToken);
    }

    /**
     * 리프레시 토큰 검증 후 문제 없을 시 토큰 재발급(RTR 적용)
     */
    public TokenInfo reCreateToken(String passedRefreshToken) {
        //토큰 유효성 검증
        TokenValidationResult tokenValidationResult = jwtTokenProvider.validateToken(passedRefreshToken);
        if(tokenValidationResult == TokenValidationResult.EXPIRED){
            throw new ExpiredTokenException("Refresh token has expired. Please login again.");
        }else if(tokenValidationResult == TokenValidationResult.INVALID){
            throw new InvalidTokenException("Invalid refresh token.");
        }
        //refresh token이 아닌 경우
        if(!jwtTokenProvider.getTokenType(passedRefreshToken).equals("REFRESH")){
            throw new InvalidTokenException("Invalid refresh token.");
        }

        //적합한 refresh token이라면 user를 찾고 해당 user의 유효한 토큰을 조회
        Long userId = jwtTokenProvider.getUserId(passedRefreshToken);
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidTokenException("user not found by refresh token"));


        RefreshToken storedRefreshToken = refreshTokenRepository.findValidTokenByUser(user)
                .orElseThrow(() -> { //해당 user의 유효한 토큰이 없는 경우. 비정상적인 접근이라 판단, 모든 유효한 토큰을 중지
                    refreshTokenRepository.revokeAllByUser(user); //모든 Refresh Token 무효화
                    return new InvalidTokenException("No valid refresh token found.");
                });

        //요청으로 온 refresh token과 저장되어 있는 유효한 refresh token이 다르면 비정상적인 접근으로 판단
        if(!passwordEncoder.matches(passedRefreshToken, storedRefreshToken.getHashedRefreshToken())){
            refreshTokenRepository.revokeAllByUser(user);
            throw new InvalidTokenException("Refresh token is invalid or revoked.");
        }

        //토큰 생성 및 반환
        CustomUserDetails userDetails = new CustomUserDetails(
                user.getUserId(),
                user.getLocalAuth().getUsername(),
                "",
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(authentication);

        //새로 refresh token을 발급받았으니 기존의 refresh token 중 유효한 token은 모두 revoke
        refreshTokenRepository.revokeAllByUser(user);

        //새로 발급한 refresh token 생성
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .hashedRefreshToken(passwordEncoder.encode(newRefreshToken))
                .isRevoked(false)
                .issuedAt(jwtTokenProvider.getIssuedAt(newRefreshToken))
                .expiresAt(jwtTokenProvider.getExpiresAt(newRefreshToken))
                .build();

        //refresh token 저장
        user.addRefreshToken(refreshToken);
        refreshTokenRepository.save(refreshToken);

        return new TokenInfo("Bearer", newAccessToken, newRefreshToken);
    }

    /**
     * 로그아웃
     */
    public void logout(String refreshToken) {
        //로그 아웃의 경우 해당 토큰에서 userId만 획득가능하면 해당 유저의 모든 refresh token을 revoke하도록 구현
        //만료나 유효성 검사를 하지 않는 이유는 통과하든 안하든 모든 refresh token을 revoke해야 하는 건 똑같기 때문
        try{
            Long userId = jwtTokenProvider.getUserId(refreshToken);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new InvalidTokenException("User not found."));
            refreshTokenRepository.revokeAllByUser(user);
        }catch (Exception e){
            throw new InvalidTokenException("Failed to extract user from token");
        }
    }

    /**
     * 프로필 사진 업로드
     */
    public String uploadProfileImage(Long userId, MultipartFile profileImage) {
        //사진 검증
        if(profileImage == null || profileImage.isEmpty()){
            throw new InvalidInputException("Invalid input", Map.of("profile_image", "profileImage is empty"));
        }
        if(!ALLOWED_FILE_TYPES.contains(profileImage.getContentType())){
            throw new InvalidInputException("Invalid input", Map.of("profile_image", "Invalid profileImage format. Only JPG and PNG are allowed."));
        }
        if (profileImage.getSize() > MAX_FILE_SIZE) {
            throw new InvalidInputException("Invalid input", Map.of("profile_image", "File size exceeds 5MB limit."));
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 기본 프로필 이미지가 아니면 S3에서 삭제
        if (user.getProfileImage() != null && !user.getProfileImage().equals(DEFAULT_PROFILE_IMAGE_URL)) {
            if(user.getProfileImage().contains(BUCKET_NAME)){
                String oldFilePath = FOLDER_NAME +  user.getProfileImage().substring(user.getProfileImage().lastIndexOf("/") + 1);
                if (amazonS3.doesObjectExist(BUCKET_NAME, oldFilePath)) {
                    amazonS3.deleteObject(new DeleteObjectRequest(BUCKET_NAME, oldFilePath));
                }
            }
        }

        //새로운 사진 이름 지정
        String fileExtension = "";
        int lastDotIndex = profileImage.getOriginalFilename().lastIndexOf(".");
        if(lastDotIndex != -1){
            fileExtension = profileImage.getOriginalFilename().substring(lastDotIndex);
        }
        String newFileName = UUID.randomUUID() + "_" + userId + fileExtension;
        String filePath = FOLDER_NAME + newFileName;

        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(profileImage.getContentType());
            objectMetadata.setContentLength(profileImage.getSize());

            //S3에 저장
            amazonS3.putObject(new PutObjectRequest(
                    BUCKET_NAME, filePath, profileImage.getInputStream(), objectMetadata
                )
            );

            //업로드된 이미지 Url
            String imageUrl = amazonS3.getUrl(BUCKET_NAME, filePath).toString();

            // 사용자 프로필 이미지 업데이트
            user.updateProfileImage(imageUrl);
            return imageUrl;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 프로필 이미지 조회
     */
    public String getProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.getProfileImage();
    }

    /**
     * 프로필 이미지 삭제
     */
    public String deleteProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String currentImage = user.getProfileImage();

        if(currentImage != null && !currentImage.contains(DEFAULT_PROFILE_IMAGE_URL)){
            if(currentImage.contains(BUCKET_NAME)){
                String oldFilePath = FOLDER_NAME +  user.getProfileImage().substring(user.getProfileImage().lastIndexOf("/") + 1);
                if (amazonS3.doesObjectExist(BUCKET_NAME, oldFilePath)) {
                    amazonS3.deleteObject(new DeleteObjectRequest(BUCKET_NAME, oldFilePath));
                }
            }
        }

        user.updateProfileImage(DEFAULT_PROFILE_IMAGE_URL);

        return user.getProfileImage();
    }

    /**
     * 회원 정보 조회
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    }

    /**
     * 회원 정보 수정
     */
    public User updateUserProfile(Long userId, UpdateProfileRequestDto updateProfileRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(updateProfileRequestDto.getNickname() != null){
            if (userRepository.existsByNickname(updateProfileRequestDto.getNickname())) {
                throw new InvalidInputException("Invalid input", Map.of("nickname", "nickname already exist."));
            }
            user.updateNickname(updateProfileRequestDto.getNickname());
        }

        if(updateProfileRequestDto.getEmail() != null){
            if(userRepository.existsByEmail(updateProfileRequestDto.getEmail())){
                throw new InvalidInputException("Invalid input", Map.of("email", "Email already exists."));
            }
            user.updateEmail(updateProfileRequestDto.getEmail());
        }

        return user;
    }
}
