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
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.RegisterUserRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.LoginUserRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.UpdateUserRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.VerifyOtpRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.LoginUserResponse;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.CreateUserResponse;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;
import vn.quangkhongbiet.homestay_booking.service.email.OtpService;
import vn.quangkhongbiet.homestay_booking.service.user.UserService;
import vn.quangkhongbiet.homestay_booking.utils.SecurityUtil;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.UpdateUserResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Auth", description = "Authentication and user account management")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final SecurityUtil securityUtil;
    
    private final UserService userService;

    private final OtpService otpService;

    @Value("${security.authentication.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    @PostMapping("/auth/login")
    @Operation(summary = "Login", description = "Login to the system and receive access token, refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid login information", content = @Content())
    })
    public ResponseEntity<Object> login(@Valid @RequestBody LoginUserRequest loginDTO) {
        log.info("REST request to login Auth: {}", loginDTO);

        // check user is verified
        User user = userService.findUserByEmail(loginDTO.getUserName());
        if (!user.getVerified()) {
            throw new UnauthorizedException("Tài khoản chưa được xác thực", "Authentication", "unverified_account");
        }
        
        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticateUser(loginDTO);

        // Lấy thông tin người dùng
        LoginUserResponse resLoginDTO = buildUserInfoDTO(loginDTO.getUserName());

        // Tạo Access Token
        String accessToken = securityUtil.createAccessToken(authentication.getName(), resLoginDTO);
        resLoginDTO.setAccess_token(accessToken);

        // Tạo Refresh Token và cập nhật DB
        String refreshToken = securityUtil.createFreshToken(loginDTO.getUserName(), resLoginDTO);
        userService.updateUserToken(loginDTO.getUserName(), refreshToken);

        // Tạo HTTP-Only cookie
        ResponseCookie refreshTokenCookie = buildRefreshTokenCookie(refreshToken);

        // Gán Authentication vào context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Trả về response
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(resLoginDTO);
    }

    @GetMapping("/auth/account")
    @ApiMessage("Get account information")
    @Operation(summary = "Get account information", description = "Get current account information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content())
    })
    public ResponseEntity<LoginUserResponse.UserGetAccount> getAccount() {
        log.info("REST request to get Auth account");
        // get information of user from SecurityContext
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;
        if (email == null) {
            throw new EntityNotFoundException("User not found", email, "usernotfound");
        }
        // Get user from DB
        User currentUserDB = this.userService.findUserByEmail(email);
        LoginUserResponse.InformationUser userLogin = new LoginUserResponse().new InformationUser(
                currentUserDB.getId(),
                currentUserDB.getUserName(),
                currentUserDB.getEmail(),
                currentUserDB.getRole());

        LoginUserResponse.UserGetAccount userGetAccount = new LoginUserResponse().new UserGetAccount(userLogin);

        return ResponseEntity.ok(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get new access token")
    @Operation(summary = "Refresh access token", description = "Get new access token using refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refresh successful"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token", content = @Content())
    })
    public ResponseEntity<Object> handleRefreshToken(@CookieValue(name = "refresh_token") String refresh_token) {
        log.info("REST request to refresh Auth token, refresh_token: {}", refresh_token);
        // check refresh token
        Jwt decodedJwt = this.securityUtil.checkValidPrefreshToken(refresh_token);
        String email = decodedJwt.getSubject();

        // check user by email and token
        User currentUser = this.userService.findUserByRefreshTokenAndEmail(refresh_token, email);
        if (currentUser == null) {
            throw new UnauthorizedException("Refresh token is invalid or expired!", "Authentication",
                    "invalid_refresh_token");
        }

        // 2. Lấy thông tin người dùng
        LoginUserResponse resLoginDTO = buildUserInfoDTO(currentUser.getEmail());

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

    private Authentication authenticateUser(LoginUserRequest loginDTO) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginDTO.getUserName(),
                loginDTO.getPassword());
        return authenticationManagerBuilder.getObject().authenticate(authToken);
    }

    private LoginUserResponse buildUserInfoDTO(String email) {
        User user = userService.findUserByEmail(email);

        if (user == null) {
            throw new EntityNotFoundException("User not found", "User", "usernotfound");
        }

        LoginUserResponse res = new LoginUserResponse();
        LoginUserResponse.InformationUser info = res.new InformationUser(
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
    @ApiMessage("Logout successful")
    @Operation(summary = "Logout", description = "Logout from the system and remove refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout successful")
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
    @Operation(summary = "Register account", description = "Register a new user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Registration successful", content = @Content()),
        @ApiResponse(responseCode = "409", description = "Email already exists", content = @Content()),
        @ApiResponse(responseCode = "429", description = "Too many OTP requests", content = @Content())
    })
    public ResponseEntity<CreateUserResponse> register(@Valid @RequestBody RegisterUserRequest register) {
        log.info("REST request to register Auth: {}", register);

        this.userService.registerUser(register);
        this.otpService.generateOtp(register);
        log.info("Registration OTP sent to {}", register.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/verify-otp")
    @Operation(summary = "Verify OTP", description = "Verify OTP for user account. If successful, the account will be marked as verified.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Verification successful, return user information", content = @Content()),
        @ApiResponse(responseCode = "401", description = "Email does not exist or is null", content = @Content()),
        @ApiResponse(responseCode = "404", description = "OTP does not exist or has expired", content = @Content())
    })
    public ResponseEntity<UpdateUserResponse> verifyOtp(@RequestBody VerifyOtpRequest req) {
        boolean isOtpValid = otpService.validateOTP(req.getEmail(), req.getOtp());
        if (!isOtpValid) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User realUser = this.userService.findUserByEmail(req.getEmail());
        UpdateUserRequest updateUser = UpdateUserRequest.builder()
            .id(realUser.getId())
            .verified(true)
            .role("User")
            .build();
        UpdateUserResponse updatedUser = this.userService.updatePartialUser(updateUser);
        log.info("User account created for {}", req.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedUser);
    }
}
