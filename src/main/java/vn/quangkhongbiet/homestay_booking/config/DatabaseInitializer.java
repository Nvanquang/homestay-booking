package vn.quangkhongbiet.homestay_booking.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.user.constant.Gender;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.Permission;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.Role;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;
import vn.quangkhongbiet.homestay_booking.repository.PermissionRepository;
import vn.quangkhongbiet.homestay_booking.repository.RoleRepository;
import vn.quangkhongbiet.homestay_booking.repository.UserRepository;

@Service
@RequiredArgsConstructor 
public class DatabaseInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        System.out.println(">>> START INIT DATABASE");

        if (permissionRepository.count() == 0) {
            List<Permission> permissions = new ArrayList<>();

            // Amenity
            permissions.add(new Permission("Create amenity", "/api/v1/amenities", "POST", "AMENITY"));
            permissions.add(new Permission("Get amenity by ID", "/api/v1/amenities/{id}", "GET", "AMENITY"));
            permissions.add(new Permission("Get all amenities", "/api/v1/amenities", "GET", "AMENITY"));
            permissions.add(new Permission("Delete amenity", "/api/v1/amenities/{id}", "DELETE", "AMENITY"));

            // Booking
            permissions.add(new Permission("Create booking", "/api/v1/bookings", "POST", "BOOKING"));
            permissions.add(new Permission("Get booking by ID", "/api/v1/bookings/{id}", "GET", "BOOKING"));
            permissions.add(new Permission("Get booking sattus by ID", "/api/v1/bookings/{id}/status", "GET", "BOOKING"));
            permissions.add(new Permission("Get all bookings", "/api/v1/bookings", "GET", "BOOKING"));
            permissions.add(new Permission("Update booking", "/api/v1/bookings/{id}", "PATCH", "BOOKING"));

            // Homestay
            permissions.add(new Permission("Create homestay", "/api/v1/homestays", "POST", "HOMESTAY"));
            permissions.add(new Permission("Get homestay by ID", "/api/v1/homestays/{id}", "GET", "HOMESTAY"));
            permissions.add(new Permission("Search homestays", "/api/v1/homestays/search", "GET", "HOMESTAY"));
            permissions.add(new Permission("Get all homestays", "/api/v1/homestays", "GET", "HOMESTAY"));
            permissions.add(new Permission("Add amenities to homestay", "/api/v1/homestays/{homestayId}/amenities", "POST", "HOMESTAY"));
            permissions.add(new Permission("Update homestay", "/api/v1/homestays/{id}", "PATCH", "HOMESTAY"));
            permissions.add(new Permission("Delete homestay", "/api/v1/homestays/{id}", "DELETE", "HOMESTAY"));

            // Homestay Image
            permissions.add(new Permission("Upload homestay images", "/api/v1/homestay/{homestayId}/images", "POST", "HOMESTAY_IMAGE"));
            permissions.add(new Permission("Get homestay images by homestayId", "/api/v1/homestay/{homestayId}/images", "GET", "HOMESTAY_IMAGE"));
            permissions.add(new Permission("Delete homestay image", "/api/v1/homestay-images/{id}", "DELETE", "HOMESTAY_IMAGE"));

            // Availability
            permissions.add(new Permission("Create homestay availability", "/api/v1/availabilities", "POST", "AVAILABILITY"));

            // Location
            permissions.add(new Permission("Get location by ID", "/api/v1/locations/{id}", "GET", "LOCATION"));

            // Permission
            permissions.add(new Permission("Create permission", "/api/v1/permissions", "POST", "PERMISSION"));
            permissions.add(new Permission("Get permission by ID", "/api/v1/permissions/{id}", "GET", "PERMISSION"));
            permissions.add(new Permission("Get all permissions", "/api/v1/permissions", "GET", "PERMISSION"));
            permissions.add(new Permission("Update permission", "/api/v1/permissions/{id}", "PATCH", "PERMISSION"));
            permissions.add(new Permission("Delete permission", "/api/v1/permissions/{id}", "DELETE", "PERMISSION"));

            // Role
            permissions.add(new Permission("Create role", "/api/v1/roles", "POST", "ROLE"));
            permissions.add(new Permission("Get role by ID", "/api/v1/roles/{id}", "GET", "ROLE"));
            permissions.add(new Permission("Get all roles", "/api/v1/roles", "GET", "ROLE"));
            permissions.add(new Permission("Update role", "/api/v1/roles/{id}", "PATCH", "ROLE"));
            permissions.add(new Permission("Delete role", "/api/v1/roles/{id}", "DELETE", "ROLE"));

            // User
            permissions.add(new Permission("Create user", "/api/v1/users", "POST", "USER"));
            permissions.add(new Permission("Get user by ID", "/api/v1/users/{id}", "GET", "USER"));
            permissions.add(new Permission("Get all users", "/api/v1/users", "GET", "USER"));
            permissions.add(new Permission("Update user", "/api/v1/users/{id}", "PATCH", "USER"));
            permissions.add(new Permission("Delete user", "/api/v1/users/{id}", "DELETE", "USER"));

            // PaymentTransaction
            permissions.add(new Permission("Get payment by ID", "/api/v1/payments/{id}", "GET", "PAYMENT"));
            permissions.add(new Permission("Get all payments", "/api/v1/payments", "GET", "PAYMENT"));

            permissionRepository.saveAll(permissions);
        }

        if (roleRepository.count() == 0) {
            Role admin = new Role();
            admin.setName("SUPER_ADMIN");
            admin.setDescription("Toàn quyền hệ thống");
            admin.setActive(true);
            admin.setPermissions(permissionRepository.findAll());
            roleRepository.save(admin);
        }

        if (userRepository.count() == 0) {
            User adminUser = new User();
            adminUser.setEmail("admin@gmail.com");
            adminUser.setPassword(passwordEncoder.encode("12345678"));
            adminUser.setUserName("Super Admin");
            adminUser.setGender(Gender.MALE);
            adminUser.setPhoneNumber("+84123456789");
            adminUser.setCreatedAt(java.time.Instant.now());
            adminUser.setCreatedBy("SYSTEM");

            Role adminRole = roleRepository.findByName("SUPER_ADMIN").get();
            adminUser.setRole(adminRole);

            userRepository.save(adminUser);
        }

        System.out.println(">>> END INIT DATABASE");
    }
}