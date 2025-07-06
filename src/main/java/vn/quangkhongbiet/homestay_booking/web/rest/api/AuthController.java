package vn.quangkhongbiet.homestay_booking.web.rest.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.RegisterUserDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.ReqLoginDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.UpdateUserDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.VerifyOtpRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.ResLoginDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.ResUserCreateDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;
import vn.quangkhongbiet.homestay_booking.service.email.OtpService;
import vn.quangkhongbiet.homestay_booking.service.user.UserService;
import vn.quangkhongbiet.homestay_booking.utils.SecurityUtil;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.ResUserUpdatedDTO;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final SecurityUtil securityUtil;
    
    private final UserService userService;

    private final OtpService otpService;

    @Value("${security.authentication.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    @PostMapping("/auth/login")
    @Operation(summary = "Đăng nhập", description = "Đăng nhập vào hệ thống và nhận access token, refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Đăng nhập thành công"),
        @ApiResponse(responseCode = "401", description = "Sai thông tin đăng nhập")
    })
    public ResponseEntity<Object> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        log.info("REST request to login Auth: {}", loginDTO);

        // check user is verified
        User user = userService.findUserByEmail(loginDTO.getUserName());
        if (!user.getVerified()) {
            throw new UnauthorizedException("Tài khoản chưa được xác thực", "Authentication", "unverified_account");
        }
        
        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticateUser(loginDTO);

        // 2. Lấy thông tin người dùng
        ResLoginDTO resLoginDTO = buildUserInfoDTO(loginDTO.getUserName());

        // 3. Tạo Access Token
        String accessToken = securityUtil.createAccessToken(authentication.getName(), resLoginDTO);
        resLoginDTO.setAccess_token(accessToken);

        // 4. Tạo Refresh Token và cập nhật DB
        String refreshToken = securityUtil.createFreshToken(loginDTO.getUserName(), resLoginDTO);
        userService.updateUserToken(loginDTO.getUserName(), refreshToken);

        // 5. Tạo HTTP-Only cookie
        ResponseCookie refreshTokenCookie = buildRefreshTokenCookie(refreshToken);

        // 6. Gán Authentication vào context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 7. Trả về response
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(resLoginDTO);
    }

    @GetMapping("/auth/account")
    @ApiMessage("Lấy thông tin tài khoản")
    @Operation(summary = "Lấy thông tin tài khoản", description = "Lấy thông tin tài khoản hiện tại")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy user")
    })
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        log.info("REST request to get Auth account");
        // get information of user from SecurityContext
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;
        if (email == null) {
            throw new EntityNotFoundException("User not found", email, "usernotfound");
        }
        // Get user from DB
        User currentUserDB = this.userService.findUserByEmail(email);
        ResLoginDTO.InformationUser userLogin = new ResLoginDTO().new InformationUser(
                currentUserDB.getId(),
                currentUserDB.getUserName(),
                currentUserDB.getEmail(),
                currentUserDB.getRole());

        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO().new UserGetAccount(userLogin);

        return ResponseEntity.ok(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Lấy lại access token")
    @Operation(summary = "Làm mới access token", description = "Lấy lại access token bằng refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Làm mới thành công"),
        @ApiResponse(responseCode = "401", description = "Refresh token không hợp lệ hoặc hết hạn")
    })
    public ResponseEntity<Object> handleRefreshToken(@CookieValue(name = "refresh_token") String refresh_token) {
        log.info("REST request to refresh Auth token, refresh_token: {}", refresh_token);
        // check refresh token
        Jwt decodedJwt = this.securityUtil.checkValidPrefreshToken(refresh_token);
        String email = decodedJwt.getSubject();

        // check user by email and token
        User currentUser = this.userService.findUserByRefreshTokenAndEmail(refresh_token, email);
        if (currentUser == null) {
            throw new UnauthorizedException("Refresh token không hợp lệ hoặc đã hết hạn!", "Authentication",
                    "invalid_refresh_token");
        }

        // 2. Lấy thông tin người dùng
        ResLoginDTO resLoginDTO = buildUserInfoDTO(currentUser.getEmail());

        // create token
        String access_token = this.securityUtil.createAccessToken(email, resLoginDTO);
        resLoginDTO.setAccess_token(access_token);

        // Create refresh token and update DB
        String new_refresh_token = this.securityUtil.createFreshToken(email, resLoginDTO);
        this.userService.updateUserToken(email, refresh_token);

        // 5. Tạo HTTP-Only cookie
        ResponseCookie refreshTokenCookie = buildRefreshTokenCookie(new_refresh_token);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(resLoginDTO);
    }

    private Authentication authenticateUser(ReqLoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginDTO.getUserName(),
                loginDTO.getPassword());
        return authenticationManagerBuilder.getObject().authenticate(authToken);
    }

    private ResLoginDTO buildUserInfoDTO(String email) {
        User user = userService.findUserByEmail(email);

        if (user == null) {
            throw new EntityNotFoundException("User not found", "User", "usernotfound");
        }

        ResLoginDTO res = new ResLoginDTO();
        ResLoginDTO.InformationUser info = res.new InformationUser(
                user.getId(), user.getUserName(), user.getEmail(), user.getRole());
        res.setUser(info);

        return res;
    }

    private ResponseCookie buildRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(accessTokenExpiration)
                .build();
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Đăng xuất thành công")
    @Operation(summary = "Đăng xuất", description = "Đăng xuất khỏi hệ thống và xóa refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Đăng xuất thành công")
    })
    public ResponseEntity<Void> handleLogout() {
        log.info("REST request to logout Auth");
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;

        // remove refresh token
        this.userService.updateUserToken(email, null);

        // remove cookie
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .build();

    }

    @Transactional
    @PostMapping("/auth/register")
    @Operation(summary = "Đăng ký tài khoản", description = "Đăng ký tài khoản mới cho người dùng")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Đăng ký thành công"),
        @ApiResponse(responseCode = "409", description = "Email đã tồn tại"),
        @ApiResponse(responseCode = "429", description = "Quá nhiều yêu cầu OTP")
    })
    public ResponseEntity<ResUserCreateDTO> register(@Valid @RequestBody RegisterUserDTO register) {
        log.info("REST request to register Auth: {}", register);

        User user = User.builder()
                        .userName(register.getUserName())
                        .email(register.getEmail())
                        .password(register.getPassword())
                        .phoneNumber(register.getPhoneNumber())
                        .fullName(register.getFullName())
                        .gender(register.getGender())
                        .verified(false)
                        .build();
        userService.createUser(user);

        otpService.generateOtp(register);
        log.info("Registration OTP sent to {}", register.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/verify-otp")
    @Operation(summary = "Xác thực OTP", description = "Xác thực OTP cho tài khoản người dùng. Nếu thành công, tài khoản sẽ được đánh dấu đã xác thực.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Xác thực thành công, trả về thông tin user"),
        @ApiResponse(responseCode = "401", description = "Email không tồn tại hoặc null"),
        @ApiResponse(responseCode = "404", description = "OTP không tồn tại hoặc đã hết hạn")
    })
    public ResponseEntity<ResUserUpdatedDTO> verifyOtp(@RequestBody VerifyOtpRequest req) {
        boolean isOtpValid = otpService.validateOTP(req.getEmail(), req.getOtp());
        if (!isOtpValid) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User realUser = this.userService.findUserByEmail(req.getEmail());
        UpdateUserDTO updateUser = UpdateUserDTO.builder()
            .id(realUser.getId())
            .verified(true)
            .role("User")
            .build();
        ResUserUpdatedDTO updatedUser = this.userService.updatePartialUser(updateUser);
        log.info("User account created for {}", req.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedUser);
    }
}
